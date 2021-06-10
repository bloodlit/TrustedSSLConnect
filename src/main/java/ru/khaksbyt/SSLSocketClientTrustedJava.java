package ru.khaksbyt;

/*
 * Copyright(C) 1999-2019 ООО <<Цифровые технологии>>
 *
 * Этот файл содержит информацию, являющуюся
 * собственностью компании ООО <<Цифровые технологии>>.
 *
 * Любая часть этого файла не может быть скопирована,
 * исправлена, переведена на другие языки,
 * локализована или модифицирована любым способом,
 * откомпилирована, передана по сети или на
 * любую компьютерную систему без предварительного
 * заключения соглашения с ООО <<Цифровые технологии>>.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.security.Principal;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLSocketClientTrustedJava {

    public static void main(String[] args) throws Exception {

        /*
         * set default factory
         * needed for ReadFromHTTPS(URL url)
         */
        Security.setProperty("ssl.SocketFactory.provider", "com.digt.trusted.jsse.provider.DigtSocketFactory");
        Security.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

        /*
         * Setup client authenticated-params
         *
         * path to client certificate
         * certificate password
         *
        System.setProperty("com.digt.trusted.jsse.certFile", "client.cer");
        System.setProperty("com.digt.trusted.jsse.keyPasswd", "");
         *
         */
        System.setProperty("com.digt.trusted.jsse.server.certFile", "c://project/shargina.cer");
        System.setProperty("com.digt.trusted.jsse.server.keyFile", "c://client.key");

        /*
         * get factory
         *
         * from SSLContext
         *
        SSLContext ssl_cnt = SSLContext.getInstance("GostTLS");
        SSLSocketFactory factory1 = ssl_cnt.getSocketFactory();
         *
         * or from default factory
         *
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
         *
         */
/*
        SSLContext ssl_cnt1 = SSLContext.getInstance("GostTLS");
        SSLSocketFactory factory1 = ssl_cnt1.getSocketFactory();
        System.setProperty("com.digt.trusted.jsse.certFile", "");
        SSLSocket s1 =  (SSLSocket)factory1.createSocket("vm-oamvd.ct.local", 443);
        System.out.println("test unauthenticated Client - Socket1");
        //s1.startHandshake();
        ReadFromSocket(s1, "/");
        AboutSocket("Socket 1: ", s1);
        s1.close();

        String shost,url;
        int sport;
        */
/*
         *
        shost = "vm-websphere7.ct.local";
        sport = 443;
        url = "/";
        url = "/TJ-tlcert/";
         *//*

        */
/*
         *
        shost = "vm-vib.ct.local";
        //sport = 8443;
        sport = 4444;
        url = "/";
         *//*

        shost = "vm-w2003-ane7.ct.local";
        sport = 4437;
        url = "/site/Protected/Main.aspx";

        SSLContext ssl_cnt2 = SSLContext.getInstance("GostTLS");
        SSLSocketFactory factory2 = ssl_cnt2.getSocketFactory();
        System.setProperty("com.digt.trusted.jsse.certFile", "/certs/test1_cryptopro.cer");
        //System.setProperty("com.digt.trusted.jsse.keyPasswd", "");
        SSLSocket s2 =  (SSLSocket)factory2.createSocket(shost, sport);
        System.out.println("test authenticated Client - Socket2");
        //s2.startHandshake();
        ReadFromSocket(s2, url);
        AboutSocket("Socket 2: ", s2);
        s2.close();
*/

        System.out.println("test HTTPS");
        System.setProperty("com.digt.trusted.jsse.certFile", "/certs/test1_cryptopro.cer");
        //System.setProperty("com.digt.trusted.jsse.keyPasswd", "");
        String shost = "https://api.dom.gosuslugi.ru";
        int sport = 443;
        String url = "/ext-bus-home-management-service/services/HomeManagementAsync";
        ReadFromHTTPS(new URL("https://"+shost+":"+sport+url));

    }

    private static void AboutSocket(String prefix, SSLSocket s) {
        String[] cs = s.getEnabledCipherSuites();
        String[] ep = s.getEnabledProtocols();
        if (cs.length>0) System.out.println(prefix + " SSLSocket.getEnabledCipherSuites() = " + cs[0]);
        if (ep.length>0) System.out.println(prefix + " SSLSocket.getEnabledProtocols() = " + ep[0]);
        InetAddress ia = s.getInetAddress();
        System.out.println(prefix + " SSLSocket.getInetAddress().getHostAddress() = " + ia.getHostAddress());
        SSLSession ss = s.getSession();
        About_session(prefix, ss);
    }

    private static void About_session(String prefix, SSLSession ss) {
        System.out.println(prefix + " Session.getCipherSuite() = " + ss.getCipherSuite());
        System.out.println(prefix + " Session.getPeerHost() = " + ss.getPeerHost());
        System.out.println(prefix + " Session.getProtocol() = " + ss.getProtocol());
        System.out.println(prefix + " Session.getId().length = " + ss.getId().length);
        System.out.println(prefix + " Session.getId() = " + ss.getId());
        System.out.println(prefix + " Session.getLastAccessedTime() = " + new Date(ss.getLastAccessedTime()));
        X509Certificate[] lcerts = (X509Certificate[]) ss.getLocalCertificates();
        if (lcerts == null) {
            System.out.println(prefix + " None local certificate");
        } else {
            Principal lp = ss.getLocalPrincipal();
            System.out.println(prefix + " Session.getLocalPrincipal().toString() = " + lp.toString());
        }
        try {
            System.out.println(prefix + " Session.getPeerPrincipal().toString() = " + ss.getPeerPrincipal().toString());
        } catch (SSLPeerUnverifiedException ex) {
            ex.printStackTrace();
        }
    }

    private static void ReadFromHTTPS(URL url) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //connection.setDoInput(true);
            connection.setDoOutput(true);
            //connection.setUseCaches(false);
            //connection.setRequestMethod("GET");
            //connection.setRequestProperty("Connection", "Keep-Alive");
            //connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; AskTbFXTV5/5.9.1.14019)");
            // send data and get response
            //connection.connect();
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes("GET");
            output.flush();
            output.close();
            /* read response */
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void ReadFromSocket(SSLSocket s, String url) {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    s.getOutputStream())));

            out.println("GET "+url+" HTTP/1.0");
            out.println();
            out.flush();

            /*
             * Make sure there were no surprises
             */
            if (out.checkError()) {
                System.out.println(
                        "SSLSocketClient:  java.io.PrintWriter error");
            }

            /* read response */
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            s.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
