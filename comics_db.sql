CREATE DATABASE comics_db;

USE comics_db;

-- Create the users table
CREATE TABLE users (
    idUser INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Create the Volume table
CREATE TABLE Volume (
    idVolume INT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    api_detail_url VARCHAR(255) -- Column for storing the API detail URL
);

-- Create the Issue table
CREATE TABLE Issue (
    idIssue INT PRIMARY KEY,
    idVolume INT NOT NULL,
    numero INT NOT NULL,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    datePublication DATE,
    imageCouverture VARCHAR(255),
    api_detail_url VARCHAR(255), -- Column for storing the API detail URL
    FOREIGN KEY (idVolume) REFERENCES Volume(idVolume) ON DELETE CASCADE
);

-- Create the UserIssue table
CREATE TABLE UserIssue (
    idUser INT NOT NULL,               -- Foreign key referencing the user ID
    idIssue INT NOT NULL,              -- Foreign key referencing the issue ID
    Favoris VARCHAR(10),               -- Indicates if the issue is marked as a favorite (e.g., 'YES' or 'NO')
    Avancement VARCHAR(50),            -- Indicates the progress of the issue (e.g., 'Not Started', 'In Progress')
    Acheter VARCHAR(10),               -- Indicates if the issue is purchased (e.g., 'YES' or 'NO')
    PRIMARY KEY (idUser, idIssue),     -- Composite primary key to ensure no duplicates
    FOREIGN KEY (idUser) REFERENCES users(idUser) ON DELETE CASCADE,  -- Foreign key referencing the users table
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE -- Foreign key referencing the Issue table
);


-- Create the Personnage table
CREATE TABLE Personnage (
    idPersonnage INT PRIMARY KEY ,
    nom VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    api_detail_url VARCHAR(255) 
);


-- Create the IssuePersonnage table
CREATE TABLE IssuePersonnage (
    idIssue INT NOT NULL,
    idPersonnage INT NOT NULL,
    PRIMARY KEY (idIssue, idPersonnage),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idPersonnage) REFERENCES Personnage(idPersonnage) ON DELETE CASCADE
);

-- Table: Power
CREATE TABLE Power (
    idPower INT PRIMARY KEY,
    api_detail_url VARCHAR(255),
    nom VARCHAR(255)
);

-- Table: PersoPower (link table for Personnage and Power)
CREATE TABLE PersoPower (
    idPersonnage INT,
    idPower INT,
    PRIMARY KEY (idPersonnage, idPower),
    FOREIGN KEY (idPersonnage) REFERENCES Personnage(idPersonnage) ON DELETE CASCADE,
    FOREIGN KEY (idPower) REFERENCES Power(idPower) ON DELETE CASCADE
);

-- Table: Location
CREATE TABLE Location (
    idLocation INT PRIMARY KEY,
    api_detail_url VARCHAR(255),
    name VARCHAR(255),
    image VARCHAR(255)
);

-- Table: IssueLocation (link table for Issue and Location)
CREATE TABLE IssueLocation (
    idIssue INT,
    idLocation INT,
    PRIMARY KEY (idIssue, idLocation),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idLocation) REFERENCES Location(idLocation) ON DELETE CASCADE
);

-- Table: Genre
CREATE TABLE Genre (
    idGenre INT PRIMARY KEY,
    api_detail_url VARCHAR(255),
    image VARCHAR(255),
    name VARCHAR(255)
);

-- Table: IssueGenre (link table for Issue and Genre)
CREATE TABLE IssueGenre (
    idIssue INT,
    idGenre INT,
    PRIMARY KEY (idIssue, idGenre),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idGenre) REFERENCES Genre(idGenre) ON DELETE CASCADE
);

-- Table: ComicObject
CREATE TABLE ComicObject (
    idComicObject INT PRIMARY KEY,
    api_detail_url VARCHAR(255),
    image VARCHAR(255),
    name VARCHAR(255)
);

-- Table: IssueComicObject (link table for Issue and ComicObject)
CREATE TABLE IssueComicObject (
    idIssue INT,
    idComicObject INT,
    PRIMARY KEY (idIssue, idComicObject),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idComicObject) REFERENCES ComicObject(idComicObject) ON DELETE CASCADE
);

-- Table: Auteur
CREATE TABLE Auteur (
    idAuteur INT PRIMARY KEY,
    nom VARCHAR(255),
    api_detail_url VARCHAR(255),
    role VARCHAR(255),
    image VARCHAR(255)
);

-- Table: IssueAuteur (link table for Issue and Auteur)
CREATE TABLE IssueAuteur (
    idIssue INT,
    idAuteur INT,
    PRIMARY KEY (idIssue, idAuteur),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idAuteur) REFERENCES Auteur(idAuteur) ON DELETE CASCADE
);

-- Table: Team
CREATE TABLE Team (
    idTeam INT PRIMARY KEY,
    api_detail_url VARCHAR(255),
    name VARCHAR(255),
    image VARCHAR(255)
);

-- Table: IssueTeam (link table for Issue and Team)
CREATE TABLE IssueTeam (
    idIssue INT,
    idTeam INT,
    PRIMARY KEY (idIssue, idTeam),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idTeam) REFERENCES Team(idTeam) ON DELETE CASCADE
);

-- Table: StoryArc
CREATE TABLE StoryArc (
    idStoryArc INT PRIMARY KEY,
    api_detail_url VARCHAR(255),
    image VARCHAR(255)
);

-- Table: IssueStoryArc (link table for Issue and StoryArc)
CREATE TABLE IssueStoryArc (
    idIssue INT,
    idStoryArc INT,
    PRIMARY KEY (idIssue, idStoryArc),
    FOREIGN KEY (idIssue) REFERENCES Issue(idIssue) ON DELETE CASCADE,
    FOREIGN KEY (idStoryArc) REFERENCES StoryArc(idStoryArc) ON DELETE CASCADE
);
