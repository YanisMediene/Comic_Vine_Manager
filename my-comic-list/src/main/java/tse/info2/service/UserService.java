package tse.info2.service;

import tse.info2.database.*;
import tse.info2.model.*;
import tse.info2.util.ApiClient;

import java.sql.SQLException;
import java.io.IOException;  // Ajout de l'import manquant
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserService {
	 private final VolumeDAO volumeDAO;
	    private final IssueDAO issueDAO;
	    private final UserIssueDAO userIssueDAO;
	    private final AuteurDAO auteurDAO;
	    private final GenreDAO genreDAO;
	    private final PowerDAO powerDAO;
	    private final LocationDAO locationDAO;
	    private final TeamDAO teamDAO;
	    private final ComicObjectDAO comicObjectDAO;
	    private final String apiKey;
	    private final PersonnageDAO personnageDAO;
	    private final StoryArcDAO storyArcDAO;
	    private final ApiClient apiClient;
       
	    public UserService(VolumeDAO volumeDAO, IssueDAO issueDAO, UserIssueDAO userIssueDAO, AuteurDAO auteurDAO,
                GenreDAO genreDAO,  PersonnageDAO personnageDAO, 
                ApiClient apiClient,PowerDAO powerDAO,LocationDAO locationDAO,TeamDAO teamDAO,ComicObjectDAO comicObjectDAO,StoryArcDAO storyArcDAO) {
 this.volumeDAO = volumeDAO;
 this.issueDAO = issueDAO;
 this.userIssueDAO = userIssueDAO;
 this.auteurDAO = auteurDAO;
 this.genreDAO = genreDAO;
 this.powerDAO = powerDAO;
 this.personnageDAO = personnageDAO;
 this.locationDAO = locationDAO;
 this.teamDAO = teamDAO;
 this.apiClient = apiClient;
 this.comicObjectDAO = comicObjectDAO;
 this.storyArcDAO = storyArcDAO;
 this.apiKey = ResourceBundle.getBundle("application").getString("api.key");
	    
}
	
    private DatabaseService databaseService = new DatabaseService();

    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    private final Map<String, Integer> volumeCache = new HashMap<>();


    public boolean addIssueToFavorites(int userId, String issueApiDetailUrl) {
        try {
            Issue completeIssue = apiClient.getCompleteIssueDetails(issueApiDetailUrl);
            int volumeId = findOrSaveVolumeCached(completeIssue.getVolume());
            int issueId = issueDAO.findOrSaveIssue(completeIssue, volumeId);

            CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> {
                    try {
                        completeIssue.getPersonnages().parallelStream().forEach(personnage -> {
                            try {
                                int personnageId = personnageDAO.findOrSavePersonnage(personnage);
                                if (personnage.getPowers() != null) {
                                    powerDAO.findOrSavePowers(personnage.getPowers());
                                    powerDAO.linkPowerToPersonnage(personnageId, personnage.getPowers());
                                }
                                personnageDAO.linkIssueWithPersonnage(issueId, personnageId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }),
                CompletableFuture.runAsync(() -> {
                    completeIssue.getAuteurs().parallelStream().forEach(auteur -> {
                        try {
                            int auteurId = auteurDAO.findOrSaveAuteur(auteur);
                            auteurDAO.linkIssueWithAuteur(issueId, auteurId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }),
                CompletableFuture.runAsync(() -> saveLocationsList(completeIssue.getLocations(), issueId)),
                CompletableFuture.runAsync(() -> saveTeamsList(completeIssue.getTeams(), issueId)),
                CompletableFuture.runAsync(() -> saveGenresList(completeIssue.getGenres(), issueId)),
                CompletableFuture.runAsync(() -> saveComicObjectsList(completeIssue.getObjects(), issueId)),
                CompletableFuture.runAsync(() -> saveStoryArcsList(completeIssue.getStoryArcs(), issueId))
            ).join();

            return userIssueDAO.addIssueToFavorites(userId, completeIssue);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int findOrSaveVolumeCached(Volume volume) throws SQLException {
        if (volume == null) return -1;
        if (volumeCache.containsKey(volume.getName())) {
            return volumeCache.get(volume.getName());
        }
        int volumeId = volumeDAO.findOrSaveVolume(volume);
        volumeCache.put(volume.getName(), volumeId);
        return volumeId;
    }

    private void savePersonnages(JSONArray characterCreditsArray, int issueId) throws IOException, InterruptedException {
        if (characterCreditsArray == null || characterCreditsArray.isEmpty()) return;

        List<Personnage> personnages = apiClient.parseCharacterCredits(characterCreditsArray);

        personnages.parallelStream().forEach(personnage -> {
            try {
                Object[] personnageInfo = apiClient.getPersonnageInfo(personnage.getApi_detail_url());
                personnage.setImage((String) personnageInfo[0]);
                personnage.setPowers((List<Power>) personnageInfo[1]);

                int personnageId = personnageDAO.findOrSavePersonnage(personnage);
                if (personnage.getPowers() != null) {
                    powerDAO.findOrSavePowers(personnage.getPowers());
                    powerDAO.linkPowerToPersonnage(personnageId, personnage.getPowers());
                }
                personnageDAO.linkIssueWithPersonnage(issueId, personnageId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveAuteurs(JSONArray personCreditsArray, int issueId) throws IOException, InterruptedException {
        if (personCreditsArray == null || personCreditsArray.isEmpty()) return;

        List<Auteur> auteurs = apiClient.parsePersonCredits(personCreditsArray);

        auteurs.parallelStream().forEach(auteur -> {
            try {
                int auteurId = auteurDAO.findOrSaveAuteur(auteur);
                auteurDAO.linkIssueWithAuteur(issueId, auteurId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveLocations(JSONArray locationCreditsArray, int issueId) throws IOException, InterruptedException {
        if (locationCreditsArray == null || locationCreditsArray.isEmpty()) return;

        List<Location> locations = apiClient.parseLocationCredits(locationCreditsArray);

        locations.parallelStream().forEach(location -> {
            try {
                int locationId = locationDAO.findOrSaveLocation(location);
                locationDAO.linkIssueWithLocation(issueId, locationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveTeams(JSONArray teamCreditsArray, int issueId) throws IOException, InterruptedException {
        if (teamCreditsArray == null || teamCreditsArray.isEmpty()) return;

        List<Team> teams = apiClient.parseTeamCredits(teamCreditsArray);

        teams.parallelStream().forEach(team -> {
            try {
                int teamId = teamDAO.findOrSaveTeam(team);
                teamDAO.linkIssueWithTeam(issueId, teamId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveGenres(JSONArray conceptCreditsArray, int issueId) throws IOException, InterruptedException {
        if (conceptCreditsArray == null || conceptCreditsArray.isEmpty()) return;

        List<Genre> genres = apiClient.parseConceptCredits(conceptCreditsArray);

        genres.parallelStream().forEach(genre -> {
            try {
                int genreId = genreDAO.findOrSaveGenre(genre);
                genreDAO.linkIssueWithGenre(issueId, genreId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveComicObjects(JSONArray objectCreditsArray, int issueId) throws IOException, InterruptedException {
        if (objectCreditsArray == null || objectCreditsArray.isEmpty()) return;

        List<ComicObject> comicObjects = apiClient.parseObjectCredits(objectCreditsArray);

        comicObjects.parallelStream().forEach(object -> {
            try {
                int objectId = comicObjectDAO.findOrSaveComicObject(object);
                comicObjectDAO.linkIssueWithComicObject(issueId, objectId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveStoryArcs(JSONArray storyArcCreditsArray, int issueId) throws IOException, InterruptedException {
        if (storyArcCreditsArray == null || storyArcCreditsArray.isEmpty()) return;

        List<StoryArc> storyArcs = apiClient.parseStoryArcCredits(storyArcCreditsArray);

        storyArcs.parallelStream().forEach(arc -> {
            try {
                int storyArcId = storyArcDAO.findOrSaveStoryArc(arc);
                storyArcDAO.linkIssueWithStoryArc(issueId, storyArcId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Nouvelles méthodes pour gérer les List<>
    private void saveLocationsList(List<Location> locations, int issueId) {
        if (locations == null || locations.isEmpty()) return;
        locations.parallelStream().forEach(location -> {
            try {
                int locationId = locationDAO.findOrSaveLocation(location);
                locationDAO.linkIssueWithLocation(issueId, locationId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveTeamsList(List<Team> teams, int issueId) {
        if (teams == null || teams.isEmpty()) return;
        teams.parallelStream().forEach(team -> {
            try {
                int teamId = teamDAO.findOrSaveTeam(team);
                teamDAO.linkIssueWithTeam(issueId, teamId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveGenresList(List<Genre> genres, int issueId) {
        if (genres == null || genres.isEmpty()) return;
        genres.parallelStream().forEach(genre -> {
            try {
                int genreId = genreDAO.findOrSaveGenre(genre);
                genreDAO.linkIssueWithGenre(issueId, genreId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveComicObjectsList(List<ComicObject> objects, int issueId) {
        if (objects == null || objects.isEmpty()) return;
        objects.parallelStream().forEach(object -> {
            try {
                int objectId = comicObjectDAO.findOrSaveComicObject(object);
                comicObjectDAO.linkIssueWithComicObject(issueId, objectId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void saveStoryArcsList(List<StoryArc> storyArcs, int issueId) {
        if (storyArcs == null || storyArcs.isEmpty()) return;
        storyArcs.parallelStream().forEach(arc -> {
            try {
                int storyArcId = storyArcDAO.findOrSaveStoryArc(arc);
                storyArcDAO.linkIssueWithStoryArc(issueId, storyArcId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
