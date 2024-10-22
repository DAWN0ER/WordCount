package priv.dawn.reducer;

import org.junit.Test;

import java.util.HashMap;

public class ReducerTest extends ReducerApplicationTests {

    @Test
    public void mapperTest() {

    }

    private HashMap<String, Integer> generate() {
        String data = "大地回春 春花怒放 春色撩人 春色满园 淅淅沥沥 雨声沙沙 细雨淅沥 春雨连绵 雨过天晴 细雨如丝 春雨阵阵 含苞欲放 风和日丽 和风细雨 柳绿花红 气象万千";
        HashMap<String, Integer> tmp = new HashMap<>(32);
        for (String str : data.split(" ")) {
            int cnt = 10;
            tmp.put(str, cnt);
        }
        return tmp;
    }


}
