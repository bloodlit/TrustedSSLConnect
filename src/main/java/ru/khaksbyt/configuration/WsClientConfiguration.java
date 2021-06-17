package ru.khaksbyt.configuration;

import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import ru.khaksbyt.interceptor.SOAPActionInterceptor;
import ru.khaksbyt.interceptor.SignInterceptor;

@Configuration
public class WsClientConfiguration extends WebServiceGatewaySupport {

    public WsClientConfiguration(HttpComponentsMessageSender sender) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMessageSender(sender);
        template.setInterceptors(new ClientInterceptor[]{
                new SignInterceptor(),
                new SOAPActionInterceptor()
        });

        setWebServiceTemplate(template);
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setPackagesToScan(
                packageName(ru.gosuslugi.dom.schema.integration.house_management.ObjectFactory.class),
                packageName(ru.gosuslugi.dom.schema.integration.base.ObjectFactory.class)
        );

        return marshaller;
    }

    private String packageName(final Class<?> jaxbClass) {
        return jaxbClass.getPackage().getName();
    }

    @Bean
    public HttpComponentsMessageSender defaultMyMessageSender(HttpClient httpClient) {
        return new HttpComponentsMessageSender(httpClient);
    }


}
