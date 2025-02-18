insert into disease values  
('OMIM:101600',	'OMIM:176943',	'Craniofacial-skeletal-dermatologic dysplasia',	2263,	'D',	'D'),
('OMIM:101600',	'OMIM:136350',	'Pfeiffer syndrome',	2260,	'D',	'D'),
('ORPHA:11111',	null,	'Test CNV disease',	2222,	'C',	null),
('OMIM:123456',	null,	'Test unconfirmed disease association',	3333,	'?',	null),
('OMIM:234567',	null,	'Test susceptibility disease association',	4444,	'S',	null);


insert into disease_hp values
('OMIM:101600',	'HP:0000174,HP:0000194,HP:0000218,HP:0000238,HP:0000244,HP:0000272,HP:0000303,HP:0000316,HP:0000322,HP:0000324,HP:0000327,HP:0000348,HP:0000431,HP:0000452,HP:0000453,HP:0000470,HP:0000486,HP:0000494,HP:0000508,HP:0000586,HP:0000678,HP:0001156,HP:0001249,HP:0002308,HP:0002676,HP:0002780,HP:0003041,HP:0003070,HP:0003196,HP:0003272,HP:0003307,HP:0003795,HP:0004209,HP:0004322,HP:0004440,HP:0005048,HP:0005280,HP:0005347,HP:0006101,HP:0006110,HP:0009602,HP:0009773,HP:0010055,HP:0010669,HP:0011304'),
('ORPHA:11111',	'HP:0000001'),
('OMIM:123456',	'HP:0000002'),
('OMIM:234567',	'HP:0000002');


insert into entrez2sym
VALUES (2263, 'FGFR2'),
       (2260, 'FGFR1'),
       (2222, 'GENE2'),
       (3333, 'GENE3'),
       (4444, 'GENE4');