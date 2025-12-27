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
    
    Player1_ID INT NOT NULL,
    Player2_ID INT NOT NULL,
    
    Start_Time TIMESTAMP,
    End_Time TIMESTAMP,
    
    Winner_ID INT,
    
    PRIMARY KEY (ID),
    
    CONSTRAINT fk_player1
        FOREIGN KEY (Player1_ID)
        REFERENCES PLAYER(ID),
        
    CONSTRAINT fk_player2
        FOREIGN KEY (Player2_ID)
        REFERENCES PLAYER(ID),
        
    CONSTRAINT fk_winner
        FOREIGN KEY (Winner_ID)
        REFERENCES PLAYER(ID)
);
