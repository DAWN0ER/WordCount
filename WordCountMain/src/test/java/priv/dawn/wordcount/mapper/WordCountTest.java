package priv.dawn.wordcount.mapper;

import com.google.gson.Gson;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import priv.dawn.wordcount.dao.domain.WordCount;
import priv.dawn.wordcount.dao.domain.WordCountExample;
import priv.dawn.wordcount.dao.mapper.primary.WordCountMapper;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/14/15:38
 */

public class WordCountTest {

    private WordCountMapper mapper;

    private final Logger logger = LoggerFactory.getLogger(WordCountTest.class);

    @Before
    public void doBefore() {
        System.out.println("Before");

    }

    @Test
    public void topKTest() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(WordCountTest.class,TestMapperConfig.class);
        mapper = ac.getBean(WordCountMapper.class);
        int K = 3;
        int fileUid = 1234;
        for (int k = 1; k <= 4; k++) {
            WordCountExample example = new WordCountExample();
            example.createCriteria().andFileUidEqualTo(fileUid);
            example.setOrderByClause("cnt DESC LIMIT " + k);
            List<WordCount> counts = mapper.selectByExample(example);
            logger.info("k={}, and words={}", K, new Gson().toJson(counts));
        }
    }

    @TestConfiguration
    @MapperScan(basePackageClasses = WordCountMapper.class)
    class TestMapperConfig {

        @Bean(name = "myTestDatasource")
        @Qualifier("myTestDatasource")
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/word_count?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&serverTimezone=GMT%2B8");
            dataSource.setUsername("root");
            dataSource.setPassword("1234");
            return dataSource;
        }

        @Bean(name = "myTestSqlSessionFactory")
        public SqlSessionFactory sqlSessionFactory(@Qualifier("myTestDatasource") @Lazy DataSource dataSource) throws Exception { // @Lazy 避免循环依赖
            String path = "classpath:mappers/primary/WordCountMapper.xml";
            Resource resource = new PathMatchingResourcePatternResolver().getResource(path);
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(dataSource);
            bean.setMapperLocations(resource);
            return bean.getObject();
        }
    }

}
