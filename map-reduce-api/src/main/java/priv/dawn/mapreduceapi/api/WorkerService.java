package priv.dawn.mapreduceapi.api;

import java.util.List;

public interface WorkerService {

    int loadFile(int fileUID, int chunkBegin, int chunkNum);
    float getProgress(int fileUID);
    List<String> getWords(int fileUID);
    boolean createOrder(int fileUID, int chunkNum);

}
