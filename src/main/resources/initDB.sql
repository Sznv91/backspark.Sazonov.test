DROP TABLE IF EXISTS sock;
CREATE TABLE sock
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    color               VARCHAR NOT NULL,
    cotton_percentage   INT     NOT NULL,
    quantity            BIGINT  NOT NULL DEFAULT 0,
    CONSTRAINT sock_uq unique (color, cotton_percentage)
);

INSERT INTO sock (color, cotton_percentage, quantity)
VALUES  ('red',80,20),
        ('blue',60,10),
        ('green',70,14),
        ('Переименован',14,14),
        ('Желтый',18,30),
        ('Зелёный',16,32),
        ('Зелёный',20,5);