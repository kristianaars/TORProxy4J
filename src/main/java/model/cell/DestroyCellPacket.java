package model.cell;

import model.payload.DestroyPayload;

public class DestroyCellPacket extends CellPacket {

    /**
     *    The destroy reasons are:
     *
     *      0 -- NONE            (No reason given.)
     *      1 -- PROTOCOL        (Tor protocol violation.)
     *      2 -- INTERNAL        (Internal error.)
     *      3 -- REQUESTED       (A client sent a TRUNCATE command.)
     *      4 -- HIBERNATING     (Not currently operating; trying to save bandwidth.)
     *      5 -- RESOURCELIMIT   (Out of memory, sockets, or circuit IDs.)
     *      6 -- CONNECTFAILED   (Unable to reach relay.)
     *      7 -- OR_IDENTITY     (Connected to relay, but its OR identity was not
     *                            as expected.)
     *      8 -- CHANNEL_CLOSED  (The OR connection that was carrying this circuit
     *                            died.)
     *      9 -- FINISHED        (The circuit has expired for being dirty or old.)
     *     10 -- TIMEOUT         (Circuit construction took too long)
     *     11 -- DESTROYED       (The circuit was destroyed w/o client TRUNCATE)
     *     12 -- NOSUCHSERVICE   (Request for unknown hidden service)
     *
     */

    private final byte DESTROY_REASON;

    public DestroyCellPacket(int CIRC_ID, byte[] payload) {
        super(CIRC_ID, CellPacket.DESTROY_COMMAND, payload);

        this.PAYLOAD = new DestroyPayload(this.PAYLOAD);

        this.DESTROY_REASON = ((DestroyPayload)this.PAYLOAD).retrieveDestroyReason();
    }

    @Override
    public DestroyPayload getPayload() {
        return (DestroyPayload) super.getPayload();
    }

    public byte getDESTROY_REASON() {
        return DESTROY_REASON;
    }

    @Override
    public String toString() {
        return "DestroyCellPacket{" +
                "DESTROY_REASON=" + DESTROY_REASON +
                "} " + super.toString();
    }
}

