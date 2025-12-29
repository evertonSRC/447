CREATE TABLE IF NOT EXISTS `player_fourthclass_skills`
(
	`player_id` INT NOT NULL,
	`class_id` INT NOT NULL,
	`is_dual` TINYINT(1) NOT NULL DEFAULT 0,
	`skill_id` INT NOT NULL,
	`skill_level` INT NOT NULL,
	`acquired_time` BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`player_id`, `class_id`, `is_dual`, `skill_id`),
	KEY `idx_player_fourthclass_skills_player` (`player_id`),
	KEY `idx_player_fourthclass_skills_class` (`class_id`, `is_dual`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
