package com.ireslab.coinclaim.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author iRESlab
 *
 */
@Component
@PropertySource(value = "classpath:node_config.properties")
@ConfigurationProperties(prefix = "node")
public class NodeConfigProperties {

	private String baseUrl;
	private String btcBalanceEndpoint;
	private String ethBalanceEndpoint;
	private String addressGenerationEndpoint;
	private String btcTransferEndpoint;
	private String privateKeyDerivationEndpoint;

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl
	 *            the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the btcBalanceEndpoint
	 */
	public String getBtcBalanceEndpoint() {
		return btcBalanceEndpoint;
	}

	/**
	 * @param btcBalanceEndpoint
	 *            the btcBalanceEndpoint to set
	 */
	public void setBtcBalanceEndpoint(String btcBalanceEndpoint) {
		this.btcBalanceEndpoint = btcBalanceEndpoint;
	}

	/**
	 * @return the ethBalanceEndpoint
	 */
	public String getEthBalanceEndpoint() {
		return ethBalanceEndpoint;
	}

	/**
	 * @param ethBalanceEndpoint
	 *            the ethBalanceEndpoint to set
	 */
	public void setEthBalanceEndpoint(String ethBalanceEndpoint) {
		this.ethBalanceEndpoint = ethBalanceEndpoint;
	}

	/**
	 * @return the addressGenerationEndpoint
	 */
	public String getAddressGenerationEndpoint() {
		return addressGenerationEndpoint;
	}

	/**
	 * @param addressGenerationEndpoint
	 *            the addressGenerationEndpoint to set
	 */
	public void setAddressGenerationEndpoint(String addressGenerationEndpoint) {
		this.addressGenerationEndpoint = addressGenerationEndpoint;
	}

	/**
	 * @return the btcTransferEndpoint
	 */
	public String getBtcTransferEndpoint() {
		return btcTransferEndpoint;
	}

	/**
	 * @param btcTransferEndpoint
	 *            the btcTransferEndpoint to set
	 */
	public void setBtcTransferEndpoint(String btcTransferEndpoint) {
		this.btcTransferEndpoint = btcTransferEndpoint;
	}

	/**
	 * @return the privateKeyDerivationEndpoint
	 */
	public String getPrivateKeyDerivationEndpoint() {
		return privateKeyDerivationEndpoint;
	}

	/**
	 * @param privateKeyDerivationEndpoint
	 *            the privateKeyDerivationEndpoint to set
	 */
	public void setPrivateKeyDerivationEndpoint(String privateKeyDerivationEndpoint) {
		this.privateKeyDerivationEndpoint = privateKeyDerivationEndpoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeConfigProperties [baseUrl=" + baseUrl + ", btcBalanceEndpoint=" + btcBalanceEndpoint
				+ ", addressGenerationEndpoint=" + addressGenerationEndpoint + "]";
	}
}
