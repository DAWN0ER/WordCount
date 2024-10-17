CREATE TABLE IF NOT EXISTS file_info
(
    id           BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    file_uid     INT(10) UNSIGNED    NOT NULL COMMENT '文件标识id',
    file_name    VARCHAR(128)        NOT NULL COMMENT '文件名',
    chunk_num    INT(10)             NOT NULL COMMENT '分区数量',
    status       TINYINT             NOT NULL DEFAULT 0 COMMENT '文件状态（1-INIT，2-STORED，3-DAMAGED）',
    created_time DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY pk (id),
    UNIQUE KEY file_uid_key (file_uid)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1 COMMENT '文件基础信息表';

-- 可分库分表
CREATE TABLE IF NOT EXISTS file_chunks
(
    id       BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    file_uid INT(10) UNSIGNED    NOT NULL COMMENT '文件标识id',
    chunk_id INT(10)             NOT NULL COMMENT '分区id',
    context  TEXT                NOT NULL COMMENT '分区主要内容',
    PRIMARY KEY id_key (id),
    UNIQUE KEY file_uid_chunk_id (file_uid, chunk_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1 COMMENT '文件存储表';

-- 可分库分表
CREATE TABLE IF NOT EXISTS word_count
(
    id       BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    file_uid INT(10) UNSIGNED    NOT NULL COMMENT '文件标识id',
    word     CHAR(20)            NOT NULL COMMENT '分词',
    cnt      INT                 NOT NULL COMMENT '计数',
    PRIMARY KEY id (id),
    UNIQUE KEY file_word (file_uid, word)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1 COMMENT '分词计数表';

-- 下面废弃
CREATE TABLE IF NOT EXISTS file_info
(
    id        INT          NOT NULL AUTO_INCREMENT,
    file_name VARCHAR(128) NOT NULL,
    file_uid  INT          NOT NULL,
    chunk_num INT          NOT NULL,

    PRIMARY KEY id (id),
    key file_name_id (file_name, file_uid),
    UNIQUE KEY file_uid (file_uid)

) AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS t_file_chunks
(
    id       INT  NOT NULL AUTO_INCREMENT,
    file_uid INT  NOT NULL,
    chunk_id INT  NOT NULL,
    context  TEXT NOT NULL,

    PRIMARY KEY id (id),
    UNIQUE KEY file_uid_chunk_id (file_uid, chunk_id)
) AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS t_file_progress
(
    id         INT NOT NULL AUTO_INCREMENT,
    file_uid   INT NOT NULL,
    chunks_num INT NOT NULL,
    finished   INT NOT NULL DEFAULT 0,

    PRIMARY KEY id (id),
    UNIQUE KEY file_uid (file_uid)
) AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS t_file_word_count
(
    id       INT      NOT NULL AUTO_INCREMENT,
    file_uid INT      NOT NULL,
    word     CHAR(12) NOT NULL,
    cnt      INT      NOT NULL,

    PRIMARY KEY id (id),
    UNIQUE KEY file_word (file_uid, word)
) AUTO_INCREMENT = 1;

CREATE TABLE IF NOT EXISTS t_msg_key
(
    id        INT NOT NULL AUTO_INCREMENT,
    file_uid  INT NOT NULL,
    chunk_id  INT NOT NULL,
    part INT NOT NULL,
    part_num  INT NOT NULL,

    PRIMARY KEY id (id),
    UNIQUE KEY only_key (file_uid, chunk_id)
) AUTO_INCREMENT = 1;
