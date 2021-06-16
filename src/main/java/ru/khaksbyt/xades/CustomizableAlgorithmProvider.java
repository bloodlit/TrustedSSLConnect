package ru.khaksbyt.xades;

import xades.util.GostXAdESUtility;
import xades4j.UnsupportedAlgorithmException;
import xades4j.algorithms.Algorithm;
import xades4j.algorithms.ExclusiveCanonicalXMLWithoutComments;
import xades4j.algorithms.GenericAlgorithm;
import xades4j.providers.impl.DefaultAlgorithmsProviderEx;

/**
 * Провайдер, описывающий используемые алгоритмы.
 */
public class CustomizableAlgorithmProvider extends DefaultAlgorithmsProviderEx {
    private String digestUrn = null;

    @Override
    public Algorithm getSignatureAlgorithm(String keyAlgorithmName) throws UnsupportedAlgorithmException {

        digestUrn = GostXAdESUtility.key2DigestUrn(keyAlgorithmName);
        final String signatureUrn = GostXAdESUtility.key2SignatureUrn(keyAlgorithmName);

        return new GenericAlgorithm(signatureUrn);
    }

    @Override
    public String getDigestAlgorithmForReferenceProperties() {
        return digestUrn;
    }

    public String getDigestAlgorithmForDataObjsReferences() {
        return digestUrn;
    }

    public String getDigestAlgorithmForTimeStampProperties() {
        return digestUrn;
    }

    @Override
    public Algorithm getCanonicalizationAlgorithmForSignature() {
        return new ExclusiveCanonicalXMLWithoutComments();
    }

    @Override
    public Algorithm getCanonicalizationAlgorithmForTimeStampProperties() {
        return new ExclusiveCanonicalXMLWithoutComments();
    }
}
