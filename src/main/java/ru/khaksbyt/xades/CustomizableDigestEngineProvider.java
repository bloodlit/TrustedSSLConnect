package ru.khaksbyt.xades;

import xades.util.GostXAdESUtility;
import xades4j.UnsupportedAlgorithmException;
import xades4j.providers.impl.DefaultMessageDigestProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CustomizableDigestEngineProvider extends DefaultMessageDigestProvider {
    @Override
    public MessageDigest getEngine(String digestAlgorithmURI) throws UnsupportedAlgorithmException {
        final String digestAlgOid = GostXAdESUtility.digestUri2Digest(digestAlgorithmURI);

        try {
            return MessageDigest.getInstance(digestAlgOid);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedAlgorithmException(e.getMessage(), digestAlgorithmURI, e);
        }
    }
}
