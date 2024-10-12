package priv.dawn.wordcount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Deprecated
@Data
@AllArgsConstructor
@ToString
public class FileInfoDTO {
    private String filename;
    private int uid;
    private int chunkNum;
}
