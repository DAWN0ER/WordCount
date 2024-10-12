package priv.dawn.wordcount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Deprecated
@Data
@AllArgsConstructor
public class FileChunkDTO {
    private int chunkId;
    private String context;
}
