package com.example.emos.wx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class EmosWxServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmosWxServerApplication.class, args);
    }

}
