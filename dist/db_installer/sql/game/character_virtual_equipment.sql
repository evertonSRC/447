DROP TABLE IF EXISTS `character_virtual_equipment`;
CREATE TABLE IF NOT EXISTS `character_virtual_equipment` (
  `charId` int(10) UNSIGNED NOT NULL,
  `slot` int NOT NULL,
  `itemId` int NOT NULL,
  `enchant` int NOT NULL,
  `indexMain` int NOT NULL,
  `indexSub` int NOT NULL,
  PRIMARY KEY (`charId`, `slot`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
