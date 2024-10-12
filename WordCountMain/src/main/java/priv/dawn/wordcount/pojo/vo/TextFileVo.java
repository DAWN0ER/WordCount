package priv.dawn.wordcount.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextFileVo {
    private String filename;
    private String context;
}
