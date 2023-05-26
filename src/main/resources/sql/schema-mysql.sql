create table if not exists log_document
(
    uuid          varchar(255)  null,
    url           varchar(255)  null,
    method        varchar(255)  null,
    ip            varchar(255)  null,
    class_method  varchar(255)  null,
    detail        varchar(1024) null,
    status        int           null,
    timecost      bigint        null,
    timestamp     bigint        null,
    env           varchar(255)  null,
    instance_uuid varchar(255)  null
);

create table if not exists submission
(
    id              varchar(255) not null
        primary key,
    url             varchar(255) null,
    hash            int          null,
    name            varchar(255) null,
    deleted         tinyint(1)   null,
    reviewed        tinyint(1)   null,
    timestamp       bigint       null,
    up              int          null,
    down            int          null,
    date            varchar(255) null,
    submission_type varchar(255) null
);

