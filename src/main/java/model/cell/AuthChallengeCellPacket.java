package model.cell;

import model.AuthChallenge;
import model.payload.AuthChallengePayload;

public class AuthChallengeCellPacket extends CellPacket{

    private AuthChallenge authChallenge;

    public AuthChallengeCellPacket(short CIRC_ID, byte COMMAND, byte[] PAYLOAD) {
        super(CIRC_ID, COMMAND, PAYLOAD);

        this.PAYLOAD = new AuthChallengePayload(this.PAYLOAD);
        initiateAuthChallengeRead();
    }

    private void initiateAuthChallengeRead() {
        authChallenge = getPayload().readAuthChallenge();
    }

    public AuthChallenge getAuthChallenge() {
        return authChallenge;
    }

    public AuthChallengePayload getPayload() {
        return (AuthChallengePayload) PAYLOAD;
    }

    @Override
    public String toString() {
        return "AuthChallengeCellPacket{" +
                "authChallenge=" + authChallenge +
                "} " + super.toString();
    }
}
