package model.cell;

import model.AuthChallenge;
import model.payload.AuthChallengePayload;

public class AuthChallengeCellPacket extends CellPacket{

    private AuthChallenge authChallenge;

    public AuthChallengeCellPacket(int CIRC_ID, byte[] PAYLOAD) {
        super(CIRC_ID, CellPacket.AUTH_CHALLENGE_COMMAND, PAYLOAD);

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
