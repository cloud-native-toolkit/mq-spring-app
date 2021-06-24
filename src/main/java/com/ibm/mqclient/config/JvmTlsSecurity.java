package com.ibm.mqclient.config;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("securemq")
@Configuration
public class JvmTlsSecurity {
	
	public JvmTlsSecurity() {
		super();
	}

	final Logger LOG = LoggerFactory.getLogger(JvmTlsSecurity.class);
	
	@Value("${app.keystore}")
    private String keyStore;
	
	@Value("${app.keystore-pw}")
    private String keyStorePassword;
	
	@Value("${app.truststore}")
    private String trustStore;
	
	@Value("${app.truststore-pw}")
    private String trustStorePassword;
	
    @PostConstruct
    public void init() {
    	        
    	LOG.debug("Initializing the application");
        
        LOG.debug("Setting the javax.net.ssl.keyStore from keyStore file: {}", keyStore );

        // For mTLS, this MQ client app needs to send a certificate to the MQM server
		System.setProperty("javax.net.ssl.keyStore", keyStore);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
		
        
        LOG.debug("Setting the javax.net.ssl.trustStore from keyStore file: {}", trustStore );
        
        // The trust store has the CA certificate that signed the MQM server certificate.
        // This is required to setup a TLS connection with the MQM server
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);


		System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
    	
	}

}
