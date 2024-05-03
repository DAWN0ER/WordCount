package priv.dawn.wordcountmain.domain;

import org.springframework.stereotype.Component;

@Component
public class WorkerDubboCilent {

    public int startCountWord(int fileUID) {

        return fileUID;
    }

    public float getProgress(int fileUID) {

        return fileUID % 2 == 0 ? (fileUID % 1_000) / 10L : 100L;
    }

}
