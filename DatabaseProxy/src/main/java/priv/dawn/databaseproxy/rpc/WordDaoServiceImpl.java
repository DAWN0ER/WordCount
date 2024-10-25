package priv.dawn.databaseproxy.rpc;

import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.dawn.wordcount.api.WordDaoService;
import priv.dawn.databaseproxy.dao.service.WordCountDaoService;
import priv.dawn.wordcount.domain.FileWordCountDto;
import priv.dawn.wordcount.domain.WordCountDto;
import priv.dawn.databaseproxy.dao.dto.DaoWordCountDto;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/22:50
 */

@DubboService
public class WordDaoServiceImpl implements WordDaoService {

    private final Logger logger = LoggerFactory.getLogger(WordDaoServiceImpl.class);
    private final Gson gson = new Gson();

    @Resource
    private WordCountDaoService wordCountDaoService;

    @Override
    public List<String> saveWordCount(FileWordCountDto fileWordCountDto) {
        if(Objects.isNull(fileWordCountDto)){
            logger.error("[saveWordCount]输入参数为空");
            return null;
        }
        Integer fileUid = fileWordCountDto.getFileUid();
        if(Objects.isNull(fileUid) || fileUid<=0){
            logger.error("[saveWordCount]输入参数异常, fileUid:{}",fileUid);
            return null;
        }
        List<WordCountDto> wordCounts = fileWordCountDto.getWordCounts();
        if(CollectionUtils.isEmpty(wordCounts)){
            logger.error("[saveWordCount]输入参数异常, 保存 WordCount 列表为空");
            return null;
        }
        List<DaoWordCountDto> collect = wordCounts.stream().map(e -> {
            DaoWordCountDto daoWordCountDto = new DaoWordCountDto();
            daoWordCountDto.setWord(e.getWord());
            daoWordCountDto.setCount(e.getCount());
            return daoWordCountDto;
        }).collect(Collectors.toList());
        List<String> success = wordCountDaoService.saveWordCounts(fileUid, collect);
        logger.info("[saveWordCount]fileUid:{} 更新数量对比: 期望更新:{},实际更新:{}",fileUid,collect.size(),success.size());
        return success;
    }

    @Override
    public FileWordCountDto queryTopKWords(Integer fileUid, Integer K) {
        if (Objects.isNull(fileUid) || Objects.isNull(K) || fileUid <= 0 || K <= 0) {
            logger.error("[queryTopKWords]参数异常: fileUid:{}, K:{}", fileUid, K);
            return null;
        }
        logger.info("[queryTopKWords] 参数: fileUid:{}, K:{}",fileUid,K);
        // 还是不要超过 100 比较好
        int k = Math.min(K, 100);
        List<DaoWordCountDto> countDtoList = wordCountDaoService.queryTopKWords(fileUid, k);
        if (CollectionUtils.isEmpty(countDtoList)) {
            logger.warn("[queryTopKWords] 查询结果为空");
            return null;
        }
        FileWordCountDto fileWordCountDto = castToFileWordCountDto(fileUid, countDtoList);
        logger.info("[queryTopKWords] 调用结果, return:{}, fileUid:{},K:{}",
                gson.toJson(fileWordCountDto),fileUid,K);
        return fileWordCountDto;
    }

    @Override
    public FileWordCountDto queryWordCounts(Integer fileUid, List<String> words) {
        if(Objects.isNull(fileUid) || fileUid <=0 || CollectionUtils.isEmpty(words)){
            logger.error("[queryWordCounts] 参数异常:fileUid:{}, words is empty:{}",fileUid,CollectionUtils.isEmpty(words));
            return null;
        }
        logger.info("[queryWordCounts] 调用参数: fileUid:{}, words:{}",fileUid,words);
        List<DaoWordCountDto> countDtoList = wordCountDaoService.queryCountByWords(fileUid, words);
        if(CollectionUtils.isEmpty(countDtoList)){
            logger.warn("[queryWordCounts] 没有查到数据, fileUid:{}, words:{}",fileUid,words);
            FileWordCountDto temp = new FileWordCountDto();
            temp.setWordCounts(Collections.emptyList());
            temp.setFileUid(fileUid);
            return temp;
        }
        FileWordCountDto fileWordCountDto = castToFileWordCountDto(fileUid, countDtoList);
        logger.info("[queryWordCounts] 调用结果, return:{}, fileUid:{},words:{}",
                gson.toJson(fileWordCountDto),fileUid,words);
        return fileWordCountDto;

    }

    private FileWordCountDto castToFileWordCountDto(int fileUid,List<DaoWordCountDto> daoWordCountDtoList) {
        List<WordCountDto> collectWordCountDto = new ArrayList<>();
        if (CollectionUtils.isEmpty(daoWordCountDtoList)) {
            collectWordCountDto = daoWordCountDtoList.stream().map(e -> {
                WordCountDto wordCountDto = new WordCountDto();
                wordCountDto.setWord(e.getWord());
                wordCountDto.setCount(e.getCount());
                return wordCountDto;
            }).collect(Collectors.toList());
        }
        FileWordCountDto fileWordCountDto = new FileWordCountDto();
        fileWordCountDto.setWordCounts(collectWordCountDto);
        fileWordCountDto.setFileUid(fileUid);

        return fileWordCountDto;
    }
}
