package priv.dawn.wordcount.service;

import priv.dawn.wordcount.pojo.vo.TextFileVo;

@Deprecated
public interface FileStorageService {
    int saveFile(TextFileVo fileText);
    TextFileVo getFile(int fileUID);
}
