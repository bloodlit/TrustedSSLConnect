package ru.khaksbyt;

import ru.CryptoPro.JCP.Util.JCPInit;
import ru.khaksbyt.sign.XmlXadesSign;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.security.Security;

public class SSLSocketClientJCP {

    private final SSLConfiguration configuration;
    private final SSLConnector connector;
    private final XmlXadesSign xmlXadesSign;

    public SSLSocketClientJCP() throws Exception {
        this.configuration = new SSLConfiguration();
        this.connector = new SSLConnector(this.configuration);
        this.connector.prepare(true);

        this.xmlXadesSign = new XmlXadesSign(connector.certificate(), connector.privateKey());
    }

    public static void main(String[] args) throws Exception {
        //включаем проверка цепочки сертификатов на отзыв по CRLDP сертификата
        System.setProperty("com.sun.security.enableCRLDP", "true");
        System.setProperty("com.ibm.security.enableCRLDP", "true");
        //System.setProperty("javax.net.ssl.supportGVO", "true");

        Security.setProperty("ssl.KeyManagerFactory.algorithm", "GostX509");
        Security.setProperty("ssl.TrustManagerFactory.algorithm", "GostX509");

        // инициализируем providers JCSP
        JCPInit.initProviders(true);

        SSLSocketClientJCP application = new SSLSocketClientJCP();
        application.init();
    }

    private void init() {
        try {
            URL url = new URL("https://api.dom.gosuslugi.ru/ext-bus-home-management-service/services/HomeManagementAsync");

            SSLContext sSLContext = connector.create();
            SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sSLSocketFactory);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpsURLConnection.setRequestProperty("Keep-Alive", "header");
            httpsURLConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

            httpsURLConnection.setRequestProperty("SOAPAction", "urn:getState");
            //httpsURLConnection.setRequestProperty("SOAPAction", "urn:exportHouseData");

            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setDoOutput(true);
            httpsURLConnection.setReadTimeout(300000);

            print_content(httpsURLConnection);

            httpsURLConnection.disconnect();
            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] streamToByteArray(InputStream is) throws IOException {
        if (is == null)
            return new byte[1];

        int c;
        byte[] b = new byte[1024];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        while ((c = is.read(b)) != -1) {
            stream.write(b, 0, c);
        }
        return stream.toByteArray();
    }

    private void print_content(HttpsURLConnection paramHttpsURLConnection) throws Exception {
        if (paramHttpsURLConnection != null) {
            System.out.println("****** Content of the URL ********");

            InputStream fis = getClass().getClassLoader().getResourceAsStream("req/getstate.xml");
            byte[] dataBytes = streamToByteArray(fis);

            //Подписываем документ
            OutputStream os = paramHttpsURLConnection.getOutputStream();

            //xmlXadesSign.sign(dataBytes, "signed-data-container", os);

            //String xmlString = new String(dataBytes, "UTF-8");
            System.out.println("Send packet: ");
            System.out.println(os);

            DataOutputStream dos = new DataOutputStream(os);
            dos.write(dataBytes);
            dos.flush();

            paramHttpsURLConnection.connect();

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                br = new BufferedReader(new InputStreamReader(paramHttpsURLConnection.getInputStream()));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                String connectionResponseMessage = paramHttpsURLConnection.getResponseMessage();
                System.out.println("ERROR!!! " + connectionResponseMessage);
                br = new BufferedReader(new InputStreamReader(paramHttpsURLConnection.getErrorStream()));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("RESULT: ");
            System.out.println(sb.toString());
            System.out.println("****** Content of the URL ********");
        }
    }
}
