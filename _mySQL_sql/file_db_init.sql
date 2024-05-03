create table if not exists file_info (
    id          int             NOT NULL AUTO_INCREMENT,
    file_name   varchar(128)    NOT NULL,
    file_uid    int             NOT NULL,
    chunk_num   int             NOT NULL,

    PRIMARY KEY id(id),
    key file_name_id(file_name,file_uid),
    UNIQUE KEY file_uid(file_uid)

)AUTO_INCREMENT=1;

create table if not exists t_file_chunks (
    id          int         NOT NULL AUTO_INCREMENT,
    file_uid    int         NOt NULL,
    chunk_id    int         NOT NULL,
    context     text        NOT NULL,

    PRIMARY KEY id(id),
    UNIQUE KEY file_uid_chunk_id(file_uid,chunk_id)
)AUTO_INCREMENT=1;

insert into file_info (file_name,file_uid,chunk_num) values('红楼梦',123,100);