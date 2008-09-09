-- MySQL dump 10.9
--
-- Host: localhost    Database: repository_test
-- ------------------------------------------------------
-- Server version	5.0.37-community-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
CREATE TABLE `item` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `title` varchar(255) NOT NULL,
  `document` text NOT NULL,
  `createdOn` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `lastUpdatedOn` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Table structure for table `itemmapping`
--

DROP TABLE IF EXISTS `itemmapping`;
CREATE TABLE `itemmapping` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `leftId` bigint(20) unsigned NOT NULL,
  `rightId` bigint(20) unsigned NOT NULL,
  `relationType` varchar(255) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_ItemMapping_leftId` (`leftId`),
  KEY `FK_ItemMapping_rightId` (`rightId`),
  CONSTRAINT `FK_ItemMapping_rightId` FOREIGN KEY (`rightId`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ItemMapping_leftId` FOREIGN KEY (`leftId`) REFERENCES `item` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

