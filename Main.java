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
        TCPParser tcp = new TCPParser();
        
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
        tcp.parsePacket(packet);
        
        System.out.println("Ethernet Header:");
        System.out.println("Destination: " + eth.getDestinationString());
        System.out.println("Source: " + eth.getSourceString());
        System.out.println("Type: " + eth.getTypeString());
        
        System.out.println("IP Header:");
        System.out.println("IP version: " + ip.getVersionString());
        System.out.println("IP length: " + ip.getIHLString());
        System.out.println("IP DSCP: " + ip.getDSCPString());
        System.out.println("IP ECN: " + ip.getECNString());
        System.out.println("IP packet length: "+ ip.getLengthString());
        System.out.println("Identification: " + ip.getIdentification());
        System.out.println("Flags: " + ip.getFlagString());
        System.out.println("Fragment Offset: " + ip.getFragmentOffsetString());
        System.out.println("TTL: " + ip.getTTLString());
        System.out.println("Protocol: " + ip.getProtocolString());
        System.out.println("Header Checksum: " + ip.getHeaderChecksumString());
        System.out.println("Source IP Address: " + ip.getSourceAddressString());
        System.out.println("Destination IP Address: " + ip.getDestinationAddressString());
        
        System.out.println("TCP Header:");
        System.out.println("Port source: " + tcp.getSourcePortString());
        System.out.println("Port destination: " + tcp.getDestinationPortString());
        
    }
    
}