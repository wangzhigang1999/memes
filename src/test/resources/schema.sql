drop table IF EXISTS config;
CREATE TABLE config
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key    VARCHAR(255)                           NOT NULL,
    "value"       TEXT,
    description   TEXT,
    visible       BOOLEAN      DEFAULT TRUE              NOT NULL,
    visible_name  VARCHAR(255) DEFAULT ''                NOT NULL,
    type          VARCHAR(10)                            NOT NULL,
    "constraints" JSON,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT config_key UNIQUE (config_key),
    CONSTRAINT check_type CHECK (type IN ('STRING', 'INTEGER', 'BOOLEAN', 'DOUBLE', 'JSON'))
);


drop table IF EXISTS media_content;
CREATE TABLE media_content
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_type             VARCHAR(10)                           NOT NULL CHECK (data_type IN ('IMAGE', 'VIDEO', 'MARKDOWN')),
    data_content          CLOB,
    user_id               VARCHAR(255),
    checksum              VARCHAR(64),
    llm_description       TEXT,
    llm_moderation_status VARCHAR(10) DEFAULT 'PENDING' CHECK (llm_moderation_status IN ('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED')),
    rejection_reason      TEXT,
    tags                  JSON,
    file_size             BIGINT,
    metadata              JSON,
    status                VARCHAR(10) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'DELETED')),
    created_at            TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_media_content_id ON media_content (id);


drop table IF EXISTS request_log;

CREATE TABLE request_log
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    url             VARCHAR(2048),
    method          VARCHAR(10) CHECK (method IN ('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD')),
    ip              VARBINARY(64),
    user_agent      VARCHAR(512),
    refer           VARCHAR(512),
    headers         TEXT,
    parameter_map   JSON,
    uuid            CHAR(36),
    response_status SMALLINT,
    response_size   BIGINT,
    timecost        INT,
    timestamp       BIGINT,
    instance_uuid   CHAR(36),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

drop table IF EXISTS submission;

CREATE TABLE submission
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    media_content_id_list JSON,
    likes_count           INT       DEFAULT 0,
    dislikes_count        INT       DEFAULT 0,
    tags                  JSON,
    created_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create the index
CREATE INDEX idx_submission_id_created_at ON submission (id, created_at);

