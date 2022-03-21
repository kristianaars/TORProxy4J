package model;

import utils.CertificateUtils;

public class TorRelays {

    public static final TorRelay[] RELAYS = {

            new TorRelay("128.31.0.34", 9101, CertificateUtils.readPublicKey(
                                                        "-----BEGIN RSA PUBLIC KEY-----\n" +
                                                            "MIGJAoGBAMLpmWDsZNKk2acRSJoQVEStRjsC3LHqxgJjvFEnbkzmJ5FPxlx/SNi8\n" +
                                                            "C6mMLy4DSJwMF6NFpHZ0zE11XFIBxKA3LRnXi4MX1DGMS0EP6Qn9RbOl4hcg1BJ7\n" +
                                                            "kryzQzVKmbP9SCO/Fe3ruHatmYIUeuY7SDtcoL7983DQRKJiITDzAgMBAAE=\n" +
                                                            "-----END RSA PUBLIC KEY-----"))


    };
}
