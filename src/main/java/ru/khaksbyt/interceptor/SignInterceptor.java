package ru.khaksbyt.interceptor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

import java.io.ByteArrayOutputStream;

@Slf4j
@Component
public class SignInterceptor implements ClientInterceptor {

    @SneakyThrows
    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        log.info("Server Request Message: " + messageContext.getRequest().toString());
        WebServiceMessage message = messageContext.getRequest();
        if (message instanceof SoapMessage) {
            SoapMessage soapMessage = (SoapMessage) message;

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            soapMessage.writeTo(stream);

            log.info("DUMP OF SOAP MESSAGE");
            log.info(stream.toString());

            SoapEnvelope envelope = soapMessage.getEnvelope();
            SoapHeader header = envelope.getHeader();

            log.info(header.toString());
        }
        //Signature если в щаблоне есть упоминания то необходимо добавить подпись

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        log.info("Server Response Message: " + messageContext.getResponse().toString());

        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        log.info("Message Context on soap Fault  " + messageContext);

        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception e) throws WebServiceClientException {

    }
}
