DROP TABLE IF EXISTS `conquest_prev_season_ranklist`;
CREATE TABLE `conquest_prev_season_ranklist` (
  `charId` int(10) unsigned NOT NULL DEFAULT 0,
  `char_name` VARCHAR(45) DEFAULT NULL,
  `personal_points` bigint(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
