package priv.dawn.wordcount;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import priv.dawn.wordcount.pojo.vo.TextFileVo;
import priv.dawn.wordcount.service.FileStorageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class FileStorageServiceTest extends WordCountMainApplicationTests {

    @Autowired
    FileStorageService fileStorageService;

    @Test
    public void chunkTest() throws IOException {
        File file = ResourceUtils.getFile("classpath:活着.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String context = reader.lines().collect(Collectors.joining());
        TextFileVo fileText = new TextFileVo("活着", context);
        int uid = fileStorageService.saveFile(fileText);
        log.info("File uid: "+uid);
    }

}
