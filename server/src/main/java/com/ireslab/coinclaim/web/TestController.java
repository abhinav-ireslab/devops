package com.ireslab.coinclaim.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ireslab.coinclaim.properties.ApiConfigProperties;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping("/test")
public class TestController {

	private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

	@Autowired
	ApiConfigProperties apiConfig;

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String testStartup() {

		LOG.info("Server is up and running . . . . " + apiConfig.getOauth2().getClientApiKey());
		return "Server is up and running . . . . ";
	}

	public static void main(String[] args) {
		System.out.println(new BCryptPasswordEncoder().encode("coinclaim"));
	}

	@RequestMapping(value = "/test")
	public String testMethod() {
		System.out.println("TestController.testMethod()");
		return "success";
	}
}
