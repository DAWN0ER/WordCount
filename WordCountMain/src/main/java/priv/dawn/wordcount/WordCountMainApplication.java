package priv.dawn.wordcount;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 没有启动Dubbo的时候不要
 @EnableDubbo
@SpringBootApplication
public class WordCountMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordCountMainApplication.class, args);
    }

}
