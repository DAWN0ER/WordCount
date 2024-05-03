package priv.dawn.mapreduceapi.api;

import java.util.List;

public interface WorkerService {

    void loadFile(int fileUID, int chunkBegin, int chunkNum);
    float getProgress(int fileUID);
    List<String> getWords(int fileUID);

}
