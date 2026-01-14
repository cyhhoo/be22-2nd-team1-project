package com.mycompany.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = "com.mycompany.project")
@EntityScan(basePackages = "com.mycompany.project")
@MapperScan(basePackages = "com.mycompany.project", annotationClass = Mapper.class)
@ComponentScan(basePackages = {
        "com.mycompany.schedule",
        "com.mycompany.project.schedule",
        "com.mycompany.project.security",
        "com.mycompany.project.common",
        "com.mycompany.project.exception"
})
public class ScheduleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduleServiceApplication.class, args);
    }
}
