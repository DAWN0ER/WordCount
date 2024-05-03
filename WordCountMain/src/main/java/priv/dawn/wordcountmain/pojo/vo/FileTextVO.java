package priv.dawn.wordcountmain.pojo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileTextVO {
    @JsonProperty(value = "filename")
    private String filename;
    @JsonProperty(value = "context")
    private String context;
}
