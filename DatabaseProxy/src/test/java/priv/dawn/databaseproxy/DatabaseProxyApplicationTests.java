package priv.dawn.databaseproxy;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import priv.dawn.databaseproxy.dao.dto.DaoFileInfoDto;
import priv.dawn.databaseproxy.dao.service.FileStoreDaoService;

import javax.annotation.Resource;


@SpringBootTest
class DatabaseProxyApplicationTests {

    @Resource
    private FileStoreDaoService fileStoreDaoService;

    @Test
    void contextLoads() {
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(1635622473);
        System.out.println("fileInfo = " + new Gson().toJson(fileInfo));
    }

}
