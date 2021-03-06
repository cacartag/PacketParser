import java.util.Scanner;

public class PacketTester{


    public static void main(String [] args)
    {
        SimplePacketDriver driver = new SimplePacketDriver();
        EthernetParser eth = new EthernetParser();
        IPPacketParser ip = new IPPacketParser();
        UDPParser udp = new UDPParser();
        ARPParser arp = new ARPParser();
        ICMPParser icmp = new ICMPParser();
        Scanner scan = new Scanner(System.in);

        //Get adapter names and print info
        String[] adapters=driver.getAdapterNames();
        System.out.println("Number of adapters: "+adapters.length);
        for (int i=0; i< adapters.length; i++) 
            System.out.println(i+") "+ " Device name in Java ="+adapters[i]);

        System.out.println("Choose an interface");
        int NIC = scan.nextInt();
        
        
        //Open first found adapter (usually first Ethernet card found)
        if (driver.openAdapter(adapters[NIC])) 
            System.out.println("Adapter is open: "+adapters[NIC]);
        
        //Read a packet (blocking operation)
        byte [] packet;
        
//        while(true){        
        eth.parsePacket(driver.readPacket());
  
        while(true)
        {
            eth = new EthernetParser();
            ip = new IPPacketParser();
            
            packet = driver.readPacket();
            
            eth.parsePacket(packet);
            //eth.printAll();
            
            if(eth.getTypeString().equals("0800"))
            {
                ip.parsePacket(packet);
                //ip.printHeaderOnly();
                
                if(Integer.parseInt(ip.getProtocolString()) == 1)
                {
                    icmp.parsePacket(packet);
                    icmp.printAll();
                    //protocolCaptured = Integer.parseInt(ip.getProtocolString());   
                }                    
            }
        }
  
//        while(Integer.parseInt(ip.getProtocolString()) != 17)
//        {
//            System.out.println("next");
//            eth = new EthernetParser();
//            ip = new IPPacketParser();
//            udp = new UDPParser();
//            
//            packet = driver.readPacket();
//            eth.parsePacket(packet);
//            eth.printAll();
//            
//            if(eth.getTypeString().equals("0800"))
//            {
//                ip.parsePacket(packet);
//                ip.printAll();
//                
//                if(Integer.parseInt(ip.getProtocolString()) == 17)
//                {
//                    udp.parsePacket(packet);
//                    udp.printAll();
//                }
//            }
//            
//        }
        
        //while(
            // Wrap it into a ByteBuffer
            // ByteBuffer Packet=ByteBuffer.wrap(packet);
            // Print packet summary
            // System.out.println("Packet: "+Packet+" with capacity: "+Packet.capacity());
            // System.out.println(driver.byteArrayToString(packet));
//        }
//        System.out.println("Hello World");
    }

}