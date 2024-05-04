package priv.dawn.wordcountmain.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO: 2024/5/4 改为RPC调用
@Component
public class WorkerDubboCilent {

    public int startCountWord(int fileUID) {

        return fileUID % 2;
    }

    public float getProgress(int fileUID) {
        System.out.println("use RPC: " + fileUID);
        switch (fileUID % 3) {
            case 0:
                return -1f;
            case 1:
                return (fileUID % 1000) / 10f;
            case 2:
                return 100f;
        }
        return 0;
    }

    public List<String> getWordCountList(int fileUID){

        System.out.println("RPC used");
        List<String> list = new ArrayList<String>();
        list.add("RPC"+fileUID);
        return list;
    }



}
