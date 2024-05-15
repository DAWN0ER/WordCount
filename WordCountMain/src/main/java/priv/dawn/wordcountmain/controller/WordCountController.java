package priv.dawn.wordcountmain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;
import priv.dawn.wordcountmain.service.impl.WordCountClientRPC;

@Controller("/demo/v1/api/count")
public class WordCountController {

    @Autowired
    WordCountClientRPC wordCountClient;

    @GetMapping("/get/{uid}")
    public WordCountListVO countWordOfFile(@PathVariable("uid") int fileUID) {
        return wordCountClient.getWordCounts(fileUID);
    }

    @GetMapping("/start")
    public FileWordCountStateEnum startCount(@RequestParam("uid") int fileUID){
        return wordCountClient.startCountWord(fileUID);
    }



}
