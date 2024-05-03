package priv.dawn.wordcountmain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import priv.dawn.wordcountmain.mapper.FileMapper;
import priv.dawn.wordcountmain.pojo.dto.FileInfoDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MapperTest extends WordCountMainApplicationTests {

    @Autowired
    FileMapper fileMapper;

    @Test
    public void insertChunkTest() {
        for (int idx = 1; idx <= 20; idx++) {
            fileMapper.saveFileChunk(456, idx, "这是第" + idx + "段文本,来自西游记;");
        }
    }

    @Test
    public void getContextTest() {
        List<String> context = fileMapper.getALLContextByUid(1414193578);
        String collect = context.stream().collect(Collectors.joining());
        System.out.println(collect);
    }

    @Test
    public void fileInfoTest(){
        FileInfoDTO file = new FileInfoDTO("西游记",456,20);
        fileMapper.saveFileInfo(file);
        FileInfoDTO res = fileMapper.getFileInfoById(456);
        System.out.println(res.toString());
    }


}
