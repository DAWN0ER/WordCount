package priv.dawn.workers.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import priv.dawn.workers.pojo.ChunkDTO;

import java.util.List;

@Mapper
public interface ChunkReadMapper {

    @Select("SELECT chunk_id as chunkId,context FROM t_file_chunks  WHERE file_uid=#{uid} AND chunk_id BETWEEN #{begin} AND #{end};")
    List<ChunkDTO> getChunks(@Param("uid") int uid, @Param("begin") int begin, @Param("end") int end);

}
