package priv.dawn.wordcount.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.dawn.wordcount.domain.FileWordCountTaskVo;
import priv.dawn.wordcount.domain.FileWordCountVo;

import java.util.List;

@RestController
@RequestMapping("/v2/api/word")
public class WordCountController {


    @GetMapping("/count")
    public FileWordCountTaskVo startCountWord(int id) {
        return null;
    }

    @GetMapping("/topK")
    public FileWordCountVo getTopKWordCount(int id) {
        return null;
    }

    @PostMapping("/get")
    public FileWordCountVo getCountOfWords(List<String> words){
        return null;
    }

}
