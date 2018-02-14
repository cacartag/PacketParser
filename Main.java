import java.nio.ByteBuffer;
import java.util.Scanner;
import org.apache.commons.cli.*;
import java.util.Collection;
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
        Options optionsSingleNoArg = new Options();
        Options optionsDoubleArg = new Options();
        
        //options.setArgs(2);
        // options for single or no argument
        optionsSingleNoArg.addOption("c",true,   "-c count                      Exit after receiving count packets");
        optionsSingleNoArg.addOption("r",true,   "-r filename                   Read Packets from file");
        optionsSingleNoArg.addOption("o",true,   "-o filename                   Save Output to filename ");
        optionsSingleNoArg.addOption("t",true,   "-t type                       Print only packets of the specified type where type is one of: eth, arp, ip, icmp, tcp, or udp");
        optionsSingleNoArg.addOption("h",true,   "-h                            Print header info only as specified by -t");
        optionsSingleNoArg.addOption("src",true, "-src saddress                 Print only packets with source address equal to saddress");
        optionsSingleNoArg.addOption("dst",true, "-dst daddress                 Print only packets with destination address equal to daddress");

        // options for double arguments
        
        Option temp = new Option("sord", "-sord saddress daddress        Print only packets where the source address matches saddress or the destination address matches daddress");
        temp.setArgs(2);
        optionsDoubleArg.addOption(temp);
        
        temp = new Option("sandd", "-sandd saddress daddress       Print only packets where the source address matches saddress and the destination address matches daddress");
        temp.setArgs(2);
        optionsDoubleArg.addOption(temp);

        temp = new Option("sport", "-sport                         Print only packets where the source port is in the range ");
        temp.setArgs(2);
        optionsDoubleArg.addOption(temp);
        
        temp = new Option("dport", "-dport                         Print only packets where the destination port is in the range port1-port2");
        temp.setArgs(2);
        optionsDoubleArg.addOption(temp);
        
        Option [] optionSingleNoArgLists = optionsSingleNoArg.getOptions().toArray(new Option[0]);
        
        for(int current = 0; current < optionSingleNoArgLists.length; current++)
        {
            System.out.println(optionSingleNoArgLists[current].getDescription());
        }
        
        Option [] optionDoubleArgLists = optionsDoubleArg.getOptions().toArray(new Option[0]);
        
        for(int current = 0; current < optionDoubleArgLists.length; current++)
        {
            System.out.println(optionDoubleArgLists[current].getDescription());
        }
        
        CommandLineParser parser = new DefaultParser();
        //CommandLine cmdSingleNoArg = parser.parse(optionsSingleNoArg, args);
        CommandLine cmdDoubleArg = parser.parse(optionsDoubleArg, args);
        
        
        // System.out.println("Adapter found are:");
        // for (int index = 0; index < adapters.length; index++)
        // {
        //     System.out.println("("+index+"): "+adapters[index]);
        // }
        // 
        // System.out.println("Which adapter do you choose?");
        // 
        // int adapterIndex = sc.nextInt();
        // 
        // if(driver.openAdapter(adapters[adapterIndex]))
        // {
        //     System.out.println("adapter "+ adapters[adapterIndex] + " open");
        // }

        //if(cmdSingleNoArg.hasOption("c"))
        //{
        //    String clArg = cmdSingleNoArg.getOptionValue("c");
        //    System.out.println("Received: " + clArg);
        //} else {
        //    System.out.println("No Single argument detected");
        //}
        
        if(cmdDoubleArg.hasOption("sord"))
        {
            String clArg = cmdDoubleArg.getOptionValue("sord");
            System.out.println("Received: " + clArg);
        } else {
            System.out.println("No Double arguments detected");
        }
        
        
    }
    
}