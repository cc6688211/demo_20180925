package com.example.demo_20180925;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Demo20180925Application {

	public static void main(String[] args) {
		SpringApplication.run(Demo20180925Application.class, args);
	}
}
