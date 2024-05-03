package priv.dawn.wordcountmain.domain;

import org.springframework.stereotype.Component;

@Component
public class WorkerDubboCilent {

    public int startCountWord(int fileUID) {

        return fileUID % 2;
    }

    public float getProgress(int fileUID) {
        System.out.println("use RPC: " + fileUID);
        switch (fileUID % 3) {
            case 0:
                return 0f;
            case 1:
                return (fileUID % 1000) / 10f;
            case 2:
                return 100f;
        }
        return 0;
    }

}
