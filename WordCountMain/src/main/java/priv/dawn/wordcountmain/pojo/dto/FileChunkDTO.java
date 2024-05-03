package priv.dawn.wordcountmain.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChunkDTO {
    private int chunkId;
    private String context;
}
