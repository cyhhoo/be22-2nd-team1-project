package com.mycompany.attendance;

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
@EnableFeignClients(basePackages = "com.mycompany.project.attendance.client", defaultConfiguration = GlobalFeignConfig.class)
@EnableJpaRepositories(basePackages = "com.mycompany.project")
@EntityScan(basePackages = "com.mycompany.project")
@MapperScan(basePackages = "com.mycompany.project", annotationClass = Mapper.class // @Mapper 어노테이션이 붙은 것만 스캔하여 JPA
                                                                                   // 리포지토리와 충돌 방지
)
@ComponentScan(basePackages = {
        "com.mycompany.attendance",
        "com.mycompany.project.attendance",
        "com.mycompany.project.security",
        "com.mycompany.project.common",
        "com.mycompany.project.exception"
})
public class AttendanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceServiceApplication.class, args);
    }
}
