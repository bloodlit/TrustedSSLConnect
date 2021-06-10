package ru.khaksbyt;

public class SSLConfiguration {
    //контейнер с доверительными сертификатами УЦ сервера
    private final String trustStoreType = "CertStore";
    private final String trustStoreFile = "data/trustStoreJCP.jks";
    private final String trustStorePassword = "12345678";

    //
    private final String keyStoreType = "REGISTRY";
    private final String keyAlias = "";
    private final String keyStorePassword = "12345678";

    public String getTrustStoreType() {
        return this.trustStoreType;
    }

    public String getTrustStoreFile() {
        return trustStoreFile;
    }

    public char[] getTrustStorePassword() {
        return trustStorePassword != null ? trustStorePassword.toCharArray() : null;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public char[] getKeyPassword() {
        return keyStorePassword != null ? keyStorePassword.toCharArray() : null;
    }
}
