ALTER TABLE `term` ADD COLUMN `item_id` INT AFTER `field`
, DROP INDEX `IDX_Term`
, DROP INDEX `IDX_Term_And_Count`,
 ADD INDEX IDX_field_and_item_id USING BTREE(`field`, `item_id`),
 ADD INDEX IDX_item_id(`item_id`);

