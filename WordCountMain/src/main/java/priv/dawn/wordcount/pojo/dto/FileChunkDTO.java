package priv.dawn.wordcount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChunkDTO {
    private int chunkId;
    private String context;
}
