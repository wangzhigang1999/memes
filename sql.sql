create database if not exists memes;

use memes;

drop table if exists media_content;
create table media_content
(
    id                    bigint auto_increment
        primary key,
    data_type             enum ('IMAGE', 'VIDEO', 'MARKDOWN')                                           not null,
    data_content          longtext                                                                      null,
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
)
    collate = utf8mb4_unicode_ci;

create index idx_media_content_id
    on media_content (id);


drop table if exists config;
create table config
(
    id           bigint auto_increment
        primary key,
    config_key   varchar(255)                                            not null comment '配置项的唯一键',
    value        text                                                    null comment '配置的具体值',
    description  text                                                    null comment '配置项的说明',
    visible      tinyint(1)   default 1                                  not null comment '是否可见（1: 可见, 0: 隐藏）',
    visible_name varchar(255) default ''                                 not null comment '用于 UI 显示的名称',
    type         enum ('STRING', 'INTEGER', 'BOOLEAN', 'DOUBLE', 'JSON') not null comment '配置项的数据类型 (STRING, INTEGER, BOOLEAN, DOUBLE, JSON等)',
    constraints  json                                                    null comment '存储 min/max 或其他约束，如 {"min": 1, "max": 100} 或 {"minLength": 3, "maxLength": 10}',
    created_at   timestamp    default CURRENT_TIMESTAMP                  not null comment '创建时间',
    updated_at   timestamp    default CURRENT_TIMESTAMP                  not null on update CURRENT_TIMESTAMP comment '最后更新时间',
    constraint config_key
        unique (config_key)
)
    collate = utf8mb4_unicode_ci;

drop table if exists pinned_submission;
create table pinned_submission
(
    id            bigint auto_increment
        primary key,
    submission_id bigint                              not null comment '被置顶的投稿ID',
    pinned_at     timestamp default CURRENT_TIMESTAMP not null comment '置顶时间',
    pinned_expiry timestamp                           null comment '置顶过期时间',
    pinned_order  int       default 0                 null comment '置顶排序优先级',
    pinned_reason varchar(255)                        null comment '置顶原因',
    created_by    varchar(255)                        not null comment '操作人',
    created_at    timestamp default CURRENT_TIMESTAMP not null,
    updated_at    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);

create index idx_pinned_submission_expiry
    on pinned_submission (pinned_expiry);

create index idx_pinned_submission_order
    on pinned_submission (pinned_order, pinned_at);

drop table if exists request_log;
create table request_log
(
    id              bigint auto_increment
        primary key,
    url             varchar(2048)                                                     null,
    method          enum ('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD') null,
    ip              varbinary(64)                                                     null,
    user_agent      varchar(512)                                                      null,
    refer           varchar(512)                                                      null,
    headers         text                                                              null,
    parameter_map   json                                                              null,
    uuid            char(36)                                                          null,
    response_status smallint                                                          null,
    response_size   bigint                                                            null,
    timecost        int                                                               null,
    timestamp       bigint                                                            null,
    instance_uuid   char(36)                                                          null,
    created_at      timestamp default CURRENT_TIMESTAMP                               not null,
    updated_at      timestamp default CURRENT_TIMESTAMP                               not null on update CURRENT_TIMESTAMP
)
    collate = utf8mb4_unicode_ci;


drop table if exists submission;
create table submission
(
    id                    bigint auto_increment
        primary key,
    media_content_id_list json                                   null,
    likes_count           int unsigned default '0'               null,
    dislikes_count        int          default 0                 null,
    tags                  json                                   null,
    created_at            timestamp    default CURRENT_TIMESTAMP not null,
    updated_at            timestamp    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    collate = utf8mb4_unicode_ci;

create index idx_submission_id_created_at
    on submission (id, created_at);

