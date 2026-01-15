package com.mycompany.project;

import com.mycompany.project.common.config.GlobalFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(defaultConfiguration = GlobalFeignConfig.class)
@EnableJpaRepositories(basePackages = "com.mycompany.project")
@EntityScan(basePackages = "com.mycompany.project")
@MapperScan(basePackages = "com.mycompany.project", annotationClass = Mapper.class)
@ComponentScan(basePackages = {
        "com.mycompany.project.attendance",
        "com.mycompany.project.common",
        "com.mycompany.project.security",
        "com.mycompany.project.exception"
})
public class AttendanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceServiceApplication.class, args);
    }
}
