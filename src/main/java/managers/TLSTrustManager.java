package managers;

import javax.net.ssl.X509TrustManager;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TLSTrustManager implements X509TrustManager {

    private X509Certificate TLSHandshakeServerCertificate = null;

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
        System.out.println("TLS-Certificate to relay: " + chain[chain.length - 1]);
        TLSHandshakeServerCertificate = chain[chain.length - 1];
    }

    public X509Certificate getTLSHandshakeServerCertificate() {
        return TLSHandshakeServerCertificate;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
