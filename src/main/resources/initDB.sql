DROP TABLE IF EXISTS sock;
CREATE TABLE sock
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    color               VARCHAR NOT NULL,
    cotton_percentage   INT     NOT NULL,
    quantity            BIGINT  NOT NULL DEFAULT 0,
    CONSTRAINT sock_uq unique (color, cotton_percentage)
);

INSERT INTO sock (id, color, cotton_percentage, quantity)
VALUES  (2,'red',80,20),
        (3,'blue',60,10),
        (4,'green',70,14),
        (1,'Переименован',14,14),
        (5,'Желтый',18,30),
        (6,'Зелёный',16,32),
        (7,'Зелёный',20,5);