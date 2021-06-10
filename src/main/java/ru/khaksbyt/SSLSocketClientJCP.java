package ru.khaksbyt;

import ru.CryptoPro.JCSP.JCSP;
import ru.CryptoPro.ssl.Provider;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Security;

public class SSLSocketClientJCP {
    private final SSLConfiguration configuration;
    private final SSLConnector connector;

    public SSLSocketClientJCP() {
        this.configuration = new SSLConfiguration();
        this.connector = new SSLConnector(this.configuration);
    }

    public static void main(String[] args) throws Exception {
        //включаем проверка цепочки сертификатов на отзыв по CRLDP сертификата
        //System.setProperty("ru.CryptoPro.reprov.enableCRLDP", "true");
        System.setProperty("com.sun.security.enableCRLDP", "true");
        //System.setProperty("com.ibm.security.enableCRLDP", "true");
        System.setProperty("javax.net.ssl.supportGVO", "true");

        //Security.setProperty("ssl.KeyManagerFactory.algorithm", "GostX509");
        //Security.setProperty("ssl.TrustManagerFactory.algorithm", "GostX509");

        // инициализируем providers JCSP
        Security.addProvider(new JCSP());
        Security.addProvider(new Provider());

        SSLSocketClientJCP application = new SSLSocketClientJCP();
        application.init();
    }

    private void init() {
        try {
            URL url = new URL("https://api.dom.gosuslugi.ru/ext-bus-home-management-service/services/HomeManagementAsync");

            connector.prepare(false);
            SSLContext sSLContext = connector.create();
            SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();

            HttpsURLConnection.setDefaultSSLSocketFactory(sSLSocketFactory);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("Keep-Alive", "header");
            httpsURLConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setReadTimeout(300000);

            httpsURLConnection.connect();
            print_content(httpsURLConnection);
            httpsURLConnection.disconnect();
            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print_content(HttpsURLConnection paramHttpsURLConnection) {
        if (paramHttpsURLConnection != null)
            try {
                System.out.println("****** Content of the URL ********");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramHttpsURLConnection.getInputStream()));
                String str;
                while ((str = bufferedReader.readLine()) != null)
                    System.out.println(str);
                bufferedReader.close();
                System.out.println("****** Content of the URL ********");
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
    }
}
