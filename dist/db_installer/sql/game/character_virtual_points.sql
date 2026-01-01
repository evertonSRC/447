DROP TABLE IF EXISTS `character_virtual_points`;
CREATE TABLE IF NOT EXISTS `character_virtual_points` (
  `charId` int(10) UNSIGNED NOT NULL,
  `points` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
