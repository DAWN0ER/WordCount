package priv.dawn.reducer.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgOrderMapper {

    @Insert("INSERT INTO t_msg_key(file_uid, chunk_id, part, part_num) VALUE(#{arg0},#{arg1},#{arg2},#{arg3})")
    void saveNewOrder(int fileUID,int chunkID,int partition,int partitionNum);

}
