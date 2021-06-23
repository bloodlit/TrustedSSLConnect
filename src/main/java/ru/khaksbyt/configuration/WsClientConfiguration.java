package ru.khaksbyt.configuration;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import ru.khaksbyt.interceptor.SignInterceptor;
import ru.khaksbyt.repository.memory.HistoryRepository;
import xades4j.production.XadesSigner;

@Configuration
public class WsClientConfiguration {

    private final WebServiceTemplate template;

    @Autowired
    public WsClientConfiguration(
            HttpClient httpClient,
            XadesSigner signer,
            HistoryRepository repository)
    {
        template = new WebServiceTemplate();
        template.setMessageSender(defaultMyMessageSender(httpClient));
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        template.setInterceptors(new ClientInterceptor[]{
                new SignInterceptor(signer, repository)
        });
    }

    private Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setPackagesToScan(
                packageName(ru.gosuslugi.dom.schema.integration.house_management.ObjectFactory.class),
                packageName(ru.gosuslugi.dom.schema.integration.nsi_common.ObjectFactory.class),
                packageName(ru.gosuslugi.dom.schema.integration.base.ObjectFactory.class)
        );

        return marshaller;
    }

    private String packageName(final Class<?> jaxbClass) {
        return jaxbClass.getPackage().getName();
    }

    private HttpComponentsMessageSender defaultMyMessageSender(HttpClient httpClient) {
        return new HttpComponentsMessageSender(httpClient);
    }

    @Bean
    public WebServiceTemplate defaultWebServiceTemplate() {
        return template;
    }

}
