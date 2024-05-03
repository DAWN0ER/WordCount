package priv.dawn.wordcountmain.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class FileInfoDTO {
    private String filename;
    private int uid;
    private int chunkNum;
}
