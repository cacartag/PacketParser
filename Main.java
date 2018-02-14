import java.nio.ByteBuffer;
import java.util.Scanner;
import org.apache.commons.cli.*;
// javac -cp ".;commons-cli-1.4.jar" -d . Main.java
// java -cp ".;commons-cli-1.4.jar" Main

public class Main
{
    
    public void captureIPTCP(EthernetParser eth, IPPacketParser ip, TCPParser tcp, SimplePacketDriver driver) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("What IP protocol type to capture");
        
        int protocol = sc.nextInt();
        
        int protocolCaptured = 0;        
        
        while(protocolCaptured != protocol)
        {

            eth.clear();
            ip.clear();
            tcp.clear();
        
            byte [] packet = driver.readPacket();
        
            ByteBuffer Packet = ByteBuffer.wrap(packet);            
            
            eth.parsePacket(packet);

            if(eth.getTypeString().equals("0800"))
            {
                ip.parsePacket(packet);
                
                if(Integer.parseInt(ip.getProtocolString()) == 6)
                {
                    tcp.parsePacket(packet);
                    protocolCaptured = Integer.parseInt(ip.getProtocolString());   
                }                    
            }


            

            
            if(protocolCaptured == protocol)
            {
                System.out.println("Packet: "+Packet+" with capacity: "+Packet.capacity());
                System.out.println(driver.byteArrayToString(packet));              
            }
        }
  


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
        System.out.println("Sequence Number: " + tcp.getSequenceNumberString());
        System.out.println("Acknowledgement Number: " + tcp.getAcknowledgementNumberString());
        System.out.println("Offset: " + tcp.getOffsetString());
        System.out.println("Reserved: " + tcp.getReservedString());
        System.out.println("TCP Flags: \n" + tcp.getTCPFlagsString());
        System.out.println("Window: " + tcp.getWindowString());
        System.out.println("Checksum: " + tcp.getCheckSumString());
        System.out.println("Urgent Pointer: " + tcp.getUrgentPointerString());
    }
    
    public static void main(String [] args) throws Exception
    {
        SimplePacketDriver driver = new SimplePacketDriver();
        String[] adapters = driver.getAdapterNames();
        Scanner sc = new Scanner(System.in);
        EthernetParser eth = new EthernetParser();
        IPPacketParser ip = new IPPacketParser();
        TCPParser tcp = new TCPParser();
        Options options = new Options();
        
        options.setArgs(2);
        options.addOption("c",true, "Exit after receiving count packets");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        
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

        if(cmd.hasOption("c"))
        {
            System.out.println("Detected c argument");
        } else {
            System.out.println("No argument detected");
        }
        
        //System.out.println("Got: " + args[0]);

        
    }
    
}