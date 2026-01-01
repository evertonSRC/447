CREATE TABLE IF NOT EXISTS `character_virtual_equipment` (
  `charId` INT NOT NULL,
  `slot` BIGINT NOT NULL,
  `index_main` INT NOT NULL,
  `index_sub` INT NOT NULL,
  `equipped_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`charId`, `slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
