/*
 * Copyright Addicticks 2015.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addicticks.net.httpsupload;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * SSL/TLS utilities.
 * 
 * @author Addicticks 
 */
public class SSLUtils {

    private static final Logger LOGGER = Logger.getLogger(SSLUtils.class.getName());


    // Create all-trusting host name verifier.
    // This is used when we do not want to validate certificates, e.g. when
    // we want allow self-signed certificates.
    private static final HostnameVerifier ALLHOSTSVALID_HOSTNAMEVERIFIER = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private SSLUtils() {
    }
    
    // Create a trust manager chain that does not validate certificate chains.
    // This is used when we do not want to validate certificates, e.g. when
    // we want to allow self-signed certificates.
    private static TrustManager[] getNonValidatingTrustManagers(String[] acceptedIssuers) {
        X509TrustManager x509TrustManager = new CustomX509TrustManager(acceptedIssuers);
        return new TrustManager[]{x509TrustManager};
    }
    
    /**
     * Changes the HTTPS connection so that it will not validate the endpoint's
     * certificates. Also it will not require the URL hostname to match the
     * common name presented by the endpoint's certificate. This method should
     * be called <i>before</i> a connection is made on the {@code connection}
     * object.
     *
     * <p>
     * This method is equivalent to <code>--no-check-certificate</code> option when
     * using the Unix/Linux <code>wget</code> command line tool.
     *
     * <p>
     * As an additional feature the issuer of the certificate can be checked
     * to match (any of) a certain string. If - for example - you create
     * a self-signed certificate then you decide the 'issuer organization' 
     * yourself and the issuer organization name you used when you created the
     * certificate can then be validated here. This provides a 
     * little extra security than simply accepting any type of certificate.
     * 
     * 
     * @param connection connection to change (must not yet be connected)
     * @param acceptedIssuers accept only a certificate from one of these issuer
     * organizations. Checks against the Organization (O) field in the 'Issued
     * By' section of the server's certficate. This parameter provides some
     * minimal security. A {@code null} means all issuer organizations are
     * accepted.
     */
    public static void setNoValidate(HttpsURLConnection connection, String[] acceptedIssuers) {
        SSLContext sc;
        try {
            // Using "SSL" below means protocols: SSLv3, TLSv1
            sc = SSLContext.getInstance("SSL");
            sc.init(null, getNonValidatingTrustManagers(acceptedIssuers), new java.security.SecureRandom());
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(SSLUtils.ALLHOSTSVALID_HOSTNAMEVERIFIER);
        } catch (NoSuchAlgorithmException ex) {
            // Don't think this will ever happen. Hence we do not forward it.
            LOGGER.log(Level.SEVERE, "Algorithm SSL not found.", ex);
        } catch (KeyManagementException ex) {
            // Don't think this will ever happen. Hence we do not forward it.
            LOGGER.log(Level.SEVERE, "Error initializing SSL security context.", ex);
        }
    }

    /**
     * Changes the HTTPS connection so that it will not validate the endpoint's
     * certificates. Also it will not require the URL hostname to match the
     * common name presented by the endpoint's certificate. This method should
     * be called <i>before</i> a connection is made on the {@code connection}
     * object.
     *
     * <p>
     * This method is equivalent to <code>--no-check-certificate</code> option when
     * using the Unix/Linux <code>wget</code> command line tool.
     *
     * 
     * @param connection connection to change (must not yet be connected)
     */
    public static void setNoValidate(HttpsURLConnection connection) {
        setNoValidate(connection, null);
    }

    
    private static class CustomX509TrustManager implements X509TrustManager {

        private final String[] acceptedIssuers;

        public CustomX509TrustManager(String[] acceptedIssuers) {
            this.acceptedIssuers = acceptedIssuers;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // To accept all certs this method should return null
            // even if the Javadoc says otherwise.
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            // Parameter verification
            if (certs == null || certs.length == 0) {
                throw new IllegalArgumentException("certs parameter cannot be null or zero length");
            }
            if (authType == null || authType.length() == 0) {
                throw new IllegalArgumentException("authType parameter cannot be null or zero length");
            }
            // Check if the server certificate has an Issuer Organization name included
            // in acceptedIssuers list.
            boolean accepted = false;
            String serverCertIssuer = null;

            if (acceptedIssuers != null) {
                
                // Check against the certificate's Issued By section.
                // First we validate if the certificate has Issuer information at all.
                // (it really should have)
                if (certs[0].getIssuerX500Principal() == null || certs[0].getIssuerX500Principal().getName() == null) {
                    throw new CertificateException("Certificate at endpoint can't be trusted. Has no issuer information. Cannot validate against list of accepted issuers.");
                } else {
                    try {
                        List<Rdn> rdns = (new LdapName(certs[0].getIssuerX500Principal().getName())).getRdns();
                        boolean orgFound = false;
                        for (Rdn rdn : rdns) {
                            if (rdn.getType().equals("O")) {
                                orgFound = true;
                                if (rdn.getValue() == null) {
                                    throw new CertificateException("Certificate's Issuer Organization (O) field is empty. Cannot validate against list of accepted issuers.");
                                } else {
                                    serverCertIssuer = (String) rdn.getValue();
                                    if (Arrays.asList(acceptedIssuers).contains(serverCertIssuer)) {
                                        accepted = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!orgFound) {
                            // The Organization (O) field was not found.
                            throw new CertificateException("No Organization (0) field found in the issuer section of server's certificate. Cannot validate against list of accepted issuers.");
                        }
                        if (!accepted) {
                            throw new CertificateException("Certificate at endpoint can't be trusted. It is issued by \"" + serverCertIssuer + "\" which is not on the list of accepted issuers.");
                        }
                    } catch (InvalidNameException ex) {
                        // Caught from LdapName constructor. Doubt it will ever happen.
                        throw new CertificateException("Certificate at endpoint can't be trusted (LDAP name syntax violation). Cannot validate against list of accepted issuers.", ex);
                    }
                }
            }
        }
    }

}
