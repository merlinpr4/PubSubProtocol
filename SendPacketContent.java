// Author : Merlin Prasad
//Student number : 19333557
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SendPacketContent extends PacketContent {

	String format;
	String device ;
	String room ;
    String info;
	String topic;
	String ID ;


	/**
	 * Constructor that takes in information about a file.
	 * @param format Format of packet (Sub/Pub)
	 * @param device the type of device it is 
	 * @param room the room the device is pub/sub from
	 * @param info Message.
	 * @param topic topic name i.e light , temp
	 * @param ID ID of device
	 */

    SendPacketContent(String format ,String device, String room ,String info, String topic, String ID){
        type = SENDPACKET ;
		this.format = format;
		this.device = device ;
		this.room = room ;
        this.info = info ;
		this.topic = topic ;
		this.ID = ID ;
    }

    	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains information about a file.
	 */
	protected SendPacketContent(ObjectInputStream oin) {
		try {
			type= SENDPACKET;
			format = oin.readUTF();
			device = oin.readUTF();
			room = oin.readUTF();
			info= oin.readUTF();
			topic = oin.readUTF();
			ID =  oin.readUTF();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeUTF(format);
			oout.writeUTF(device);
			oout.writeUTF(room);
			oout.writeUTF(info);
			oout.writeUTF(topic);
			oout.writeUTF(ID);
		}
		catch(Exception e) {e.printStackTrace();}
	}

    /**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return "Format: " + format + " Device:" + device + " Room:"+ room + " Info:" + info + " Topic:" + topic + " ID:" + ID;
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
	@Override public String getTopicInfo() {
		return topic;
	}

	/**
	 * Returns the format contained in the packet.
	 *
	 * @return Returns the format contained in the packet.
	 */
	@Override public String getFormatInfo() {
		return format;
	}

	/**
	 * Returns the device contained in the packet.
	 *
	 * @return Returns the device contained in the packet.
	 */
	@Override public String getDeviceInfo() {
		return device;
	}

	/**
	 * Returns the room contained in the packet.
	 *
	 * @return Returns the room contained in the packet.
	 */
	@Override public String getRoomInfo() {
		return room;
	}

	/**
	 * Returns the ID contained in the packet.
	 *
	 * @return Returns the ID contained in the packet.
	 */
	@Override public String getIDInfo() {
		return ID;
	}
}
