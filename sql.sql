-- auto-generated definition
create table media_content
(
    id                    int auto_increment
        primary key,
    data_type             enum ('IMAGE', 'VIDEO', 'MARKDOWN')                                           not null,
    data_content          varchar(2048)                                                                 null,
    user_id               varchar(255)                                                                  null,
    checksum              varchar(64)                                                                   null,
    llm_description       text                                                                          null,
    llm_moderation_status enum ('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED') default 'PENDING'         null,
    rejection_reason      text                                                                          null,
    tags                  json                                                                          null,
    file_size             bigint                                                                        null,
    metadata              json                                                                          null,
    status                enum ('PENDING', 'APPROVED', 'REJECTED', 'DELETED') default 'PENDING'         null,
    created_at            timestamp                                           default CURRENT_TIMESTAMP not null,
    updated_at            timestamp                                           default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);


-- auto-generated definition
create table submission
(
    id                    int auto_increment primary key,
    media_content_id_list json null,
    likes_count           int  null,
    dislikes_count        int  null,
    tags                  json null
);

drop table if exists request_log;
CREATE TABLE if not exists request_log
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,                                                                                      -- 使用 BIGINT 扩展 ID 以支持更大数据量
    url             VARCHAR(2048)                                                     DEFAULT NULL,                                         -- URL 请求
    method          ENUM ('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD') DEFAULT NULL,                                         -- 约束 HTTP 方法
    ip              VARBINARY(16)                                                     DEFAULT NULL,                                         -- 存储 IPv4/IPv6 地址，减少存储空间 (INET_ATON()/INET_NTOA())
    user_agent      VARCHAR(512)                                                      DEFAULT NULL,                                         -- 记录 User-Agent 以分析客户端来源
    refer           VARCHAR(512)                                                      DEFAULT NULL,                                         -- 记录 User-Agent 以分析客户端来源
    headers         TEXT                                                              DEFAULT NULL,                                         -- 存储请求头（JSON 格式更合理）
    parameter_map   JSON                                                              DEFAULT NULL,                                         -- 变更为 JSON，方便解析与查询
    uuid            CHAR(36)                                                          DEFAULT NULL,                                         -- 使用 CHAR(36) 以提高查询效率
    response_status SMALLINT                                                          DEFAULT NULL,                                         -- 记录 HTTP 响应状态码
    response_size   BIGINT                                                            DEFAULT NULL,                                         -- 记录返回的数据大小
    timecost        INT                                                               DEFAULT NULL,                                         -- 记录耗时（单位：毫秒），INT 足够
    timestamp       BIGINT                                                            DEFAULT NULL,                                         -- 记录时间戳 (UNIX 时间)
    instance_uuid   CHAR(36)                                                          DEFAULT NULL,                                         -- 关联实例唯一标识
    created_at      TIMESTAMP                                                         DEFAULT CURRENT_TIMESTAMP,                            -- 默认创建时间
    updated_at      TIMESTAMP                                                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 记录更新时间
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


drop table if exists config_item;
CREATE TABLE if not exists config
(
    id           INT                                                     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    config_key   VARCHAR(255)                                            NOT NULL UNIQUE COMMENT '配置项的唯一键',
    value        TEXT                                                    NULL COMMENT '配置的具体值',
    description  TEXT                                                    NULL COMMENT '配置项的说明',
    visible      BOOLEAN                                                 NOT NULL DEFAULT 1 COMMENT '是否可见（1: 可见, 0: 隐藏）',
    visible_name VARCHAR(255)                                            NOT NULL DEFAULT '' COMMENT '用于 UI 显示的名称',
    type         ENUM ('STRING', 'INTEGER', 'BOOLEAN', 'DOUBLE', 'JSON') NOT NULL COMMENT '配置项的数据类型 (STRING, INTEGER, BOOLEAN, DOUBLE, JSON等)',
    constraints  JSON                                                    NULL COMMENT '存储 min/max 或其他约束，如 {"min": 1, "max": 100} 或 {"minLength": 3, "maxLength": 10}',
    created_at   TIMESTAMP                                                        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   TIMESTAMP                                                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
);
