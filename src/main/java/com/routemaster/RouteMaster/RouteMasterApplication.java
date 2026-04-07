package com.routemaster.RouteMaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RouteMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteMasterApplication.class, args);
	}

}
