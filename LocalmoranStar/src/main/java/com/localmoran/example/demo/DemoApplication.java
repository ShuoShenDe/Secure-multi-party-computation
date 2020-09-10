package com.localmoran.example.demo;

import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.security.auth.message.config.AuthConfigFactory;

@SpringBootApplication
@EnableSwagger2
@MapperScan("com.localmoran.example.demo.mapper")
public class DemoApplication  extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }


    public static void main(String[] args) {
        if (AuthConfigFactory.getFactory() == null) {
            AuthConfigFactory.setFactory(new AuthConfigFactoryImpl());
        }

        SpringApplication.run(DemoApplication.class, args);

    }
}
