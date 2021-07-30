package com.coco.terminal.cocobizlog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"com.coco.terminal.cocobizlog.dal"})
public class DataSourceConfig {
}
