-- enable unicode supported
ALTER TABLE `item`
MODIFY COLUMN `title` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE `item`
MODIFY COLUMN `document` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci;