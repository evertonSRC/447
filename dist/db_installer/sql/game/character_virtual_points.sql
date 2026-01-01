CREATE TABLE IF NOT EXISTS `character_virtual_points`
(
	`charId` INT NOT NULL,
	`points` INT NOT NULL,
	PRIMARY KEY (`charId`),
	KEY `idx_character_virtual_points_char` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
