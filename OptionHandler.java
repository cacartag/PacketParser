import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.Arrays;
import org.apache.commons.cli.*;
import java.util.Collection;
import java.util.Vector;
import java.lang.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.InputStreamReader;
import javax.xml.bind.DatatypeConverter;

// To compile in Windows: 	javac -cp ".;commons-cli-1.4.jar" -d . OptionHandler.java
// To run in Windows: 		java -cp ".;commons-cli-1.4.jar" OptionHandler
// To compile in Linux:		javac -cp ".:commons-cli-1.4.jar" -d . OptionHandler.java
// note I had to move libsimplepacketdriver_x64.so to /usr/lib/x86_64-Linux-GNU

public class OptionHandler{

    private SimplePacketDriver driver;
    private String[] adapters;
    private EthernetParser eth;
    private IPPacketParser ip;
    private TCPParser tcp;
    private ARPParser arp;
    private UDPParser udp;
    private Options optionsSingleNoArg;
    private Options optionsDoubleArg;
    private BufferedReader readStream;
    
    // possible options that can be set
    private boolean doneReading;
    private int packetsToCapture;
    private String inputFile;
    private String outputFile;
    private String typeToParse;
    private boolean headerOnly;
    private String sourceAddress;
    private String destinationAddress;
    private boolean orset;
    private boolean andset;
    private int [] sourcePortRange;
    private int [] destinationPortRange;
    private boolean sourcePortSet;
    private boolean destinationPortSet;
    private String [] possibleTypes = new String[]{"eth","arp","ip","icmp","tcp","udp"};

    OptionHandler()
    {
        //try{
        //    String absolutePath = System.getProperty("user.dir");
        //    System.load(absolutePath+"/libsimplepacketdriver.so");
        //}catch (Exception e)
        //{
        //    
        //}

        driver = new SimplePacketDriver();
        adapters = driver.getAdapterNames();
        eth = new EthernetParser();
        ip = new IPPacketParser();
        tcp = new TCPParser();
        arp = new ARPParser();
        udp = new UDPParser();
        optionsSingleNoArg = new Options();
        optionsDoubleArg = new Options();
        
        // initiating options to default, these will be checked when parsing
        doneReading = false;
        packetsToCapture = -1;
        inputFile = null;
        outputFile = null;
        typeToParse = "tcp";
        headerOnly = false;
        sourceAddress = null;
        destinationAddress = null;
        
        orset = false;
        andset = false;
        sourcePortRange = new int [] {-1,-1};
        destinationPortRange = new int [] {-1,-1};
        sourcePortSet = false;
        destinationPortSet = false;
    }
    
    public int parseOptions(String [] args) throws Exception
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
        CommandLine cmd;
        
        try{
            cmd = parser.parse(optionsSingleNoArg, args);
        } catch (Exception e)
        {
            System.out.println("that option is not available");
            return 0;
        }
        
        // no argument option check
        if(cmd.hasOption("h"))
        {
            headerOnly = true;
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
            
            try{
                int argumentPlaceholder = Integer.parseInt(argument);
                if(argumentPlaceholder > 0)
                {
                    packetsToCapture = Integer.parseInt(argument);                    
                }else{
                    throw new IllegalArgumentException();
                }
                
            } catch (Exception e){
                System.out.println("Error: Not a valid number for count option, defaulting to continuous");
            }
        }
        
        if(cmd.hasOption("r"))
        {
            inputFile = getArgument(cmd,"r",1)[0];
        }
            
        if(cmd.hasOption("o"))
        {
            outputFile = getArgument(cmd,"o",1)[0];
        }
        
        if(cmd.hasOption("t"))
        {
            typeToParse = getArgument(cmd,"t",1)[0];
            if(!(Arrays.asList(possibleTypes).contains(typeToParse.toLowerCase())))
            {
                System.out.println("That type is not available, type -help to see all types, defaulting to tcp");
                typeToParse = "tcp";
            }
        }

        // double argument option checks
        if(cmd.hasOption("src"))
        {
            sourceAddress = getArgument(cmd,"src",1)[0];
        }
        
