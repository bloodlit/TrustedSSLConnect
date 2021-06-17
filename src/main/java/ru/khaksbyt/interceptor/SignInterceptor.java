package ru.khaksbyt.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

@Slf4j
@Component
public class SignInterceptor implements ClientInterceptor {
    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        log.info("Server Request Message: " + messageContext.getRequest().toString());

        return false;
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
