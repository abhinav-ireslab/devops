package com.ireslab.coinclaim;

import java.util.Arrays;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.coinclaim.service.impl.ClientDetailsServiceImpl;

/**
 * @author iRESlab
 *
 */
@Configuration
@EnableConfigurationProperties
public class ApplicationConfiguration {

	/**
	 * Spring data source configuration bean
	 * 
	 * @return
	 */
	@Bean(name = "OAuth")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource getDataSource() {

		return DataSourceBuilder.create().type(DriverManagerDataSource.class).build();
	}

	/**
	 * Client details service bean
	 * 
	 * @return
	 */
	@Primary
	@Bean(name = "clientDetailsServiceImpl")
	public ClientDetailsService getClientDetailsService() {
		return new ClientDetailsServiceImpl();
	}

	/**
	 * Jackson Mapper object writer bean
	 * 
	 * @return
	 */
	@Bean(name = "objectWriter")
	public ObjectWriter getObjectWriter() {
		return new ObjectMapper().writerWithDefaultPrettyPrinter();
	}

	/**
	 * Model mapper bean
	 * 
	 * @return
	 */
	@Bean(name = "modelMapper")
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	/**
	 * Spring rest template
	 * 
	 * @return
	 */
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

		converter.setSupportedMediaTypes(
				Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM }));

		restTemplate.setMessageConverters(Arrays.asList(converter, new FormHttpMessageConverter()));
		return restTemplate;
	}

}
