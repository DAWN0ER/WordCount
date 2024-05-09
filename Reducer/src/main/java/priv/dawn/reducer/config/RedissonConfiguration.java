package priv.dawn.reducer.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.database}")
    private int db;

    @Bean("reducerRedisson")
    public RedissonClient reducerRedisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port)
                .setDatabase(db);
        return Redisson.create(config);
    }

}
