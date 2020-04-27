package me.unc.ldms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description 线程池配置类
 * @Date 2020/2/12 17:11
 * @author LZS
 * @version v1.0
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(8);
    }

}
