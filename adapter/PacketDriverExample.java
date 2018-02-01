import java.nio.ByteBuffer;

public class PacketDriverExample
{
	public static void main(String[] args) {
        SimplePacketDriver driver=new SimplePacketDriver();
	//Get adapter names and print info
        String[] adapters=driver.getAdapterNames();
        System.out.println("Number of adapters: "+adapters.length);
        for (int i=0; i< adapters.length; i++) System.out.println("Device name in Java ="+adapters[i]);
	//Open first found adapter (usually first Ethernet card found)
        if (driver.openAdapter(adapters[1])) System.out.println("Adapter is open: "+adapters[1]);
	//Read a packet (blocking operation)
        System.out.println("Attempting to retrieve package");
        byte [] packet=driver.readPacket();
        //Wrap it into a ByteBuffer
        ByteBuffer Packet=ByteBuffer.wrap(packet);
        //Print packet summary
        System.out.println("Packet: "+Packet+" with capacity: "+Packet.capacity());
        System.out.println(driver.byteArrayToString(packet));
        //Send the same packet now (change headers)
        for (int i=0; i< 6; i++) packet[i]=1; //Destination
        for (int i=0; i< 6; i++) packet[i+6]=2; //Source
        packet[12]=9; packet[13]=10; //Make up a type
	//Send packet
        if (!driver.sendPacket(packet)) System.out.println("Error sending packet!");
        else System.out.println("packet sent");


        }
 
}
