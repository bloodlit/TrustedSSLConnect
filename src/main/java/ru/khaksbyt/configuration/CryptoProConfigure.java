package ru.khaksbyt.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.CryptoPro.JCP.KeyStore.StoreInputStream;
import ru.CryptoPro.JCP.Util.JCPInit;
import ru.CryptoPro.JCPxml.XmlInit;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
@Configuration
public class CryptoProConfigure {
    @Value("${application.store.trust.type:CertStore}")
    private String trustStoreType;

    @Value("${application.store.trust.file:#{null}}")
    private String trustStoreFile;

    @Value("${application.store.trust.name:#{null}}")
    private String trustStoreName;

    @Value("${application.store.trust.password}")
    private String trustStorePassword;

    @Value("${application.store.key.type:REGISTRY}")
    private String keyStoreType;

    @Value("${application.store.key.alias:#{null}}")
    private String keyStoreAlias;

    @Value("${application.store.key.password:#{null}}")
    private String keyStorePassword;

    private PrivateKey privateKey;
    private X509Certificate certificate;

    private SSLContext sSLContext;

    public CryptoProConfigure() {
        //включаем проверка цепочки сертификатов на отзыв по CRLDP сертификата
        System.setProperty("com.sun.security.enableCRLDP", "true");
        System.setProperty("com.ibm.security.enableCRLDP", "true");

        Security.setProperty("ssl.KeyManagerFactory.algorithm", "GostX509");
        Security.setProperty("ssl.TrustManagerFactory.algorithm", "GostX509");
    }

    @PostConstruct
    public void init() throws Exception {
        // инициализируем providers JCSP
        JCPInit.initProviders(true);

        final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        if (trustStoreFile == null || "".equals(trustStoreFile))
            loadKeyStoreByName(trustStore);
        else
            loadKeyStoreByFile(trustStore);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("GostX509", "JTLS");
        final KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        if (keyStoreAlias != null && !keyStoreAlias.isEmpty())
            keyStore.load(new StoreInputStream(keyStoreAlias), keyStorePassword.toCharArray());
        else
            keyStore.load(null, null);

        kmf.init(keyStore, keyStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("GostX509", "JTLS");
        tmf.init(trustStore);

        // Ключ подписи.
        this.privateKey = (PrivateKey) keyStore.getKey(keyStoreAlias, keyStorePassword.toCharArray());
        if (this.privateKey == null) {
            log.error("Приватнрый ключ не найден: " + keyStoreAlias, new Exception());
        }
        log.debug("Приватный ключ: \"" + keyStoreType + "\\" + keyStoreAlias + "\" алгоритм: " + this.privateKey.getAlgorithm());

        // Сертификат для проверки.
        this.certificate = (X509Certificate) keyStore.getCertificate(keyStoreAlias);
        log.debug("Сертификат: " + certificate.getIssuerX500Principal().getName());

        sSLContext = SSLContext.getInstance("GostTLS", "JTLS");
        sSLContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        XmlInit.init();
    }

    private void loadKeyStoreByFile(KeyStore store) throws KeyStoreException {
        try {
            try (FileInputStream fis = new FileInputStream(trustStoreFile)) {
                store.load(fis, trustStorePassword.toCharArray());
                log.debug("Загружен KeyStore из фала: " + trustStoreFile);
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new KeyStoreException("Не возможно загрузить KeyStore из файла: " + trustStoreFile, e);
        }
    }

    private void loadKeyStoreByName(KeyStore store) throws KeyStoreException {
        try {
            store.load(new ByteArrayInputStream(trustStoreName.getBytes(StandardCharsets.UTF_8)), trustStorePassword.toCharArray());
            log.debug("Загружен KeyStore по имени: " + trustStoreName);
        } catch (IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new KeyStoreException("Не возможно загрузить KeyStore по имени: " + trustStoreName, e);
        }
    }

    @Bean
    public PrivateKey privateKey() {
        return this.privateKey;
    }

    @Bean
    public X509Certificate certificate() {
        return this.certificate;
    }

    /**
     * Защищенный контекст соединения.
     *
     * @return
     */
    @Bean
    public SSLContext getsSLContext() {
        return this.sSLContext;
    }
}
