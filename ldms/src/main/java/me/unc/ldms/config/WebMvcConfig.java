package me.unc.ldms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description mvc配置类
 * @Date 2020/6/2 22:11
 * @author LZS
 * @version v1.0
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Bean
    public FormContentFilter FormContentFilter() {
        return new FormContentFilter();
    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Date>() {
            @Override
            public Date convert(String s) {
                if ("".equals(s) || s == null) {
                    return null;
                }
                try {
                    return simpleDateFormat.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
