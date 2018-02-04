import java.nio.ByteBuffer;
import java.util.Scanner;

public class Main{
    
    public static void main(String [] args) throws Exception
    {
        SimplePacketDriver driver = new SimplePacketDriver();
        String[] adapters = driver.getAdapterNames();
        Scanner sc = new Scanner(System.in);
        EthernetParser eth = new EthernetParser();
        IPPacketParser ip = new IPPacketParser();
        
        System.out.println("Adapter found are:");
        for (int index = 0; index < adapters.length; index++)
        {
            System.out.println("("+index+"): "+adapters[index]);
        }
        
        System.out.println("Which adapter do you choose?");
        
        int adapterIndex = sc.nextInt();
        
        if(driver.openAdapter(adapters[adapterIndex]))
        {
            System.out.println("adapter "+ adapters[adapterIndex] + " open");
        }
        
        byte [] packet = driver.readPacket();
        
        ByteBuffer Packet = ByteBuffer.wrap(packet);
        
        System.out.println("Packet: "+Packet+" with capacity: "+Packet.capacity());
        System.out.println(driver.byteArrayToString(packet));
        
        eth.parsePacket(packet);
        ip.parsePacket(packet);
        
        System.out.println("Destination: " + eth.getDestinationString());
        System.out.println("Source: " + eth.getSourceString());
        System.out.println("Type: " + eth.getTypeString());
        System.out.println("IP version: " + ip.getVersionString());
        System.out.println("IP length: " + ip.getIHLString());

    }
    
}