-- Active: 1750856713962@@schoolpass-mysql.ns-qk9pezjn.svc@3306@schoolpass
-- Campus Vehicle Management System - Database Schema
-- This script is designed for MySQL.
--
-- Table structure for table `users`
--
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `password` varchar(255) NOT NULL,
  `phone` varchar(11) NOT NULL,
  `student_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_users_phone` (`phone`),
  UNIQUE KEY `UK_users_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Stores user information and credentials.';

--
-- Table structure for table `user_roles`
--
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role` enum('ROLE_STUDENT','ROLE_STAFF','ROLE_GUARD','ROLE_ADMIN') NOT NULL,
  PRIMARY KEY (`user_id`,`role`),
  KEY `FK_user_roles_users` (`user_id`),
  CONSTRAINT `FK_user_roles_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Maps roles to users.';

--
-- Table structure for table `vehicles`
--
DROP TABLE IF EXISTS `vehicles`;
CREATE TABLE `vehicles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `license_plate` varchar(255) NOT NULL,
  `photo_url` varchar(255) NOT NULL,
  `status` enum('PENDING_APPROVAL','APPROVED','REJECTED','REPORTED_LOST') NOT NULL,
  `owner_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_vehicles_owner_id` (`owner_id`),
  CONSTRAINT `FK_vehicles_users` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Stores vehicle information and their status.'; 