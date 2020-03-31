package com.companyname.parking.api;

import com.companyname.parking.api.infrastructure.spring.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class ParkingApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingApiServiceApplication.class, args);
	}

}