        if(cmd.hasOption("dst"))
        {
            destinationAddress = getArgument(cmd,"dst",1)[0];
        }
        
        if(cmd.hasOption("sord"))
        {
            orset = true;
            String [] arguments = getArgument(cmd,"sord",2);
            sourceAddress = arguments[0];
            destinationAddress = arguments[1];
            
            //System.out.println("sord has been chosen source: " + sourceAddress + "\ndestination: " + destinationAddress);
        }
        
        if(cmd.hasOption("sandd"))
        {
            andset = true;
            String [] arguments = getArgument(cmd,"sandd",2);
            sourceAddress = arguments[0];
            destinationAddress = arguments[1];
            
            //System.out.println("sand has been chose source: " + sourceAddress + " \ndestination: " + destinationAddress);
        }
        
        if(cmd.hasOption("sport"))
        {
            String [] arguments = getArgument(cmd,"sport",2);
            
            try{
                int startRange = Integer.parseInt(arguments[0]);
                int endRange = Integer.parseInt(arguments[1]);
                
                if((startRange < endRange) && (startRange > 0) && (endRange > 0))
                {
                    sourcePortSet = true;
                    sourcePortRange[0] = startRange;
                    sourcePortRange[1] = endRange;
                } else {
                    System.out.println("not a valid range, defaulting to all ports");
                }
                
            } catch (Exception e)
            {
                System.out.println("could not parse port arguments");
            }
        }
        
        if(cmd.hasOption("dport"))
        {
            String [] arguments = getArgument(cmd,"dport",2);
            
            try{
                int startRange = Integer.parseInt(arguments[0]);
                int endRange = Integer.parseInt(arguments[1]);
                
                if((startRange < endRange) && (startRange > 0) && (endRange > 0))
                {
                    destinationPortSet = true;
                    destinationPortRange[0] = startRange;
                    destinationPortRange[1] = endRange;
                } else {
                    System.out.println("not a valid range, defaulting to all ports");
                }
                
            } catch (Exception e)
            {
                System.out.println("could not parse port arguments");
            }
        }
        
