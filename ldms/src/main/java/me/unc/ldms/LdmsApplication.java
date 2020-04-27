package me.unc.ldms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 毕设项目 -- 物流配送管理系统
 * @author LZS
 */
@SpringBootApplication
@MapperScan("me.unc.ldms.mapper")
public class LdmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LdmsApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
