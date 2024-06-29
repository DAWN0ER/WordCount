package priv.dawn.reducer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import priv.dawn.reducer.mapper.MsgOrderMapper;

public class MapperTest extends ReducerApplicationTests {

    @Autowired
    MsgOrderMapper mapper;

    @Test
    public void test01(){
        mapper.saveNewOrder(10,100,1000,999);
    }


}
