import java.nio.ByteBuffer;

public class PacketSnifferExample
{
	public static void main(String[] args) {
        SimplePacketDriver driver=new SimplePacketDriver();
	//Get adapter names and print info
        String[] adapters=driver.getAdapterNames();
        System.out.println("Number of adapters: "+adapters.length);
        for (int i=0; i< adapters.length; i++) System.out.println("Device name in Java ="+adapters[i]);
	//Open first found adapter (usually first Ethernet card found)
        if (driver.openAdapter(adapters[0])) System.out.println("Adapter is open: "+adapters[0]);
	//Read a packet (blocking operation)
        byte [] packet;
	while(true){
	   packet=driver.readPacket();
           //Wrap it into a ByteBuffer
           ByteBuffer Packet=ByteBuffer.wrap(packet);
           //Print packet summary
           System.out.println("Packet: "+Packet+" with capacity: "+Packet.capacity());
           System.out.println(driver.byteArrayToString(packet));
        }
 
}
}
