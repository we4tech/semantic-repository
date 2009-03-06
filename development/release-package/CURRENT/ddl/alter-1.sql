ALTER TABLE `item` ADD COLUMN `indexRepository` varchar(255) AFTER `lastUpdatedOn`;

ALTER TABLE `item` ADD INDEX IDX_title(`title`);

