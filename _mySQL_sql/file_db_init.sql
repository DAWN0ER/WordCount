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
    id          INT NOT NULL AUTO_INCREMENT,
    file_uid    INT NOT NULL,
    word        CHAR(12) NOT NULL,
    cnt         INT NOT NULL,

    PRIMARY KEY id(id),
    UNIQUE KEY file_word(file_uid,word)
) AUTO_INCREMENT=1;

