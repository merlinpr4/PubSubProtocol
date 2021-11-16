import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TLVPacketContent extends PacketContent {

    String t ;
    String l ;
    String v ;

	String info;
   
    /**
	 * Constructor that takes in information about a file.
     * @param t
     * @param l
     * @param v
	 * @param info info contained inside the acknowledgement
	 */

	TLVPacketContent(String t, String l, String v , String info) {
		type= TLV;
		this.t = t;
        this.l = l ;
        this.v = v ;
        this.info = info;
	}

    /**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains information about a header.
	 */
    protected TLVPacketContent(ObjectInputStream oin) {
		try {
			type= TLV;
            t = oin.readUTF();
            l = oin.readUTF();
            v = oin.readUTF();
			info= oin.readUTF();
		}
		catch(Exception e) {e.printStackTrace();}
	}

    protected void toObjectOutputStream(ObjectOutputStream oout) {
        try {
            oout.writeUTF(t);
            oout.writeUTF(l);
            oout.writeUTF(v);
			oout.writeUTF(info);
		}
		catch(Exception e) {e.printStackTrace();}
    }

  	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
    //come back later to decide if this is best encoding to do
	public String toString() {
	//	return "t:" + t + " l:" + l + " v:" + v + "payload: " + info;
        return "" + t + l + v + info ;
	}

    /**
	 * Returns the info contained in the packet.
	 *
	 * @return Returns the info contained in the packet.
	 */
	public String getPacketInfo() {
		return info;
	}

    	/**
	 * Returns the topic contained in the packet.
	 *
	 * @return Returns the topic contained in the packet.
	 */
	@Override public String getT() {
		return t;
	}

    	/**
	 * Returns the topic contained in the packet.
	 *
	 * @return Returns the topic contained in the packet.
	 */
	@Override public String getL() {
		return l;
	}

    /**
	 * Returns the topic contained in the packet.
	 *
	 * @return Returns the topic contained in the packet.
	 */
	@Override public String getV() {
		return v;
	}

}
