package priv.dawn.wordcountmain;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import priv.dawn.wordcountmain.service.WordCountService;

@Slf4j
public class WordCountServiceTest extends WordCountMainApplicationTests {

    @Autowired
    private WordCountService service;

    @Test
    public void simpleTest() {

    }

}
