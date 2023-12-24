package com.mtls.mtlsspring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
public class RestClientConfig {

    private static final String PKCS12_FILE_PATH = "C:\\Users\\nadarajah.h\\Desktop\\TVs\\STARTIMES\\centenarybank-mtls\\centenarybank-mtls\\badssl.com-client.p12";
    private static final String PKCS12_PASSWORD = "badssl.com";
    private static final String TRUSTSTORE_FILE_PATH = "C:\\Users\\nadarajah.h\\Desktop\\TVs\\STARTIMES\\centenarybank-mtls\\centenarybank-mtls\\truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "sunrise";

    @Bean
    public RestTemplate restTemplate() {
        SSLContext sslContext = configureSSLContext();
        return new RestTemplate(createRequestFactory(sslContext));
    }

    private SSLContext configureSSLContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] keyStorePassword = PKCS12_PASSWORD.toCharArray();
            keyStore.load(new FileInputStream(PKCS12_FILE_PATH), keyStorePassword);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword);

            KeyStore trustStore = KeyStore.getInstance("JKS");
            char[] trustStorePassword = TRUSTSTORE_PASSWORD.toCharArray();
            trustStore.load(new FileInputStream(TRUSTSTORE_FILE_PATH), trustStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            return sslContext;
        } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | UnrecoverableKeyException | KeyManagementException e) {
            throw new RuntimeException("Error configuring SSLContext", e);
        }
    }

    private ClientHttpRequestFactory createRequestFactory(SSLContext sslContext) {
        return new CustomRequestFactory(sslContext);
    }

    private static class CustomRequestFactory extends SimpleClientHttpRequestFactory {

        private final SSLContext sslContext;

        public CustomRequestFactory(SSLContext sslContext) {
            this.sslContext = sslContext;
        }

        @Override
        protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod) throws IOException {
            if (connection instanceof javax.net.ssl.HttpsURLConnection) {
                ((javax.net.ssl.HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
            }
            super.prepareConnection(connection, httpMethod);
        }
    }
}
