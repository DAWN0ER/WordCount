package priv.dawn.reducer;

import org.junit.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import priv.dawn.reducer.dao.SaveWordCountDao;
import priv.dawn.reducer.mapper.WordCountMapper;

import javax.annotation.Resource;
import java.util.HashMap;

public class ReducerTest extends ReducerApplicationTests {

    @Autowired
    WordCountMapper mapper;

    @Autowired
    SaveWordCountDao wcDao;

    @Qualifier("reducerRedisson")
    @Resource
    RedissonClient reducerRedisson;

    @Test
    public void mapperTest() {
        int uid = 123;
        HashMap<String, Integer> map = generate();
        map.put("测试脏数据", -1);
        try {
            wcDao.saveFromWordCountMap(uid, 0, map);
        } catch (Exception e) {
            log.error(e.toString());
        }
        if(reducerRedisson.getLock("123-0").isLocked()) log.info("lock doesnt release");
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
