package priv.dawn.wordcountmain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.dawn.wordcountmain.mapper.FileMapper;
import priv.dawn.wordcountmain.pojo.dto.FileChunkDTO;
import priv.dawn.wordcountmain.pojo.dto.FileInfoDTO;
import priv.dawn.wordcountmain.pojo.vo.FileTextVO;
import priv.dawn.wordcountmain.service.FileStorageService;
import priv.dawn.wordcountmain.utils.UidGenerator;

import java.util.ArrayList;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    @Transactional
    public int saveFile(FileTextVO fileText) {

        // divide into chunks
        ArrayList<FileChunkDTO> dtos = new ArrayList<>(fileText.getContext().length() / 1000);
        int begin = 0;
        int id = 1;
        while (begin < fileText.getContext().length() - 1000) {
            int end = fileText.getContext().indexOf('，', begin + 1000);
            String chunk = fileText.getContext().substring(begin, end + 1);
            dtos.add(new FileChunkDTO(id++, chunk));
            begin = end + 1;
        }
        String chunk = fileText.getContext().substring(begin);
        dtos.add(new FileChunkDTO(id, chunk));

        // 理论上生成 UID 是通过分布式 UID 算法生成的, 但是前提假设这个 UID 是不需要我们实现的
        // 所以简单用 UUID+Hash 实现
        int uid = UidGenerator.getOneUid();
        int chunkNum = dtos.size();
        FileInfoDTO fileInfoDTO = new FileInfoDTO(fileText.getFilename(), uid, chunkNum);

        boolean flag = false;
        int loop = 3;
        while (loop-->0 || flag){
            try {
                fileMapper.saveFileInfo(fileInfoDTO);
                flag = true; // 只要没报错就可以来到这
            } catch (DuplicateKeyException e) {
                uid = UidGenerator.getOneUid();
                fileInfoDTO.setUid(uid);
                // 重试
            }
        }

        //noinspection ConstantConditions
        if (!flag) return -1;

        for (FileChunkDTO chunkDTO : dtos)
            fileMapper.saveFileChunk(fileInfoDTO.getUid(), chunkDTO.getChunkId(), chunkDTO.getContext());

        return fileInfoDTO.getUid();
    }

    @Override
    public String getContext(int fileUID) {
        return String.join("", fileMapper.getALLContextByUid(fileUID));
    }
}
