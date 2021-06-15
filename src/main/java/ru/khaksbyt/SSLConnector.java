package ru.khaksbyt;

import ru.CryptoPro.JCP.KeyStore.StoreInputStream;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class SSLConnector {
    private final SSLConfiguration configuration;
    private final String ALGORITHM = "GostX509";
    private final String PROVIDER = "JTLS";

    private KeyStore keyStore = null;
    private KeyStore trustStore = null;
    private KeyManagerFactory kmf = null;
    private TrustManagerFactory tmf = null;

    public SSLConnector(SSLConfiguration configuration) {
        this.configuration = configuration;
    }

    public void prepare(boolean auth) throws Exception {
        this.trustStore = KeyStore.getInstance(this.configuration.getTrustStoreType());
        this.trustStore.load(
                getClass().getClassLoader().getResourceAsStream(this.configuration.getTrustStoreFile()),
                this.configuration.getTrustStorePassword());

        if (auth) {
            this.kmf = KeyManagerFactory.getInstance(ALGORITHM, PROVIDER);
            this.keyStore = KeyStore.getInstance(this.configuration.getKeyStoreType());
            if (this.configuration.getKeyAlias() != null && !this.configuration.getKeyAlias().isEmpty())
                this.keyStore.load(new StoreInputStream(this.configuration.getKeyAlias()), this.configuration.getKeyPassword());
            else
                this.keyStore.load(null, null);

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                System.out.println("alias name: " + alias);
                Certificate certificate = keyStore.getCertificate(alias);
                System.out.println("Signer found: " + ((X509Certificate)certificate).getSubjectX500Principal().getName());

                PrivateKey privateKey = null;
                Key key = keyStore.getKey(alias, this.configuration.getKeyPassword());
                if (key != null) {
                    privateKey = (PrivateKey) key;
                } else {
                    System.out.println("Private key not found: " + aliases);
                    throw new Exception();
                }
            }
            this.kmf.init(this.keyStore, this.configuration.getKeyPassword());
        }

        this.tmf = TrustManagerFactory.getInstance(ALGORITHM, PROVIDER);
        this.tmf.init(this.trustStore);
    }

    public SSLContext create() throws Exception {
        SSLContext sSLContext = SSLContext.getInstance("GostTLS", PROVIDER);
        sSLContext.init(
                this.kmf != null ? this.kmf.getKeyManagers() : null,
                this.tmf.getTrustManagers(),
                null);
        return sSLContext;
    }
}
