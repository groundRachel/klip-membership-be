-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema klip-pass
-- Character Set: utf8mb4
-- Collation utf8mb4-general_ci
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Table `admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `admin` (
  `id` INT NOT NULL,
  `is_super` TINYINT NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `partner_application`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `partner_application` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `oauth_id` VARCHAR(40) NOT NULL,
  `phone_number` VARCHAR(20) NOT NULL,
  `business_name` VARCHAR(40) NOT NULL,
  `business_registration_number` VARCHAR(12) NOT NULL,
  `status` TINYINT NOT NULL COMMENT '1: 신청함, 2:승인됨, 3:거절됨',
  `reject_reason` VARCHAR(1000) NULL DEFAULT NULL,
  `klip_drops_partner_id` INT NULL DEFAULT NULL,
  `klip_drops_partner_name` VARCHAR(100) NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `processed_at` DATETIME NULL DEFAULT NULL,
  `processor_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `partner`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `partner` (
  `id` INT NOT NULL,
  `partner_application_id` INT NOT NULL,
  `klip_drops_partner_id` INT NULL DEFAULT NULL,
  `phone_number` VARCHAR(20) NOT NULL,
  `business_registration_number` VARCHAR(12) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_partner_partner_application`
    FOREIGN KEY (`partner_application_id`)
    REFERENCES `partner_application` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX ON `partner` (`klip_drops_partner_id` ASC) VISIBLE;

CREATE INDEX `fk_partner_partner_application_idx` ON `partner` (`partner_application_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `member` (
  `id` INT NULL AUTO_INCREMENT,
  `type` CHAR(1) NOT NULL COMMENT 'A: 어드민, P:파트너',
  `email` VARCHAR(255) NULL DEFAULT NULL,
  `oauth_id` VARCHAR(40) NULL DEFAULT NULL,
  `name` VARCHAR(40) NOT NULL,
  `status` TINYINT NOT NULL COMMENT '1: 유효함, 2:탈퇴함',
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_member_admin_id`
    FOREIGN KEY (`id`)
    REFERENCES `admin` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_member_partner_id`
    FOREIGN KEY (`id`)
    REFERENCES `partner` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX `udx_member_type_email` ON `member` (`type` ASC, `email` ASC) VISIBLE;

CREATE UNIQUE INDEX `udx_member_type_oauth_id` ON `member` (`type` ASC, `oauth_id` DESC) VISIBLE;


-- -----------------------------------------------------
-- Table `attach_file`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attach_file` (
  `id` VARCHAR(255) NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `content_type` VARCHAR(50) NOT NULL,
  `content_length` BIGINT NOT NULL,
  `object_id` VARCHAR(255) NOT NULL,
  `link_url` VARCHAR(255) NOT NULL,
  `link_status` TINYINT NOT NULL COMMENT '0: Unlink, 1: Linked',
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `faq`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `faq` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `body` MEDIUMTEXT NOT NULL,
  `status` TINYINT NOT NULL COMMENT '0: Draft, 1: Live, 2: Inactive, 3: Delete',
  `lived_at` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `notice`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `notice` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `body` MEDIUMTEXT NOT NULL,
  `is_primary` TINYINT NOT NULL,
  `status` TINYINT NOT NULL COMMENT '0: Draft, 1: Live, 2: Inactive, 3: Delete',
  `lived_at` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `open_chatting`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `open_chatting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `openlink_id` BIGINT NOT NULL,
  `openlink_url` VARCHAR(255) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `cover_image_url` VARCHAR(255) NULL DEFAULT NULL,
  `source` TINYINT NOT NULL COMMENT '0: Klaytn, 1: KlipDrops',
  `status` TINYINT NOT NULL COMMENT '1: Activated, 2: Deleted',
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`));

CREATE UNIQUE INDEX `udx_open_chatting_openlink_id` ON `open_chatting` (`openlink_id` DESC) VISIBLE;

CREATE UNIQUE INDEX `udx_open_chatting_openlink_url` ON `open_chatting` (`openlink_url` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `operator`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `operator` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `partner_id` INT NOT NULL,
  `klip_id` BIGINT NOT NULL,
  `kakao_user_id` VARCHAR(30) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_operator_partner`
    FOREIGN KEY (`partner_id`)
    REFERENCES `partner` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX `udx_operator_klip_id` ON `operator`(`klip_id` DESC)  VISIBLE;

CREATE UNIQUE INDEX `udx_operator_kakao_user_id` ON `operator` (`kakao_user_id` DESC) VISIBLE;

CREATE INDEX `fk_operator_partner_idx` ON `operator` (`partner_id` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `open_chatting_member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `open_chatting_member` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `open_chatting_id` BIGINT NOT NULL,
  `kakao_user_id` VARCHAR(30) NOT NULL,
  `klip_id` BIGINT NOT NULL,
  `operator_id` BIGINT NULL,
  `nickname` VARCHAR(20) NOT NULL,
  `profile_image_url` VARCHAR(255) NOT NULL,
  `role` TINYINT NOT NULL COMMENT '0: 방장, 1: 운영진, 2: NFT홀더',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_open_chatting_member_operator`
    FOREIGN KEY (`operator_id`)
    REFERENCES `operator` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_open_chatting_member_open_chatting`
    FOREIGN KEY (`open_chatting_id`)
    REFERENCES `open_chatting` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX `udx_open_chatting_member_operator_id` ON `open_chatting_member` (`open_chatting_id` DESC, `operator_id` DESC) VISIBLE;

CREATE INDEX `fk_open_chatting_member_operator_idx` ON `open_chatting_member` (`operator_id` DESC) VISIBLE;

CREATE INDEX `fk_open_chatting_member_open_chatting_idx` ON `open_chatting_member` (`open_chatting_id` DESC) VISIBLE;


-- -----------------------------------------------------
-- Table `open_chatting_nft`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `open_chatting_nft` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `open_chatting_id` BIGINT NOT NULL,
  `drop_id` BIGINT NULL,
  `klip_drops_sca` VARCHAR(80) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `creator_id` INT NOT NULL,
  `updated_at` DATETIME NOT NULL,
  `updater_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_open_chatting_nft_open_chatting`
    FOREIGN KEY (`open_chatting_id`)
    REFERENCES `open_chatting` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX `udx_open_chatting_nft_drop_id` ON `open_chatting_nft` (`drop_id` DESC) VISIBLE;

CREATE INDEX `fk_open_chatting_nft_open_chatting_idx` ON `open_chatting_nft` (`open_chatting_id` DESC) VISIBLE;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
