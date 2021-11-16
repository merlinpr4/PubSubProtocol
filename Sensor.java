// Author : Merlin Prasad
//Student number : 19333557
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 * Sensor class
 * An instance accepts user input
 */
public class Sensor extends Node {
	static final int DEFAULT_SRC_PORT = 50003;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "broker";
	int ACKNo = 0;

	InetSocketAddress dstAddress;
	Terminal terminal;
	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Sensor(Terminal terminal,String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal= terminal;
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Sensor will recieve Acknowledgments from the Broker that publishing was successfull
	 */

	public synchronized void onReceipt(DatagramPacket packet) {
		PacketContent content= PacketContent.fromDatagramPacket(packet);

		terminal.println("Sensors recieved the following packet:" + content.toString());
		ACKNo = ACKNo + 1 ;
		if(ACKNo == 2) //only after an acknowledgment for each sensor is recieved do we restart the sensors
		{
			ACKNo = 0 ;
			this.notify();
		}
	}

	/**
	 * Sensor publishes readings to the Broker from the room the user picked 
	 *
	 */
	public synchronized void start() throws Exception {
		terminal.println("Sensors have started"); 
		String userInput;
		boolean finished = false;
		DatagramPacket tempSensor ;
		DatagramPacket lightSensor ;

		while (!finished) {
			userInput = terminal.readString("Which rooms sensors would you like to publish?(or quit to exit)");

			//each room in the building has temprature and light sensor
			if(userInput.equalsIgnoreCase("bedroom") ||userInput.equalsIgnoreCase("bed")){
				tempSensor = new SendPacketContent("Pub","sens","bed","20","temp","03").toDatagramPacket();
				tempSensor.setSocketAddress(dstAddress);
				socket.send( tempSensor);

				lightSensor = new SendPacketContent("Pub","sens","bed","31","light","04").toDatagramPacket();
				lightSensor.setSocketAddress(dstAddress);
				socket.send(lightSensor);

				this.wait();
			}

			else if (userInput.equalsIgnoreCase("kitchen") || userInput.equalsIgnoreCase("kit")){
				tempSensor = new SendPacketContent("Pub","sens","kit","43","temp","05").toDatagramPacket();
				tempSensor.setSocketAddress(dstAddress);
				socket.send( tempSensor);

				lightSensor = new SendPacketContent("Pub","sens","kit","980","light","06").toDatagramPacket();
				lightSensor.setSocketAddress(dstAddress);
				socket.send(lightSensor);
				this.wait();
			}
			else if(userInput.equalsIgnoreCase("quit")){ 
				finished = true;
			}
			else {
				terminal.println("Error. Please select a room in the house i.e kitchen or bedroom");
			}
		}
	}

	/**
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal= new Terminal("Sensor");
			(new Sensor(terminal,DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
