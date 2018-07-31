/*
SQLyog Community v12.2.1 (64 bit)
MySQL - 5.7.12-log : Database - cc-blockchain-apis-schema
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cc-blockchain-apis-schema` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `cc-blockchain-apis-schema`;

/*Table structure for table `company_account` */

DROP TABLE IF EXISTS `company_account`;

CREATE TABLE `company_account` (
  `company_account_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_correlation_id` varchar(50) NOT NULL,
  `child_index` decimal(19,2) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `btc_address` varchar(50) DEFAULT NULL,
  `eth_address` varchar(50) DEFAULT NULL,
  `date_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`company_account_id`),
  UNIQUE KEY `UNIQUE_CHILD_INDEX` (`child_index`),
  UNIQUE KEY `UNIQUE_COMPANY_CORRELATION_ID_INDEX` (`company_correlation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `company_token` */

DROP TABLE IF EXISTS `company_token`;

CREATE TABLE `company_token` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,
  `company_id` int(11) NOT NULL,
  `token_name` varchar(100) NOT NULL,
  `token_symbol` varchar(20) NOT NULL,
  `token_decimals` bigint(20) NOT NULL,
  `token_contract_address` text NOT NULL,
  `token_contract_binary` text NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `token_symbol_unique` (`token_symbol`),
  KEY `company_id_fk` (`company_id`),
  CONSTRAINT `company_id_fk` FOREIGN KEY (`company_id`) REFERENCES `company_account` (`company_account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `oauth_access_token` */

DROP TABLE IF EXISTS `oauth_access_token`;

CREATE TABLE `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` mediumblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `unique_index` */

DROP TABLE IF EXISTS `unique_index`;

CREATE TABLE `unique_index` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `unique_company_index` decimal(19,2) NOT NULL,
  `unique_user_index` decimal(19,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*Table structure for table `user_account` */

DROP TABLE IF EXISTS `user_account`;

CREATE TABLE `user_account` (
  `user_account_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_correlation_id` varchar(50) NOT NULL,
  `child_index` decimal(19,2) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `btc_address` varchar(50) DEFAULT NULL,
  `eth_address` varchar(50) DEFAULT NULL,
  `date_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_account_id`),
  UNIQUE KEY `UNIQUE_CHILD_INDEX` (`child_index`),
  UNIQUE KEY `UNIQUE_USER_CORRELATION_ID_INDEX` (`user_correlation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
