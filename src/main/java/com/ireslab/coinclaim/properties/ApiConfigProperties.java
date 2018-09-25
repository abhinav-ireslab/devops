package com.ireslab.coinclaim.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author iRESlab
 *
 */
@Component
@PropertySource(value = "classpath:cc_api_config.properties")
@ConfigurationProperties
public class ApiConfigProperties {

	private OAuth2Config oauth2;

	/**
	 * @return the oauth2
	 */
	public OAuth2Config getOauth2() {
		return oauth2;
	}

	/**
	 * @param oauth2
	 *            the oauth2 to set
	 */
	public void setOauth2(OAuth2Config oauth2) {
		this.oauth2 = oauth2;
	}

	/**
	 * @author iRESlab
	 *
	 */
	public static class OAuth2Config {

		private String clientApiKey;
		private String clientApiSecret;

		private String resourceIds;
		private String scopes;
		private String grantTypes;
		private String accessTokenValidity;
		private String authorities;
		private String additionalInformation;

		/**
		 * @return the clientApiKey
		 */
		public String getClientApiKey() {
			return clientApiKey;
		}

		/**
		 * @param clientApiKey
		 *            the clientApiKey to set
		 */
		public void setClientApiKey(String clientApiKey) {
			this.clientApiKey = clientApiKey;
		}

		/**
		 * @return the clientApiSecret
		 */
		public String getClientApiSecret() {
			return clientApiSecret;
		}

		/**
		 * @param clientApiSecret
		 *            the clientApiSecret to set
		 */
		public void setClientApiSecret(String clientApiSecret) {
			this.clientApiSecret = clientApiSecret;
		}

		/**
		 * @return the resourceIds
		 */
		public String getResourceIds() {
			return resourceIds;
		}

		/**
		 * @param resourceIds
		 *            the resourceIds to set
		 */
		public void setResourceIds(String resourceIds) {
			this.resourceIds = resourceIds;
		}

		/**
		 * @return the scopes
		 */
		public String getScopes() {
			return scopes;
		}

		/**
		 * @param scopes
		 *            the scopes to set
		 */
		public void setScopes(String scopes) {
			this.scopes = scopes;
		}

		/**
		 * @return the grantTypes
		 */
		public String getGrantTypes() {
			return grantTypes;
		}

		/**
		 * @param grantTypes
		 *            the grantTypes to set
		 */
		public void setGrantTypes(String grantTypes) {
			this.grantTypes = grantTypes;
		}

		/**
		 * @return the accessTokenValidity
		 */
		public String getAccessTokenValidity() {
			return accessTokenValidity;
		}

		/**
		 * @param accessTokenValidity
		 *            the accessTokenValidity to set
		 */
		public void setAccessTokenValidity(String accessTokenValidity) {
			this.accessTokenValidity = accessTokenValidity;
		}

		/**
		 * @return the authorities
		 */
		public String getAuthorities() {
			return authorities;
		}

		/**
		 * @param authorities
		 *            the authorities to set
		 */
		public void setAuthorities(String authorities) {
			this.authorities = authorities;
		}

		/**
		 * @return the additionalInformation
		 */
		public String getAdditionalInformation() {
			return additionalInformation;
		}

		/**
		 * @param additionalInformation
		 *            the additionalInformation to set
		 */
		public void setAdditionalInformation(String additionalInformation) {
			this.additionalInformation = additionalInformation;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "OAuth2Config [clientApiKey=" + clientApiKey + ", clientApiSecret=" + clientApiSecret
					+ ", resourceIds=" + resourceIds + ", scopes=" + scopes + ", grantTypes=" + grantTypes
					+ ", accessTokenValidity=" + accessTokenValidity + ", authorities=" + authorities
					+ ", additionalInformation=" + additionalInformation + "]";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ApiConfigProperties [oauth2=" + oauth2 + "]";
	}

}
