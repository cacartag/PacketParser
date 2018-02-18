import java.nio.ByteBuffer;
import java.util.Scanner;
import org.apache.commons.cli.*;
import java.util.Collection;
// javac -cp ".;commons-cli-1.4.jar" -d . OptionHandler.java
// java -cp ".;commons-cli-1.4.jar" OptionHandler

public class OptionHandler{

    private SimplePacketDriver driver;
    private String[] adapters;
    private EthernetParser eth;
    private IPPacketParser ip;
    private TCPParser tcp;
    private Options optionsSingleNoArg;
    private Options optionsDoubleArg;
    
    // set after options
    private int packetsToCapture;
    private String inputFile;
    private String outputFile;
    private String typeToParse;
    private int headerOnly;
    private String sourceAddress;
    private String destinationAddress;
    private int orset;
    private int andset;
    private int [] sourcePortRange;
    private int [] destinationPortRange;

    OptionHandler()
    {
        driver = new SimplePacketDriver();
        adapters = driver.getAdapterNames();
        eth = new EthernetParser();
        ip = new IPPacketParser();
        tcp = new TCPParser();
        optionsSingleNoArg = new Options();
        optionsDoubleArg = new Options();
        
        // initiating options to default, these will be checked when parsing
        packetsToCapture = -1;
        inputFile = null;
        outputFile = null;
        typeToParse = null;
        headerOnly = -1;
        sourceAddress = null;
        destinationAddress = null;
        orset = -1;
        andset = -1;
        sourcePortRange = new int [] {-1,-1};
        destinationPortRange = new int [] {-1,-1};
    }

    // parse tcp ip packets
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
    
    public void ParseOptions(String [] args) throws Exception
    {   
        // options for single or no argument
        optionsSingleNoArg.addOption("c",true,   "-c count                      Exit after receiving count packets");
        optionsSingleNoArg.addOption("r",true,   "-r filename                   Read Packets from file");
        optionsSingleNoArg.addOption("o",true,   "-o filename                   Save Output to filename ");
        optionsSingleNoArg.addOption("t",true,   "-t type                       Print only packets of the specified type where type is one of: eth, arp, ip, icmp, tcp, or udp");
        optionsSingleNoArg.addOption("h",false,   "-h                            Print header info only as specified by -t");
        optionsSingleNoArg.addOption("src",true, "-src saddress                 Print only packets with source address equal to saddress");
        optionsSingleNoArg.addOption("dst",true, "-dst daddress                 Print only packets with destination address equal to daddress");
        optionsSingleNoArg.addOption("help",false,"-help                         Get all options");
        
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
        
        Option [] optionDoubleArgLists = optionsDoubleArg.getOptions().toArray(new Option[0]);
        
        for(int current = 0; current < optionDoubleArgLists.length; current++)
        {
            optionsSingleNoArg.addOption(optionDoubleArgLists[current]);
        }        
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(optionsSingleNoArg, args);
    
        // no argument option check
        if(cmd.hasOption("h"))
        {

        }
        
        // prints out options with their descriptions
        if(cmd.hasOption("help"))
        {
            Option [] optionSingleNoArgLists = optionsSingleNoArg.getOptions().toArray(new Option[0]);
            
            for(int current = 0; current < optionSingleNoArgLists.length; current++)
            {
                System.out.println(optionSingleNoArgLists[current].getDescription());
            }
        }
        
        // single argument option checks
        if(cmd.hasOption("c"))
        {
            String argument = getArgument(cmd,"c",1)[0];
        }
        
        if(cmd.hasOption("r"))
        {
            String argument = getArgument(cmd,"r",1)[0];
        }
            
        if(cmd.hasOption("o"))
        {
            String argument = getArgument(cmd,"o",1)[0];
        }
        
        if(cmd.hasOption("t"))
        {
            String argument = getArgument(cmd,"t",1)[0];
        }

        // double argument option checks
        if(cmd.hasOption("src"))
        {
            String [] arguments = getArgument(cmd,"src",2);
        }
        
        if(cmd.hasOption("dst"))
        {
            String [] arguments = getArgument(cmd,"dst",2);
        }
        
        if(cmd.hasOption("sord"))
        {
            String [] arguments = getArgument(cmd,"sord",2);
        }
        
        if(cmd.hasOption("sandd"))
        {
            String [] arguments = getArgument(cmd,"sandd",2);
        }
        
        if(cmd.hasOption("sport"))
        {
            String [] arguments = getArgument(cmd,"sport",2);
        }
        
        if(cmd.hasOption("dport"))
        {
            String [] arguments = getArgument(cmd,"dport",2);
        }
    }
    
    // extract passed values of options that are set
    public String [] getArgument(CommandLine cmd, String option, int argumentNumber) throws Exception
    {
        
        // checking for single argument retrieval
        if(argumentNumber == 1)
        {
            try
            {
                String[] clArg = new String[]{ cmd.getOptionValue(option)};
                System.out.println("received: " + clArg[0]);
                return clArg;
            } catch (Exception e){
                System.out.println(option + " is missing an argument");
                return null;
            }
        }
        
        if(argumentNumber == 2)
        {
            try
            {
                String [] clArg = cmd.getOptionValues(option);
                System.out.println("received: ");
                for(String l : clArg) { System.out.println(l + "\n"); }
                return clArg;
            } catch (Exception e){
                System.out.println(option + " is missing an argument");
                return null;
            }            
        }
        
        return null;
        
    }
    
    // scans interfaces and prompts the user to choose one
    public void ChooseInterface()
    {
        Scanner sc = new Scanner(System.in);
        
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
    }
    
}