package priv.dawn.wordcount.api;

import priv.dawn.wordcount.domain.FileWordCountDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 分词存储接口
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:40
 */
public interface WordDaoService {

    /**
     * 批量存入接口
     * @param fileWordCountDto 输入数据传输实例
     * @return 成功的 Word
     */
    List<String> saveWordCount(FileWordCountDto fileWordCountDto);

    /**
     * 查询前 K 个热点词
     * @param fileUid 文件 ID
     * @param K 前 K 个热点词
     * @return 数据传输实例
     */
    FileWordCountDto queryTopKWords(Integer fileUid, Integer K);

    /**
     * 查找对应词的 Count
     * @param fileUid 文件ID
     * @param words 查找的词
     * @return 数据传输实例
     */
    FileWordCountDto queryWordCounts(Integer fileUid, List<String> words);

}
