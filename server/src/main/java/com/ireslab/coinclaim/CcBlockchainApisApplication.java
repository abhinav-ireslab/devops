package com.ireslab.coinclaim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author iRESlab
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.ireslab.coinclaim")
@EnableConfigurationProperties
public class CcBlockchainApisApplication extends SpringBootServletInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.boot.web.servlet.support.SpringBootServletInitializer#
	 * configure(org.springframework.boot.builder.SpringApplicationBuilder)
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CcBlockchainApisApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(CcBlockchainApisApplication.class, args);
	}
}
