CREATE TABLE IF NOT EXISTS `character_fourthclass_points`
(
	`charId` INT NOT NULL,
	`isDual` TINYINT NOT NULL,
	`usedPoints` INT NOT NULL,
	`totalPoints` INT NOT NULL,
	`updatedAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`charId`, `isDual`),
	KEY `idx_character_fourthclass_points_char` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
