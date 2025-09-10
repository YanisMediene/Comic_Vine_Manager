package tse.info2.util;

import tse.info2.model.Auteur;

import tse.info2.model.ComicObject;
import tse.info2.model.Genre;
import tse.info2.model.Issue;
import tse.info2.model.Personnage;
import tse.info2.model.Power;
import tse.info2.model.StoryArc;
import tse.info2.model.Team;
import tse.info2.model.Volume;
import tse.info2.model.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiClient {
    private String apiKey;
    private String baseUrl;
    private String searchIssuesUrl;
    private String searchCharactersUrl;
    private String comicDetailsUrl;

    public ApiClient() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return;
            }
            prop.load(input);
            this.apiKey = prop.getProperty("api.key");
            this.baseUrl = prop.getProperty("api.base.url");
            this.searchIssuesUrl = prop.getProperty("api.search.issues.url");
            this.searchCharactersUrl = prop.getProperty("api.search.characters.url");
            this.comicDetailsUrl = prop.getProperty("api.comic.details.url");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<Issue> searchIssues(String query) throws IOException, InterruptedException {
        String urlString = searchIssuesUrl + "?api_key=" + apiKey + "&format=json&limit=1&filter=name:" + query;
        //System.out.println("searchIssues URL: " + urlString);  // Debug URL
        String response = sendRequest(urlString);
        return parseIssues(response);
    }

    public List<Personnage> searchPersonnages(String query) throws IOException, InterruptedException {
        String urlString = searchCharactersUrl + "?api_key=" + apiKey + "&format=json&limit=1&filter=name:" + query;
        //System.out.println("searchPersonnages URL: " + urlString);  // Debug URL
        String response = sendRequest(urlString);
        return parsePersonnages(response);
    }

    public Personnage getPersonnagesDetails(Personnage personnage) throws IOException, InterruptedException {
        String urlString = personnage.getApi_detail_url() + "?api_key=" + apiKey + "&format=json";
        //System.out.println("getPersonnagesDetails URL: " + urlString);  // Debug URL
        String personnageDetailResponse = sendRequest(urlString);

        JSONObject jsonResponse = new JSONObject(personnageDetailResponse);
        JSONObject result = jsonResponse.getJSONObject("results");

        Object[] personnageInfo = getPersonnageInfo(personnage.getApi_detail_url());
        String image = (String) personnageInfo[0];
        List<Power> powers = (List<Power>) personnageInfo[1];
        List<Issue> appearances = (List<Issue>) personnageInfo[2];

        personnage.setImage(image);
        personnage.setPowers(powers);
        personnage.setAppearances(appearances);

        return personnage;
    }

    private List<Issue> parseIssues(String response) {
        List<Issue> issues = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String api_detail_url = result.get("api_detail_url").toString(); // Convertir l'id en chaîne de caractères
            String id = result.get("id").toString(); // Convertir l'id en chaîne de caractères
            String image = result.getJSONObject("image").getString("original_url");
            String name = result.getString("name");
            String issueNumber = result.optString("issue_number", "N/A");
            String coverDate = result.optString("cover_date", "Unknown");
            String description = result.optString("description", "Unknown");
            issues.add(new Issue(api_detail_url, id, image, name, issueNumber, description, coverDate));
        }

        return issues;
    }

    private List<Personnage> parsePersonnages(String response) {
        List<Personnage> personnages = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray results = jsonResponse.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {
            JSONObject result = results.getJSONObject(i);
            String api_detail_url = result.get("api_detail_url").toString(); // Convertir l'id en chaîne de caractères
            String id = result.get("id").toString(); // Convertir l'id en chaîne de caractères
            String image = result.getJSONObject("image").getString("original_url");
            String name = result.getString("name");
            String description = result.optString("deck", "No description available"); // Récupérer la description
            Personnage personnage = new Personnage(api_detail_url, image, name);
            personnage.setDescription(description); // Définir la description
            personnages.add(personnage);
        }

        return personnages;
    }

    public Issue getIssuesDetails(Issue issue) throws IOException, InterruptedException {
        String fields = "id,name,issue_number,description,cover_date,image,volume";
        String urlString = issue.getApi_detail_url() + "?api_key=" + apiKey + "&format=json&field_list=" + fields;
        System.out.println("Fetching issue details from URL: " + urlString);

        // Send the request and fetch the response
        String issueDetailResponse = sendRequest(urlString);

        // Debugging: Log the raw API response
        System.out.println("Raw API Response: " + issueDetailResponse);

        JSONObject jsonResponse = new JSONObject(issueDetailResponse);
        JSONObject result = jsonResponse.getJSONObject("results");

        // Extract and parse fields
        String id = result.optString("id", null);
        String name = result.optString("name", "Unknown Title");
        String issueNumber = result.optString("issue_number", null);
        String description = result.optString("description", "N/A");
        String coverDate = result.optString("cover_date", "N/A");
        String image = result.has("image") ? result.getJSONObject("image").getString("original_url") : null;

        // Set the parsed data to the Issue object
        issue.setId(id);
        issue.setName(name);
        issue.setIssueNumber(issueNumber);
        issue.setDescription(description);
        issue.setCoverDate(coverDate);
        issue.setImage(image);

        // Parse the associated Volume
        if (result.has("volume")) {
            String volumeApiDetailUrl = result.getJSONObject("volume").getString("api_detail_url");
            Volume volume = getVolumeDetails(volumeApiDetailUrl);
            issue.setVolume(volume);
        }

        return issue;
    }


    private Volume getVolumeDetails(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json";
        //System.out.println("getVolumeDetails URL: " + urlString);  // Debug URL
        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        String name = result.getString("name");
        String description = result.optString("description", "N/A");
        String publisher = result.getJSONObject("publisher").getString("name");
        JSONArray issuesArray = result.getJSONArray("issues");
        List<Issue> issues = new ArrayList<>();
        for (int i = 0; i < issuesArray.length(); i++) {
            JSONObject issueObj = issuesArray.getJSONObject(i);
            String issueName = issueObj.getString("name");
            int issueId = issueObj.getInt("id");
            String issueNumber = issueObj.getString("issue_number");
            String imageUrl = issueObj.has("image") ? issueObj.getJSONObject("image").getString("original_url") : null;
            issues.add(new Issue(issueObj.getString("api_detail_url"), String.valueOf(issueId), imageUrl, issueName, issueNumber));
        }

        return new Volume(apiDetailUrl, result.getInt("id"), name, description, publisher, issues);
    }

    public JSONObject getIssueFullDetails(String issueApiDetailUrl) throws IOException, InterruptedException {
        String fields = "person_credits,character_credits,location_credits,team_credits,concept_credits,object_credits,story_arc_credits";
        String urlString = issueApiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=" + fields;
        String response = sendRequest(urlString);
        return new JSONObject(response).getJSONObject("results");
    }

    public String sendRequest(String urlString) throws IOException, InterruptedException {
        int maxRetries = 3;
        int retryDelay = 1000; // 1 seconde
        String lastError = null;
        
        for (int i = 0; i < maxRetries; i++) {
            try {
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlString))
                        .header("User-Agent", "Mozilla/5.0")
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 503) {
                    lastError = String.format(
                        "\nRetry attempt %d/%d for URL: %s" +
                        "\nStatus: 503 Service Unavailable" +
                        "\nResponse headers: %s",
                        (i + 1), maxRetries,
                        urlString,
                        response.headers().map()
                    );
                    System.out.println(lastError);
                    Thread.sleep(retryDelay * (i + 1));
                    continue;
                }

                if (response.statusCode() != 200) {
                    lastError = String.format(
                        "\nRequest failed with status %d" +
                        "\nURL: %s" +
                        "\nResponse headers: %s" +
                        "\nResponse body: %s",
                        response.statusCode(),
                        urlString,
                        response.headers().map(),
                        response.body()
                    );
                    throw new RuntimeException(lastError);
                }
                
                return response.body();
            } catch (Exception e) {
                lastError = String.format(
                    "\nRetry attempt %d/%d failed" +
                    "\nURL: %s" +
                    "\nError: %s",
                    (i + 1), maxRetries,
                    urlString,
                    e.getMessage()
                );
                System.out.println(lastError);
                
                if (i == maxRetries - 1) {
                    throw new RuntimeException("Failed after " + maxRetries + " retry attempts. Last error: " + lastError);
                }
                Thread.sleep(retryDelay * (i + 1));
            }
        }
        
        throw new RuntimeException("Failed after " + maxRetries + " retry attempts. Last error: " + lastError);
    }

    public List<Auteur> parsePersonCredits(JSONArray personCreditsArray) throws IOException, InterruptedException {
        List<Auteur> auteurs = new ArrayList<>();
        for (int i = 0; i < personCreditsArray.length(); i++) {
            JSONObject result = personCreditsArray.getJSONObject(i);
            String api_detail_url = result.getString("api_detail_url");
            int id = result.getInt("id");
            String name = result.getString("name");
            String role = result.getString("role");
            
            String image = getAuteurImage(api_detail_url);
            Auteur auteur = new Auteur(id, name, api_detail_url, role, image); // Ensure your constructor supports the 'id'
            auteurs.add(auteur);
        }
        return auteurs;
    }

    private String getAuteurImage(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=image";
        //System.out.println("getAuteurImage URL: " + urlString);  // Debug URL

        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        return result.has("image") ? result.getJSONObject("image").getString("original_url") : null;
    }

    public List<Personnage> parseCharacterCredits(JSONArray characterCreditsArray) throws IOException, InterruptedException {
        List<Personnage> personnages = new ArrayList<>();
        for (int i = 0; i < characterCreditsArray.length(); i++) {
            JSONObject result = characterCreditsArray.getJSONObject(i);
            int id = result.getInt("id"); // Extract the ID
            String api_detail_url = result.getString("api_detail_url");
            String name = result.getString("name");

            // Fetch additional info like image, powers, and appearances
            Object[] personnageInfo = getPersonnageInfo(api_detail_url);
            String image = (String) personnageInfo[0];
            List<Power> powers = (List<Power>) personnageInfo[1];
            List<Issue> appearances = (List<Issue>) personnageInfo[2];

            // Create a new Personnage object with id
            Personnage personnage = new Personnage(id, name, image, api_detail_url);
            personnage.setId(id); // Store the ID
            personnage.setPowers(powers);
            personnage.setAppearances(appearances);

            // Add to the list
            personnages.add(personnage);

            System.out.println("Parsed Personnage - ID: " + id + ", Name: " + name);
        }
        return personnages;
    }


    public Object[] getPersonnageInfo(String apiDetailUrl) throws IOException, InterruptedException {
        String fields = "image,powers,issue_credits";
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=" + fields;
        
        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");
        String image = result.has("image") ? result.getJSONObject("image").getString("original_url") : null;

        // Utiliser un JSONTokener pour un traitement plus efficace
        List<Power> powers = new ArrayList<>();
        if (result.has("powers")) {
            JSONArray powersArray = result.getJSONArray("powers");
            int count = 0;
            for (Object obj : powersArray) {
                if (count >= 10) break; // Limite à 10 pouvoirs
                JSONObject powerObj = (JSONObject) obj;
                int powerId = powerObj.getInt("id");
                String powerApiDetailUrl = powerObj.getString("api_detail_url");
                String powerName = powerObj.getString("name");
                powers.add(new Power(powerId, powerApiDetailUrl, powerName));
                count++;
            }
        }

        // Utiliser un itérateur pour le traitement des issues
        List<Issue> appearances = new ArrayList<>();
        if (result.has("issue_credits")) {
            JSONArray appearancesArray = result.getJSONArray("issue_credits");
            int totalIssues = appearancesArray.length();
            System.out.println("Total issues disponibles : " + totalIssues);
            
            // Prendre les 10 dernières issues (plus récentes généralement)
            int startIndex = Math.max(0, totalIssues - 10);
            for (int i = startIndex; i < totalIssues; i++) {
                JSONObject issueObj = appearancesArray.getJSONObject(i);
                String api_detail_url = issueObj.getString("api_detail_url");
                String id = String.valueOf(issueObj.getInt("id"));
                String name = issueObj.optString("name", "N/A");
                appearances.add(new Issue(api_detail_url, id, null, name, null));
            }
            System.out.println("Issues traitées : " + appearances.size() + " sur " + totalIssues);
        }

        return new Object[]{image, powers, appearances};
    }

    public List<Team> parseTeamCredits(JSONArray teamCreditsArray) throws IOException, InterruptedException {
        List<Team> teams = new ArrayList<>();
        for (int i = 0; i < teamCreditsArray.length(); i++) {
            JSONObject result = teamCreditsArray.getJSONObject(i);
            int id = result.getInt("id");
            String api_detail_url = result.getString("api_detail_url");
            String name = result.getString("name");
            
            String image = getTeamImage(api_detail_url);
            teams.add(new Team(id, api_detail_url, name, image));
        }
        return teams;
    }

    private String getTeamImage(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=image";
        //System.out.println("getTeamImage URL: " + urlString);  // Debug URL

        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        return result.has("image") ? result.getJSONObject("image").getString("original_url") : null;
    }

    public List<Location> parseLocationCredits(JSONArray locationCreditsArray) throws IOException, InterruptedException {
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < locationCreditsArray.length(); i++) {
            JSONObject result = locationCreditsArray.getJSONObject(i);
            int id = result.getInt("id");
            String api_detail_url = result.getString("api_detail_url");
            String name = result.getString("name");
            
            
            String image = getLocationImage(api_detail_url);
            locations.add(new Location(id, api_detail_url, name, image));
        }
        return locations;
    }

    private String getLocationImage(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=image";
        //System.out.println("getLocationImage URL: " + urlString);  // Debug URL

        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        return result.has("image") ? result.getJSONObject("image").getString("original_url") : null;
    }

    public List<Genre> parseConceptCredits(JSONArray conceptCreditsArray) throws IOException, InterruptedException {
        List<Genre> concepts = new ArrayList<>();
        for (int i = 0; i < conceptCreditsArray.length(); i++) {
            JSONObject result = conceptCreditsArray.getJSONObject(i);
            int id = result.getInt("id");
            String api_detail_url = result.getString("api_detail_url");
            String name = result.getString("name");
            
            String image = getConceptImage(api_detail_url);
            concepts.add(new Genre(id, api_detail_url, image, name));
            
        }
        return concepts;
    }

    private String getConceptImage(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=image";
        //System.out.println("getConceptImage URL: " + urlString);  // Debug URL

        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        return result.has("image") ? result.getJSONObject("image").getString("original_url") : null;
    }

    public List<ComicObject> parseObjectCredits(JSONArray objectCreditsArray) throws IOException, InterruptedException {
        List<ComicObject> objects = new ArrayList<>();
        for (int i = 0; i < objectCreditsArray.length(); i++) {
            JSONObject result = objectCreditsArray.getJSONObject(i);
            int id = result.getInt("id");
            String api_detail_url = result.getString("api_detail_url");
            String name = result.getString("name");
            
            String image = getObjectImage(api_detail_url);
            objects.add(new ComicObject(id, api_detail_url, image, name));
        }
        return objects;
    }

    private String getObjectImage(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=image";
        //System.out.println("getObjectImage URL: " + urlString);  // Debug URL

        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        return result.has("image") ? result.getJSONObject("image").getString("original_url") : null;
    }

    public List<StoryArc> parseStoryArcCredits(JSONArray storyArcCreditsArray) throws IOException, InterruptedException {
        List<StoryArc> storyArcs = new ArrayList<>();
        for (int i = 0; i < storyArcCreditsArray.length(); i++) {
            JSONObject result = storyArcCreditsArray.getJSONObject(i);
            int id = result.getInt("id");
            String api_detail_url = result.getString("api_detail_url");
            String name = result.getString("name");
            
            String image = getStoryArcImage(api_detail_url);
            storyArcs.add(new StoryArc(id,api_detail_url, image, name));
        }
        return storyArcs;
    }

    private String getStoryArcImage(String apiDetailUrl) throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json&field_list=image";
        //System.out.println("getStoryArcImage URL: " + urlString);  // Debug URL
        
        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        return result.has("image") ? result.getJSONObject("image").getString("original_url") : null;
    }

    public List<Issue> getIssuesFromVolume(Volume volume, int limit) throws IOException, InterruptedException {
        String issuesUrlString = "https://comicvine.gamespot.com/api/issues/?api_key=" + apiKey 
                + "&format=json&filter=volume:" + volume.getId();
        
        System.out.println("Fetching issues for Volume ID: " + volume.getId());
        String response = sendRequest(issuesUrlString);
        
        List<Issue> issues = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray issuesArray = jsonResponse.getJSONArray("results");
        
        int fetchLimit = Math.min(issuesArray.length(), limit);
        
        for (int i = 0; i < fetchLimit; i++) {
            JSONObject issueObj = issuesArray.getJSONObject(i);
            
            String apiDetailUrl = issueObj.optString("api_detail_url");
            String issueId = issueObj.optString("id");
            String name = issueObj.optString("name", "Unnamed Issue");
            String issueNumber = issueObj.optString("issue_number", "Unknown");
            String description = issueObj.optString("description", "No description available");
            String coverDate = issueObj.optString("cover_date", "Unknown");
            String image = issueObj.has("image") 
                ? issueObj.getJSONObject("image").optString("original_url", "No Image") 
                : "No Image";
            
            Issue issue = new Issue(apiDetailUrl, issueId, image, name, issueNumber, description, coverDate);
            issue.setVolume(volume);
            issues.add(issue);
        }
        
        return issues;
    }

    public Volume getVolumeWithNextIssues(String apiDetailUrl, int lastConsecutiveIssueNumber) 
            throws IOException, InterruptedException {
        String urlString = apiDetailUrl + "?api_key=" + apiKey + "&format=json";
        System.out.println("Fetching volume details from: " + urlString); // Debug log
        
        String response = sendRequest(urlString);
        JSONObject result = new JSONObject(response).getJSONObject("results");

        String name = result.optString("name", "N/A");
        String description = result.optString("description", "N/A");
        String publisher = result.optJSONObject("publisher") != null
                ? result.optJSONObject("publisher").optString("name", "N/A")
                : "N/A";
        
        String volumeId = result.optString("id");
        List<Issue> nextIssues = new ArrayList<>();

        // Récupérer toutes les issues et les trier
        String issuesUrlString = "https://comicvine.gamespot.com/api/issues/?api_key=" + apiKey 
                + "&format=json&filter=volume:" + volumeId;
        
        String issuesResponse = sendRequest(issuesUrlString);
        JSONObject issuesJson = new JSONObject(issuesResponse);
        JSONArray issuesArray = issuesJson.getJSONArray("results");

        // Créer une liste triée des issues valides
        List<JSONObject> validIssues = new ArrayList<>();
        for (int i = 0; i < issuesArray.length(); i++) {
            JSONObject issueObj = issuesArray.getJSONObject(i);
            int currentIssueNumber = issueObj.optInt("issue_number", -1);
            if (currentIssueNumber > 0) { // Ignorer les numéros invalides
                validIssues.add(issueObj);
            }
        }

        // Trier les issues par numéro
        Collections.sort(validIssues, (a, b) -> {
            int numA = a.optInt("issue_number", 0);
            int numB = b.optInt("issue_number", 0);
            return Integer.compare(numA, numB);
        });

        // Trouver les 5 prochaines issues consécutives
        for (JSONObject issueObj : validIssues) {
            int currentIssueNumber = issueObj.optInt("issue_number", 0);
            if (currentIssueNumber == lastConsecutiveIssueNumber + 1) {
                String issueApiUrl = issueObj.optString("api_detail_url");
                String issueId = issueObj.optString("id");
                String issueName = issueObj.optString("name", "Unnamed Issue");
                String issueDescription = issueObj.optString("description", "No description available");
                String issueCoverDate = issueObj.optString("cover_date", "Unknown");
                String issueImage = issueObj.has("image") 
                    ? issueObj.getJSONObject("image").optString("original_url", "No Image") 
                    : "No Image";
                
                Issue issue = new Issue(issueApiUrl, issueId, issueImage, issueName, 
                    String.valueOf(currentIssueNumber), issueDescription, issueCoverDate);
                nextIssues.add(issue);
                lastConsecutiveIssueNumber = currentIssueNumber;

                if (nextIssues.size() >= 5) break;
            }
        }

        System.out.println("Found " + nextIssues.size() + " next consecutive issues to read");
        return new Volume(apiDetailUrl, result.optInt("id", 0), 
                        name, description, publisher, nextIssues);
    }

    public List<Issue> getLatestIssues(int limit) throws IOException, InterruptedException {
        String urlString = "https://comicvine.gamespot.com/api/issues/?api_key=" + apiKey 
                + "&format=json&sort=issue_number:desc,date_last_updated:desc&filter=has_staff_review:true&limit=" + (limit * 20); // Demander plus d'issues pour compenser les filtres
        
        String response = sendRequest(urlString);
        List<Issue> latestIssues = new ArrayList<>();
        
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray results = jsonResponse.getJSONArray("results");
        
        List<JSONObject> validIssues = new ArrayList<>();
        
        // Filtrer d'abord les issues valides
        for (int i = 0; i < results.length(); i++) {
            JSONObject issueObj = results.getJSONObject(i);
            
            // Vérifier que tous les champs requis sont présents et non null
            if (issueObj.has("api_detail_url") && 
                issueObj.has("id") && 
                issueObj.has("image") && 
                issueObj.getJSONObject("image").has("original_url") &&
                issueObj.has("name") && 
                issueObj.has("issue_number") &&
                issueObj.has("description") &&
                issueObj.has("cover_date") &&
                !issueObj.isNull("api_detail_url") &&
                !issueObj.isNull("id") &&
                !issueObj.isNull("name") &&
                !issueObj.isNull("issue_number") &&
                !issueObj.getJSONObject("image").isNull("original_url")) {
                
                validIssues.add(issueObj);
            }
        }
        
        // Trier les issues valides
        Collections.sort(validIssues, (a, b) -> {
            int numA = a.optInt("issue_number", 0);
            int numB = b.optInt("issue_number", 0);
            if (numA == numB) {
                String dateA = a.optString("date_added", "");
                String dateB = b.optString("date_added", "");
                return dateB.compareTo(dateA);
            }
            return Integer.compare(numB, numA);
        });
        
        // Prendre les n premières issues valides
        for (int i = 0; i < Math.min(validIssues.size(), limit); i++) {
            JSONObject issueObj = validIssues.get(i);
            
            String apiDetailUrl = issueObj.getString("api_detail_url");
            // Convertir l'ID en String car il est retourné comme un entier
            String issueId = String.valueOf(issueObj.getInt("id"));
            String name = issueObj.getString("name");
            String issueNumber = issueObj.getString("issue_number");
            String description = issueObj.optString("description", "No description available");
            String coverDate = issueObj.optString("cover_date", "Release date not available");
            String image = issueObj.getJSONObject("image").getString("original_url");
            
            Issue issue = new Issue(
                apiDetailUrl,
                issueId,
                image,
                name.isEmpty() ? "Unnamed Issue" : name,
                issueNumber.isEmpty() ? "0" : issueNumber,
                description.isEmpty() ? "No description available" : description,
                coverDate.isEmpty() ? "Release date not available" : coverDate
            );
            
            latestIssues.add(issue);
        }
        
        System.out.println("Found " + latestIssues.size() + " valid latest issues");
        return latestIssues;
    }

    public List<Issue> searchIssuesWithLimit(String query, int limit) throws IOException, InterruptedException {
        String urlString = searchIssuesUrl + "?api_key=" + apiKey + "&format=json&limit=" + limit + "&filter=name:" + query;
        //System.out.println("searchIssues URL: " + urlString);  // Debug URL
        String response = sendRequest(urlString);
        return parseIssues(response);
    }

    public List<Personnage> searchPersonnagesWithLimit(String query, int limit) throws IOException, InterruptedException {
        String urlString = searchCharactersUrl + "?api_key=" + apiKey + "&format=json&limit=" + limit + "&filter=name:" + query;
        System.out.println("searchPersonnages URL: " + urlString);  // Debug URL
        String response = sendRequest(urlString);
        return parsePersonnages(response);
    }

    public Issue getCompleteIssueDetails(String issueApiDetailUrl) throws IOException, InterruptedException {
        // Récupérer d'abord les infos de base de l'issue
        Issue issue = getIssuesDetails(new Issue(issueApiDetailUrl, null, null, null, null));
        System.out.println("Base issue details fetched for: " + issue.getName());

        // Récupérer les détails complets
        JSONObject issueDetails = getIssueFullDetails(issueApiDetailUrl);

        // Extraction des sections
        JSONArray personCreditsArray = issueDetails.optJSONArray("person_credits");
        JSONArray characterCreditsArray = issueDetails.optJSONArray("character_credits");
        JSONArray locationCreditsArray = issueDetails.optJSONArray("location_credits");
        JSONArray teamCreditsArray = issueDetails.optJSONArray("team_credits");
        JSONArray conceptCreditsArray = issueDetails.optJSONArray("concept_credits");
        JSONArray objectCreditsArray = issueDetails.optJSONArray("object_credits");
        JSONArray storyArcCreditsArray = issueDetails.optJSONArray("story_arc_credits");

        // Parsing parallèle des différentes sections
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Auteurs
        if (personCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<Auteur> auteurs = parsePersonCredits(personCreditsArray);
                    issue.setAuteurs(auteurs);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing authors: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Personnages
        if (characterCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<Personnage> personnages = parseCharacterCredits(characterCreditsArray);
                    issue.setPersonnages(personnages);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing characters: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Lieux
        if (locationCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<Location> locations = parseLocationCredits(locationCreditsArray);
                    issue.setLocations(locations);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing locations: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Teams
        if (teamCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<Team> teams = parseTeamCredits(teamCreditsArray);
                    issue.setTeams(teams);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing teams: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Genres/Concepts
        if (conceptCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<Genre> genres = parseConceptCredits(conceptCreditsArray);
                    issue.setGenres(genres);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing genres: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Objets
        if (objectCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<ComicObject> objects = parseObjectCredits(objectCreditsArray);
                    issue.setObjects(objects);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing objects: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Story Arcs
        if (storyArcCreditsArray != null) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    List<StoryArc> storyArcs = parseStoryArcCredits(storyArcCreditsArray);
                    issue.setStoryArcs(storyArcs);
                    return true;
                } catch (Exception e) {
                    System.err.println("Error parsing story arcs: " + e.getMessage());
                    return false;
                }
            }));
        }

        // Attendre que toutes les futures soient terminées
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        System.out.println("All issue details fetched successfully");
        return issue;
    }

}