        return 1;
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
                // System.out.println("received: " + clArg[0]);
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
                // System.out.println("received: ");
                // for(String l : clArg) { System.out.println(l + "\n"); }
                return clArg;
            } catch (Exception e){
                System.out.println(option + " is missing an argument");
                return null;
            }            
        }
        
        return null;
        
    }
    
    // scans interfaces and prompts the user to choose one
    public void chooseInterface()
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
    
    public void runOptions() throws Exception
    {
        // checking where to get input from
        if(inputFile == null)
        {
            chooseInterface();
        } else {
            try{
                FileInputStream in = new FileInputStream(inputFile);        
                readStream = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                
            } catch (Exception e)
            {
                System.out.println("Error: Could not open input file" + inputFile + "\n Looking for adapter");
                chooseInterface();
            }

        }
        
        // checking where to send output file to
        if(!(outputFile == null))
        {
            try{
                FileOutputStream f = new FileOutputStream(outputFile);
                System.setOut(new PrintStream(f));
            } catch (Exception e){
                System.out.println("Error: Could not open output file, defaulting to console");
            }
        }

        switch (typeToParse) {
            case "eth":
            
                boolean continueLoopEth = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterEth = 1;
                
                while(continueLoopEth)
                {
                    byte [] packet = getPacket();
                    eth = new EthernetParser();

                    if(packet.length > 14)
                    {
                        eth.parsePacket(packet);
                    
                        if(headerOnly)
                        {
                            eth.printHeaderOnly();
                        } else {
                            eth.printAll();
                        }
                        
                        if(counterEth == packetsToCapture)
                        {
                            continueLoopEth = false;
                        }
                        
                        counterEth  = counterEth + 1;
                    }
                    
                    if(doneReading)
                    {
                        continueLoopEth = false;
                    }                    
                    
                }
                
            break;
            case "arp":
                boolean continueLoopArp = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterArp = 1;
                
                while (continueLoopArp)
                {
                    byte [] packet = getPacket();
                    eth = new EthernetParser();
                    arp = new ARPParser();
                    
                    
                    if(packet.length > 14)
                        eth.parsePacket(packet);
                    
                    if(eth.getTypeString().equals("0806"))
                    {
                        eth.printHeaderOnly();
                        arp.parsePacket(packet);
                        
                        if(headerOnly)
                        {
                            arp.printHeaderOnly();
                        } else {
                            arp.printAll();
                        }
                        
                        if(counterArp == packetsToCapture)
                        {
                            continueLoopArp = false;
                        }
                        
                        counterArp = counterArp + 1;
                    }
                    
                    if(doneReading)
                    {
                        continueLoopArp = false;
                    }
                }
            break;
            case "ip":
                boolean continueLoopIp = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterIp = 1;
                
                while(continueLoopIp)
                {
            
                    eth = new EthernetParser();
                    ip = new IPPacketParser();
                    
                    byte []  packet = getPacket();
                    //ip.parsePacket(packet);
                    //ip.printAll();
                    if(packet.length > 14)
                        eth.parsePacket(packet);
                    
                    if(eth.getTypeString().equals("0800"))
                    {
                        ip.parsePacket(packet);
                        
                        // check that the address passes the ip address filter
                        if(checkIPAddressFilter(ip.getSourceAddressString(),ip.getDestinationAddressString()))
                        {
                            if(headerOnly)
                            {
                                ip.printHeaderOnly();
                            } else {
                                ip.printAll();
                            }
                            
                            if(counterIp == packetsToCapture)
                            {
                                continueLoopIp = false;
                            }
                            
                            counterIp = counterIp + 1;                        
                        }
                    }
                    
                    if(doneReading)
                    {
                        continueLoopIp = false;
                    }

                }
            break;
            case "icmp":
                // print payload
                // if function for done reading
                // if function to print payload or only header
                boolean continueLoopIcmp = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterIcmp = 1;
                
                // System.out.println("parsing for icmp");
            break;
            case "tcp":
                boolean continueLoopTcp = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterTcp = 1;

            
                while(continueLoopTcp)
                {
                    eth = new EthernetParser();
                    ip =  new IPPacketParser();
                    tcp = new TCPParser();
                
                    byte [] packet = getPacket();
          
                    // check that that the packet received has more than ethernet
                    if(packet.length > 14)
                        eth.parsePacket(packet);
        
                    // check that the packet is an IPv4 valued packet
                    if(eth.getTypeString().equals("0800"))
                    {
                        //eth.printHeaderOnly();
                        ip.parsePacket(packet);
                        
                        // check that the address passes the ip address filter
                        if(checkIPAddressFilter(ip.getSourceAddressString(),ip.getDestinationAddressString()))
                        {
                            ip.printHeaderOnly();
                            // check that the protocol is TCP
                            if(Integer.parseInt(ip.getProtocolString()) == 6)
                            {
                                tcp.parsePacket(packet);
                                if(headerOnly)
                                {
                                    tcp.printHeaderOnly();
                                } else {
                                    tcp.printAll();
                                }
                                
                                if(counterTcp == packetsToCapture)
                                {
                                    continueLoopTcp = false;
                                }
                                
                                counterTcp = counterTcp + 1;
                            }
                        }
                    }
                    
                    if(doneReading)
                    {
                        continueLoopTcp = false;
                    }

                }
            break;
            case "udp":
                boolean continueLoopUdp = ((packetsToCapture == -1) ? true: ((packetsToCapture != 0) ? true: false));
                int counterUdp = 1;
                
                while(continueLoopUdp)
                {

                    eth = new EthernetParser();
                    ip =  new IPPacketParser();
                    udp = new UDPParser();
                
                    byte [] packet = getPacket();
          
                    if(packet.length > 14)
                        eth.parsePacket(packet);
        
                    if(eth.getTypeString().equals("0800"))
                    {
                        //eth.printHeaderOnly();
                        ip.parsePacket(packet);

                        
                        // check that the address passes the ip address filter
                        if(checkIPAddressFilter(ip.getSourceAddressString(),ip.getDestinationAddressString()))
                        {
                            ip.printHeaderOnly();
                            if(Integer.parseInt(ip.getProtocolString()) == 17)
                            {
                                // printed byte representation of udp packet, for dns parsing later
                                // char[] hexArray = "0123456789ABCDEF".toCharArray();
                                // char[] hexChars = new char[packet.length * 2];
                                // for ( int j = 0; j < packet.length; j++ ) {
                                //     int v = packet[j] & 0xFF;
                                //     hexChars[j * 2] = hexArray[v >>> 4];
                                //     hexChars[j * 2 + 1] = hexArray[v & 0x0F];
                                // }
                                
                                // System.out.println(new String(hexChars));                                
                                
                                udp.parsePacket(packet);
                                if(headerOnly)
                                {
                                    udp.printHeaderOnly();
                                } else {
                                    udp.printAll();
                                }
                                

                                
                                if(counterUdp == packetsToCapture)
                                {
                                    continueLoopUdp = false;
                                }
                                
                                counterUdp = counterUdp + 1;
                            }
                        }
                    }
            
                    if(doneReading)
                    {
                        continueLoopUdp = false;
                    }
                }
            break;
        }        
    }
    
    public byte[] getPacket() throws Exception
    {
        byte [] packet; // temporary initialization
        boolean endOfFile = false;
        
        if(inputFile == null)
        {
            // read from NIC
            packet = driver.readPacket();
            
        } else {
            try{
                // using Byte vector since we don't know the size of the packet
                Vector<Byte> byteAccumulator = new Vector<Byte>();
                String temp;
                Byte [] finalPacket;
                
                do
                {
                    // string needs to be converted into Byte class array
                    // needs to go through byte primitive step
                    String [] byteString;
                    
                    if((temp = readStream.readLine()) != null)
                    {
                        byteString = temp.split(" ");
                    }
                    else{
                        endOfFile = true;
                        break;
                    }
                    // System.out.println(Arrays.toString(byteString));

                    Byte [] byteTemp = new Byte[byteString.length];
                    
                    if(!temp.isEmpty())
                    {
                        for(int counter = 0; counter < byteString.length; counter++)
                        {
                            byteTemp[counter] = new Byte(DatatypeConverter.parseHexBinary(byteString[counter])[0]);
                        }                        
                        
                        byteAccumulator.addAll(Arrays.asList(byteTemp));
                    }
                    
                    
                } while (!temp.isEmpty());
                
                // check that end of file hasn't been reached yet
                if(!endOfFile)
                {
                    finalPacket = new Byte[byteAccumulator.size()];
                    packet = new byte[byteAccumulator.size()];
                    byteAccumulator.toArray(finalPacket);
                    
                    for(int counter = 0; counter < finalPacket.length; counter++)
                    {
                        packet[counter] = finalPacket[counter];
                    }
                } else {
                    packet = new byte[50];
                    doneReading = true;
                }
                
            } catch (Exception e){
                packet = new byte[50];
                System.out.println("Error: Could not read from input file");
                doneReading = true;
            }   
        }   
        return packet;
    }
    
    public boolean checkIPAddressFilter(String currentSrc, String currentDst)
    {
        boolean passFilter = true;
        
        // check which options were set by the user
        if(!(andset || orset))
        {
            if(sourceAddress != null)
            {
                if(!currentSrc.equals(sourceAddress))
                    passFilter = false;
            }
            else if(destinationAddress != null)
            {
                if(!currentDst.equals(destinationAddress))
                    passFilter = false;
            }
        } else if (orset)
        {
            if(!(currentSrc.equals(sourceAddress) || currentDst.equals(destinationAddress)))
            {
                passFilter = false;
            }
        } else if (andset)
        {
            if(!(currentSrc.equals(sourceAddress) && currentDst.equals(destinationAddress)))
            {
                passFilter = false;
            }
        }
        
        return passFilter;
    }    
    
    public boolean checkPortRange()
    {
        boolean passFilter = true;
        
        return passFilter;
    }
}