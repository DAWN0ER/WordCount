package priv.dawn.wordcountmain.service;

import priv.dawn.wordcountmain.pojo.vo.FileTextVO;

public interface FileStorageService {
    int saveFile(FileTextVO fileText);
    String getContext(int fileUID);
}
