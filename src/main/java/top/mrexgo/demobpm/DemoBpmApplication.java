package top.mrexgo.demobpm;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author mrexgo
 */
@SpringBootApplication
public class DemoBpmApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoBpmApplication.class, args);
    }

    @Bean
    public Snowflake idGenerator() {
        return IdUtil.getSnowflake(1, 1);
    }

}
