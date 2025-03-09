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

