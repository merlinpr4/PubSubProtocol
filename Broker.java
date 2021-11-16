// Author : Merlin Prasad
//Student number : 19333557
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import tcdIO.Terminal;

//Broker class handles the management of sending packets to relevant publishers and subscribers.
public class Broker extends Node {
	static final int DEFAULT_SRC_PORT = 50001;
	static final int DST_PORT_1 = 50002;
	static final String DST_NODE_1 = "dashboard";

	static final int DST_PORT_2 = 50004;
	static final String DST_NODE_2 = "actuator";

	Terminal terminal;

	String topicA = "null";
	String actuatorTopic = "null";
	String actuatorID = "null";
	String actuatorRoom = "null";

	String prevActuatorTopic = "null";
	String prevActuatorID = "null";

	InetSocketAddress dstAddress;

	Broker(Terminal terminal,int port) {
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public synchronized void start() throws Exception {
		terminal.println("Broker is ready and waiting for contact ");
		this.wait();
	}


	/**
	 * The Broker will be handling multiple incoming packets
	 * Check if the incoming packets are Subscriber (Sub) or Publisher(Pub) packtes.
	 * Send Ackknowledgements to tell the device it was sucessfull 
	 * Forward on Pub packets to releveant subscriber devices.
	 */
	public void onReceipt(DatagramPacket packet) {
		String topicB = "null";
		String ID = "null";
		String subDevice = "null";
		String room = "null";
		String pubDevice = "null";

		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);

			//acknowledgement to publishers or subscribers 
			DatagramPacket response;
			response= new AckPacketContent("Broker ACK").toDatagramPacket();
			response.setSocketAddress(packet.getSocketAddress());
			socket.send(response);
			terminal.println("Broker send acknowledgment ");

			//Finding out what topic the Dashboard is subscribed to
			if(content.getFormatInfo().equalsIgnoreCase("Sub")) {
				subDevice = content.getDeviceInfo() ;
				//Dashboard subscribe 
				if(subDevice.equalsIgnoreCase("dash")) {
					topicA = content.getTopicInfo();
					terminal.println("Dashboard subscribed to topic: " + topicA);		
				}
				else{ //Actuator subscribe 
					prevActuatorTopic = actuatorTopic ;
					actuatorTopic = content.getTopicInfo();

					prevActuatorID = actuatorID;
					actuatorID = content.getIDInfo();

					terminal.println("Broker is subscribed to by actuator:");
					terminal.println(content.toString());
				}
			}

			//Find out what the packet Broker was published
			if(content.getFormatInfo().equalsIgnoreCase("Pub")) {
				terminal.println("Broker recieved packet: " );
				terminal.println(content.toString());

				pubDevice = content.getDeviceInfo() ;
				if(pubDevice .equalsIgnoreCase("sens")) { // Sensor published messages	
					topicB = content.getTopicInfo();
					ID = content.getIDInfo();
					room = content.getRoomInfo();

					if(topicA.equalsIgnoreCase(topicB) || topicA.equalsIgnoreCase(ID) || topicA.equalsIgnoreCase(room)){
						//sending message onto Dashboard
						dstAddress= new InetSocketAddress(DST_NODE_1, DST_PORT_1);
						packet.setSocketAddress(dstAddress);
						socket.send(packet);
						terminal.println("Packet sent onto dashboard");
					}
				}
				else{ //dashboard publish
					topicA = "null";  //reintalise dashboard subscribe topic
					topicB = content.getTopicInfo();

					if(topicB.equalsIgnoreCase(actuatorTopic) ||topicB.equalsIgnoreCase(prevActuatorTopic) || topicB.equalsIgnoreCase(actuatorID) ||topicB.equalsIgnoreCase(prevActuatorID) || topicB.equalsIgnoreCase(actuatorRoom) ) {
						//sending message onto Actuator
						dstAddress= new InetSocketAddress(DST_NODE_2, DST_PORT_2);
						packet.setSocketAddress(dstAddress);
						socket.send(packet);
						terminal.println("Packet sent onto actuator");

					}
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public static void main(String[] args) {
		try {
			Terminal terminal= new Terminal("Broker");
			(new Broker(terminal,DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}

