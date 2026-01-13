CREATE TABLE PLAYER (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    Name VARCHAR(100),
    Email VARCHAR(100),
    Password VARCHAR(100),
    Gender VARCHAR(10),
    Score INT,
    PRIMARY KEY (ID)
);

CREATE TABLE SESSION (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    
    Player1ID INT NOT NULL,
    Player2ID INT NOT NULL,
    
    Player1Score INT ,
    Player2Score INT,
    
    PRIMARY KEY (ID),
    
    CONSTRAINT fk_player1
        FOREIGN KEY (Player1ID)
        REFERENCES PLAYER(ID),
        
    CONSTRAINT fk_player2
        FOREIGN KEY (Player2ID)
        REFERENCES PLAYER(ID)
        
);