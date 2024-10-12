package priv.dawn.wordcount.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.dawn.wordcount.domain.WordCountStateEnum;
import priv.dawn.wordcount.pojo.vo.WordCountListVO;
import priv.dawn.wordcount.service.impl.WordCountClientRPC;

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
