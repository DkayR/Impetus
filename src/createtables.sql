CREATE TABLE IF NOT EXISTS impetus_players (
    uuid CHAR(36) NOT NULL PRIMARY KEY,
    base_display_name VARCHAR(50)
);
CREATE TABLE IF NOT EXISTS impetus_locations (
    uuid CHAR(36) NOT NULL PRIMARY KEY,
    world CHAR(36) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    yaw DOUBLE NOT NULL,
    pitch DOUBLE NOT NULL
);

CREATE TABLE IF NOT EXISTS impetus_effect_types (
    effect_type VARCHAR(255) NOT NULL PRIMARY KEY
);

INSERT IGNORE INTO impetus_effect_types (effect_type) VALUES ("TELEPORT");

CREATE TABLE IF NOT EXISTS impetus_prac_location_types (
    prac_location_type VARCHAR(255) NOT NULL PRIMARY KEY
);

INSERT IGNORE INTO impetus_prac_location_types (prac_location_type) VALUES ("ADHOC");
INSERT IGNORE INTO impetus_prac_location_types (prac_location_type) VALUES ("DEFINED");
INSERT IGNORE INTO impetus_prac_location_types (prac_location_type) VALUES ("COURSE");

CREATE TABLE IF NOT EXISTS impetus_player_prac_locations (
    player_uuid CHAR(36) NOT NULL,
    location_id CHAR(36) NOT NULL,
    creation_date DOUBLE UNSIGNED NOT NULL,
    time_elapsed DOUBLE UNSIGNED NOT NULL DEFAULT 0,
    attempts INT UNSIGNED NOT NULL DEFAULT 0,
    current_location BOOLEAN NOT NULL DEFAULT true,
    prac_location_type VARCHAR(255) NOT NULL DEFAULT "ADHOC",
    PRIMARY KEY(player_uuid, location_id),
    CONSTRAINT `fk_location_to_player`
        FOREIGN KEY (player_uuid) REFERENCES impetus_players (uuid),
    CONSTRAINT `fk_player_to_location`
        FOREIGN KEY (location_id) REFERENCES impetus_locations (uuid)
        ON DELETE CASCADE,
    CONSTRAINT `fk_prac_type_from_prac_types`
        FOREIGN KEY (prac_location_type) REFERENCES impetus_prac_location_types (prac_location_type)
);

CREATE TABLE IF NOT EXISTS impetus_effect_sets (
    effect_set_uuid CHAR(36) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS impetus_effect_set_effects (
    effect_set_uuid CHAR(36) NOT NULL,
    effect_id CHAR(36) NOT NULL,
    effect_type VARCHAR(255) NOT NULL,
    effect_order INT UNSIGNED NOT NULL,
    PRIMARY KEY(effect_set_uuid, effect_id),
    CONSTRAINT `fk_set_uuid_to_effects`
        FOREIGN KEY (effect_set_uuid) REFERENCES impetus_effect_sets (effect_set_uuid)
        ON DELETE CASCADE,
    CONSTRAINT `fk_effect_type`
        FOREIGN KEY (effect_type) REFERENCES impetus_effect_types (effect_type)
);

CREATE INDEX IF NOT EXISTS effect_index ON impetus_effect_set_effects (effect_id);

CREATE TABLE IF NOT EXISTS impetus_interact_types (
    interact_type VARCHAR(255) NOT NULL PRIMARY KEY
);

INSERT IGNORE INTO impetus_interact_types (interact_type) VALUES ("PHYSICAL");
INSERT IGNORE INTO impetus_interact_types (interact_type) VALUES ("RIGHT_CLICK");

CREATE TABLE IF NOT EXISTS impetus_activator_blocks (
    block_uuid CHAR(36) NOT NULL PRIMARY KEY,
    world_uuid CHAR(36) NOT NULL,
    effect_set_uuid CHAR(36),
    interact_type VARCHAR(255) NOT NULL DEFAULT "PHYSICAL",
    block_x DOUBLE NOT NULL,
    block_y DOUBLE NOT NULL,
    block_z DOUBLE NOT NULL,
    CONSTRAINT `fk_activator_block_effects_pointer`
        FOREIGN KEY (effect_set_uuid) REFERENCES impetus_effect_sets (effect_set_uuid),
    CONSTRAINT `fk_interact_type`
        FOREIGN KEY (interact_type) REFERENCES impetus_interact_types (interact_type)
);
CREATE TABLE IF NOT EXISTS impetus_teleport_types (
    teleport_type VARCHAR(255) NOT NULL PRIMARY KEY
);

INSERT IGNORE INTO impetus_teleport_types (teleport_type) VALUES ("DEFINED");
INSERT IGNORE INTO impetus_teleport_types (teleport_type) VALUES ("INSTANT");
INSERT IGNORE INTO impetus_teleport_types (teleport_type) VALUES ("UPDATE");

CREATE TABLE IF NOT EXISTS impetus_teleport_effects (
    effect_id CHAR(36) NOT NULL PRIMARY KEY,
    location_uuid CHAR(36),
    teleport_type VARCHAR(255),
    CONSTRAINT `fk_effect_id`
        FOREIGN KEY (effect_id) REFERENCES impetus_effect_set_effects (effect_id)
        ON DELETE CASCADE,
    CONSTRAINT `fk_location_pointer`
        FOREIGN KEY (location_uuid) REFERENCES impetus_locations (uuid)
        ON DELETE CASCADE,
    CONSTRAINT `fk_teleport_type`
        FOREIGN KEY (teleport_type) REFERENCES impetus_teleport_types (teleport_type)
);

CREATE TABLE IF NOT EXISTS impetus_player_settings (
    uuid CHAR(36) NOT NULL PRIMARY KEY,
    show_timer BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT `fk_player_uuid`
        FOREIGN KEY (uuid) REFERENCES impetus_players (uuid)
        ON DELETE CASCADE
);