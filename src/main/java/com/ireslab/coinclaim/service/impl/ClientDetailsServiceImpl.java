package com.ireslab.coinclaim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import com.ireslab.coinclaim.properties.ApiConfigProperties;
import com.ireslab.coinclaim.properties.ApiConfigProperties.OAuth2Config;

/**
 * @author iRESlab
 *
 */
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService {

	@Autowired
	private ApiConfigProperties ccApiConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.oauth2.provider.ClientDetailsService#
	 * loadClientByClientId(java.lang.String)
	 */
	@Override
	public ClientDetails loadClientByClientId(String clientApiKey) throws ClientRegistrationException {
		
		BaseClientDetails baseClientDetails = null;
		OAuth2Config clientCredentials = ccApiConfig.getOauth2();

		if (clientCredentials.getClientApiKey().equalsIgnoreCase(clientApiKey)) {
			baseClientDetails = new BaseClientDetails(clientCredentials.getClientApiKey(),
					clientCredentials.getResourceIds(), clientCredentials.getScopes(),
					clientCredentials.getGrantTypes(), clientCredentials.getAuthorities());

			baseClientDetails
					.setAccessTokenValiditySeconds(Integer.parseInt(clientCredentials.getAccessTokenValidity()));
			baseClientDetails.setClientSecret(clientCredentials.getClientApiSecret());
		}

		return baseClientDetails;
	}
}
