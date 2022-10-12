package io.spring.auth.center.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Created 2022/10/12 16:28
 **/
@Configuration
@MapperScan("io.spring.auth.center.dao")
public class MybatisPlusConfig {

}
