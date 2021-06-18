package ru.khaksbyt.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

@Slf4j
@Configuration
public class ClientConfig {
    // Keep alive
    private static final long DEFAULT_KEEP_ALIVE_TIME = 20 * 1000; // 20 sec

    @Value("#{new Integer('${application.client.timeout.connection:30}') * 1000}")
    private Integer connectionTimeout;

    @Value("#{new Integer('${application.client.timeout.request:30}') * 1000}")
    private Integer requestTimeout;

    @Value("#{new Integer('${application.client.timeout.socket:60}') * 1000}")
    private Integer socketTimeout;

    @Value("${application.client.pooling.connection.total:40}")
    private Integer totalConnections;

    @Value("${application.client.pooling.connection.perRoute:40}")
    private Integer routeConnections;

    @Value("application.client.defaultUri")
    private String defaultUri;

    private final SSLContext sslContext;

    @Autowired
    public ClientConfig(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * Инициализация параметров соединения и создание http-клиента.
     */
    @PostConstruct
    private void init() {

    }

    /**
     * Пул соединений обеспечивает повторное использование уже открытых соединений
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(this.totalConnections);
        poolingConnectionManager.setDefaultMaxPerRoute(this.routeConnections);

        HttpHost httpHost = new HttpHost(this.defaultUri, 443);
        poolingConnectionManager.setMaxPerRoute(new HttpRoute(httpHost), this.routeConnections);
        return poolingConnectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (httpResponse, httpContext) -> {
            HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
            HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);

            while (elementIterator.hasNext()) {
                HeaderElement element = elementIterator.nextElement();
                String param = element.getName();
                String value = element.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000; // convert to ms
                }
            }

            return DEFAULT_KEEP_ALIVE_TIME;
        };
    }

    @Bean
    public SSLConnectionSocketFactory sslConnectionSocketFactory() {
        return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(requestTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(sslConnectionSocketFactory())
                .setConnectionManager(poolingHttpClientConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }
}
