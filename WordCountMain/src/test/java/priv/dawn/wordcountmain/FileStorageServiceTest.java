package priv.dawn.wordcountmain;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import priv.dawn.wordcountmain.pojo.vo.FileTextVO;
import priv.dawn.wordcountmain.service.FileStorageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class FileStorageServiceTest extends WordCountMainApplicationTests {

    @Autowired
    FileStorageService fileStorageService;

    @Test
    public void chunkTest() throws IOException {
        File file = ResourceUtils.getFile("classpath:file.txt");
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        String context = reader.lines().collect(Collectors.joining());
        FileTextVO fileText = new FileTextVO("欧亨利", context);
        int uid = fileStorageService.saveFile(fileText);
        System.out.println(uid);
    }

}
