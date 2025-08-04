-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.22-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema sacs
--

CREATE DATABASE IF NOT EXISTS sacs;
USE sacs;

--
-- Definition of table `datapub`
--

DROP TABLE IF EXISTS `datapub`;
CREATE TABLE `datapub` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `pass` varchar(45) NOT NULL,
  `ustatus` varchar(45) NOT NULL,
  `regtime` varchar(45) NOT NULL,
  `lastlog` varchar(45) default '0',
  `vparam` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `datapub`
--

/*!40000 ALTER TABLE `datapub` DISABLE KEYS */;
/*!40000 ALTER TABLE `datapub` ENABLE KEYS */;


--
-- Definition of table `datareceiver`
--

DROP TABLE IF EXISTS `datareceiver`;
CREATE TABLE `datareceiver` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `phone` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `pass` varchar(45) NOT NULL,
  `ustatus` varchar(45) NOT NULL,
  `regtime` varchar(45) NOT NULL,
  `lastlog` varchar(45) default '0',
  `vparam` longtext NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `datareceiver`
--

/*!40000 ALTER TABLE `datareceiver` DISABLE KEYS */;
/*!40000 ALTER TABLE `datareceiver` ENABLE KEYS */;


--
-- Definition of table `downloads`
--

DROP TABLE IF EXISTS `downloads`;
CREATE TABLE `downloads` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `drid` varchar(45) NOT NULL,
  `drname` varchar(45) NOT NULL,
  `dpid` varchar(45) NOT NULL,
  `dpname` varchar(45) NOT NULL,
  `fid` varchar(45) NOT NULL,
  `fname` varchar(45) NOT NULL,
  `time` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `downloads`
--

/*!40000 ALTER TABLE `downloads` DISABLE KEYS */;
/*!40000 ALTER TABLE `downloads` ENABLE KEYS */;


--
-- Definition of table `searchreq`
--

DROP TABLE IF EXISTS `searchreq`;
CREATE TABLE `searchreq` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `drid` varchar(45) NOT NULL,
  `drname` varchar(45) NOT NULL,
  `drmail` varchar(45) NOT NULL,
  `fname` varchar(45) NOT NULL,
  `fid` varchar(45) NOT NULL,
  `vstatus` varchar(45) NOT NULL,
  `time` varchar(45) NOT NULL,
  `dkey` varchar(45) NOT NULL,
  `dpid` varchar(45) NOT NULL,
  `dpname` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `searchreq`
--

/*!40000 ALTER TABLE `searchreq` DISABLE KEYS */;
/*!40000 ALTER TABLE `searchreq` ENABLE KEYS */;


--
-- Definition of table `uploads`
--

DROP TABLE IF EXISTS `uploads`;
CREATE TABLE `uploads` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `dpid` varchar(45) NOT NULL,
  `dpname` varchar(45) NOT NULL,
  `kword` varchar(45) NOT NULL,
  `upfile` longblob NOT NULL,
  `time` varchar(45) NOT NULL,
  `enkey` varchar(45) NOT NULL,
  `fname` varchar(45) NOT NULL,
  `ftype` varchar(45) NOT NULL,
  `fstatus` varchar(45) NOT NULL,
  `skey` varchar(45) NOT NULL default 'No',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `uploads`
--

/*!40000 ALTER TABLE `uploads` DISABLE KEYS */;
/*!40000 ALTER TABLE `uploads` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
