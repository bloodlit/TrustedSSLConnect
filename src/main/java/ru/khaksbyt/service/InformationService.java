package ru.khaksbyt.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.cert.X509Certificate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static javax.security.auth.x500.X500Principal.RFC1779;

@Slf4j
@Service
public class InformationService {

    private final X509Certificate certificate;

    public InformationService(X509Certificate certificate) {
        this.certificate = certificate;
    }

    @SneakyThrows
    public String certificateName() {
        return Stream.of(certificate)
                .map(cert -> cert.getSubjectX500Principal().getName(RFC1779))
                .flatMap(name -> {
                    try {
                        return new LdapName(name).getRdns().stream()
                                .filter(rdn -> rdn.getType().equalsIgnoreCase("OID.2.5.4.4"))
                                .map(rdn -> rdn.getValue().toString());
                    } catch (InvalidNameException e) {
                        log.warn("Failed to get certificate CN.", e);
                        return Stream.empty();
                    }
                })
                .collect(joining(", "));

    }
}
