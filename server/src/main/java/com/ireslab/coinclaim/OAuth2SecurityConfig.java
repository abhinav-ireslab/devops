package com.ireslab.coinclaim;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * OAUTH2 related security configuration for authorization and resource servers
 * 
 * @author iRESlab
 *
 */
public class OAuth2SecurityConfig {

	/**
	 * Authorization server configurations - Underlying authorization end point
	 * (authentication manager & token store), security (password encoder) and
	 * service configurations
	 * 
	 * @author iRESlab
	 *
	 */
	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Resource(name = "OAuth")
		@Autowired
		private DataSource dataSource;

		@Autowired
		@Qualifier("clientDetailsService")
		private ClientDetailsService clientDetailsService;

		/*
		 * @Autowired private AuthenticationManager authenticationManager;
		 */

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * AuthorizationServerConfigurerAdapter#configure(org.springframework.security.
		 * oauth2.config.annotation.web.configurers.
		 * AuthorizationServerEndpointsConfigurer)
		 */
		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			// endpoints.authenticationManager(authenticationManager);
			endpoints.tokenStore(tokenStore());
			//super.configure(endpoints);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * AuthorizationServerConfigurerAdapter#configure(org.springframework.security.
		 * oauth2.config.annotation.web.configurers.
		 * AuthorizationServerSecurityConfigurer)
		 */
		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			security.passwordEncoder(passwordEncoder());
			//super.configure(security);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * AuthorizationServerConfigurerAdapter#configure(org.springframework.security.
		 * oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer)
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService);
			//super.configure(clients);
		}

		/**
		 * BCryptPasswordEncoder as default password encoder
		 * 
		 * @return
		 */
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		/**
		 * Datasource as Jdbc Token store
		 * 
		 * @return
		 */
		@Bean
		public TokenStore tokenStore() {
			return new JdbcTokenStore(dataSource);
		}

	}

	/**
	 * Resource server configurations
	 * 
	 * @author iRESlab
	 *
	 */
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		private static final String RESOURCE_ID = "ccBlockchainRestAPI";

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * ResourceServerConfigurerAdapter#configure(org.springframework.security.config
		 * .annotation.web.builders.HttpSecurity)
		 */
		@Override
		public void configure(HttpSecurity http) throws Exception {

			http.authorizeRequests().antMatchers("/**").permitAll();
			http.authorizeRequests().anyRequest().authenticated();
			//super.configure(http);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.oauth2.config.annotation.web.configuration.
		 * ResourceServerConfigurerAdapter#configure(org.springframework.security.oauth2
		 * .config.annotation.web.configurers.ResourceServerSecurityConfigurer)
		 */
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.resourceId(RESOURCE_ID).stateless(false);
			// super.configure(resources);
		}
	}

}
