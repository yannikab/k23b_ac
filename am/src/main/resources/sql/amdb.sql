-- phpMyAdmin SQL Dump
-- version 4.4.14.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Dec 10, 2015 at 01:46 AM
-- Server version: 5.6.26-log
-- PHP Version: 5.6.13-pl0-gentoo

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `amdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `amdb_admin`
--

CREATE TABLE IF NOT EXISTS `amdb_admin` (
  `ADMIN_ID` bigint(20) NOT NULL,
  `USERNAME` varchar(40) NOT NULL,
  `PASSWORD` char(64) NOT NULL,
  `ACTIVE` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `amdb_agent`
--

CREATE TABLE IF NOT EXISTS `amdb_agent` (
  `AGENT_ID` bigint(20) NOT NULL,
  `REQUEST_ID` bigint(20) NOT NULL,
  `ADMIN_ID` bigint(20) NOT NULL,
  `TIME_ACCEPTED` datetime NOT NULL,
  `TIME_JOBREQUEST` datetime DEFAULT NULL,
  `TIME_TERMINATED` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `amdb_job`
--

CREATE TABLE IF NOT EXISTS `amdb_job` (
  `JOB_ID` bigint(20) NOT NULL,
  `AGENT_ID` bigint(20) NOT NULL,
  `ADMIN_ID` bigint(20) NOT NULL,
  `TIME_ASSIGNED` datetime NOT NULL,
  `TIME_SENT` datetime DEFAULT NULL,
  `PARAMS` varchar(100) NOT NULL,
  `PERIODIC` bit(1) NOT NULL,
  `PERIOD` int(11) DEFAULT NULL,
  `TIME_STOPPED` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `amdb_request`
--

CREATE TABLE IF NOT EXISTS `amdb_request` (
  `REQUEST_ID` bigint(20) NOT NULL,
  `HASH` char(64) NOT NULL,
  `DEVICE_NAME` varchar(100) DEFAULT NULL,
  `INTERFACE_IP` varchar(100) DEFAULT NULL,
  `INTERFACE_MAC` varchar(100) DEFAULT NULL,
  `OS_VERSION` varchar(100) DEFAULT NULL,
  `NMAP_VERSION` varchar(100) DEFAULT NULL,
  `STATUS` int(11) NOT NULL,
  `TIME_RECEIVED` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `amdb_result`
--

CREATE TABLE IF NOT EXISTS `amdb_result` (
  `RESULT_ID` bigint(20) NOT NULL,
  `JOB_ID` bigint(20) NOT NULL,
  `TIME_RECEIVED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `OUTPUT` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `amdb_admin`
--
ALTER TABLE `amdb_admin`
  ADD PRIMARY KEY (`ADMIN_ID`),
  ADD UNIQUE KEY `UK_USERNAME` (`USERNAME`);

--
-- Indexes for table `amdb_agent`
--
ALTER TABLE `amdb_agent`
  ADD PRIMARY KEY (`AGENT_ID`),
  ADD UNIQUE KEY `UK_REQUEST_ID` (`REQUEST_ID`),
  ADD KEY `FK_AGENT_ADMIN` (`ADMIN_ID`);

--
-- Indexes for table `amdb_job`
--
ALTER TABLE `amdb_job`
  ADD PRIMARY KEY (`JOB_ID`),
  ADD KEY `FK_JOB_AGENT` (`AGENT_ID`),
  ADD KEY `FK_JOB_ADMIN` (`ADMIN_ID`);

--
-- Indexes for table `amdb_request`
--
ALTER TABLE `amdb_request`
  ADD PRIMARY KEY (`REQUEST_ID`),
  ADD UNIQUE KEY `UK_HASH` (`HASH`);

--
-- Indexes for table `amdb_result`
--
ALTER TABLE `amdb_result`
  ADD PRIMARY KEY (`RESULT_ID`),
  ADD KEY `FK_RESULT_JOB` (`JOB_ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `amdb_admin`
--
ALTER TABLE `amdb_admin`
  MODIFY `ADMIN_ID` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `amdb_agent`
--
ALTER TABLE `amdb_agent`
  MODIFY `AGENT_ID` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `amdb_job`
--
ALTER TABLE `amdb_job`
  MODIFY `JOB_ID` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `amdb_request`
--
ALTER TABLE `amdb_request`
  MODIFY `REQUEST_ID` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `amdb_result`
--
ALTER TABLE `amdb_result`
  MODIFY `RESULT_ID` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `amdb_agent`
--
ALTER TABLE `amdb_agent`
  ADD CONSTRAINT `FK_AGENT_ADMIN` FOREIGN KEY (`ADMIN_ID`) REFERENCES `amdb_admin` (`ADMIN_ID`),
  ADD CONSTRAINT `FK_AGENT_REQUEST` FOREIGN KEY (`REQUEST_ID`) REFERENCES `amdb_request` (`REQUEST_ID`);

--
-- Constraints for table `amdb_job`
--
ALTER TABLE `amdb_job`
  ADD CONSTRAINT `FK_JOB_ADMIN` FOREIGN KEY (`ADMIN_ID`) REFERENCES `amdb_admin` (`ADMIN_ID`),
  ADD CONSTRAINT `FK_JOB_AGENT` FOREIGN KEY (`AGENT_ID`) REFERENCES `amdb_agent` (`AGENT_ID`);

--
-- Constraints for table `amdb_result`
--
ALTER TABLE `amdb_result`
  ADD CONSTRAINT `FK_RESULT_JOB` FOREIGN KEY (`JOB_ID`) REFERENCES `amdb_job` (`JOB_ID`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
