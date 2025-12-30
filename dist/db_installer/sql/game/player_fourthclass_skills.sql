CREATE TABLE IF NOT EXISTS `player_fourthclass_skills`
(
	`player_id` INT NOT NULL,
	`class_id` INT NOT NULL,
	`skill_id` INT NOT NULL,
	`skill_level` INT NOT NULL,
	`acquired_time` BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`player_id`, `class_id`, `skill_id`),
	KEY `idx_player_fourthclass_skills_player` (`player_id`),
	KEY `idx_player_fourthclass_skills_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
