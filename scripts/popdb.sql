INSERT INTO Montadora (ID_MONTADORA, NOME)
VALUES 
(1, 'Volkswagen'),
(2, 'Mercedes'),
(3, 'BMW');


INSERT INTO Carro (ID_CARRO, MODELO, COR, DATAENTRADA, FK_MONTADORA)
VALUES 
(1, 'Polo', 'Azul', '2019-05-09', 1),
(2, 'A200', 'Preta', '2023-05-09', 2),
(3, '320i', 'Branca', '2021-12-01',3);