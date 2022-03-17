package model.payload;

import model.AuthChallenge;
import utils.ByteUtils;

public class AuthChallengePayload extends Payload {

    private final static int CHALLENGE_SIZE = 0x20; //(32)
    private final static int METHOD_SIZE = 0x02;
    private final static int METHOD_COUNT_SIZE = 0x02;

    public AuthChallengePayload(Payload payload) {
        super(payload);
    }

    public AuthChallenge readAuthChallenge() {
        byte[] challenge = new byte[CHALLENGE_SIZE];

        for(int i = 0; i < challenge.length; i++) {
            challenge[i] = payload[i];
        }

        int methodCount = ByteUtils.toUnsigned(ByteUtils.toShort(payload[CHALLENGE_SIZE], payload[CHALLENGE_SIZE + 1]));

        short[] methods = new short[methodCount];
        for(int i = 0; i < methods.length; i++) {
            int payloadIndex = CHALLENGE_SIZE + METHOD_COUNT_SIZE + i * METHOD_SIZE;
            methods[i] = ByteUtils.toShort(payload[payloadIndex], payload[payloadIndex + 1]);
        }

        return new AuthChallenge(challenge, methods);
    }

}
