CREATE TABLE IF NOT EXISTS `character_virtual_equipment`
(
	`charId` INT NOT NULL,
	`slot` INT NOT NULL,
	`itemId` INT NOT NULL,
	`enchant` INT NOT NULL,
	PRIMARY KEY (`charId`, `slot`),
	KEY `idx_character_virtual_equipment_char` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
