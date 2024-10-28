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

CREATE TABLE IF NOT EXISTS word_count_task
(
    id         BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    task_id	   BIGINT(20) UNSIGNED NOT NULL COMMENT '任务标识id',
    file_uid   INT(10) UNSIGNED NOT NULL COMMENT '文件标识id',
    status     INT NOT NULL DEFAULT 0 COMMENT '1-待完成,2-已完成,3-异常',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY pk (id),
    UNIQUE KEY task_id_key (task_id),
    KEY file_uid_key (file_uid)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  AUTO_INCREMENT = 1 COMMENT '分词统计任务表';