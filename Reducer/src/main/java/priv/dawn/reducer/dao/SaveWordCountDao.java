package priv.dawn.reducer.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Repository
public class SaveWordCountDao {

    // TODO: 2024/5/7 还没做 reducer 和数据库的交互 
    @Transactional
    public void saveFromMap(HashMap<Integer,HashMap<String,Integer>> fileMap){

    }

}
