CREATE TABLE IF NOT EXISTS `player_fourthclass_points`
(
	`player_id` INT NOT NULL,
	`is_dual` TINYINT NOT NULL,
	`used_points` INT NOT NULL,
	`earned_points` INT NOT NULL,
	`last_level_awarded` INT NOT NULL,
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`player_id`, `is_dual`),
	KEY `idx_player_fourthclass_points_player` (`player_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
