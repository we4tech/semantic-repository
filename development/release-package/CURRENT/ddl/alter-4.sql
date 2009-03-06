CREATE TABLE `term` (
  `id` int NOT NULL AUTO_INCREMENT,
  `term` varchar(255) DEFAULT NULL,
  `field` varchar(255) DEFAULT NULL,
  `count` INT,
  PRIMARY KEY (`id`)
)
CHARACTER SET utf8;

ALTER TABLE `term` ADD INDEX IDX_Term(`term`),
 ADD INDEX IDX_Term_And_Count(`term`, `count`);
