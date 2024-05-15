package priv.dawn.wordcountmain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.dawn.wordcountmain.domain.WordCountStateEnum;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;
import priv.dawn.wordcountmain.service.impl.WordCountClientRPC;

@RestController
@RequestMapping("/demo/v1/api/words")
public class WordCountController {

    @Autowired
    WordCountClientRPC wordCountClient;

    @GetMapping("/get/{uid}")
    public WordCountListVO getWordCountOfFile(@PathVariable("uid") int uid) {
        return wordCountClient.getWordCounts(uid);
    }

    @PostMapping("/count")
    public WordCountStateEnum startWordCount(@RequestParam("file_uid") int fileUID){
        return wordCountClient.startCountWord(fileUID);
    }
}
