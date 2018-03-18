import java.lang.Thread;  
import java.util.concurrent.ConcurrentLinkedQueue;
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
    
public class IPFragmentAssembler extends Thread {
        
    ConcurrentLinkedQueue<Map<String,IPPacketParser>> packetQueue;
    String myPacketID;
    Timestamp arrival;
    Vector<IPPacketParser> fragments;
    boolean first;
    IPPacketParser firstIP;
    boolean last;
    IPPacketParser lastIP;
        
    IPFragmentAssembler(ConcurrentLinkedQueue<Map<String,IPPacketParser>> p,String ID)
    {
        packetQueue = p;
        myPacketID = ID;
        arrival = new Timestamp(System.currentTimeMillis());
        fragments = new Vector<IPPacketParser>();
        first = false;
        last = false;
    }
        
    public void run()
    {
        while(true)
        {
            Map<String,IPPacketParser> s = packetQueue.peek();
            if (s != null)
            {
                if(s.containsKey(myPacketID))
                {
                    System.out.println("In Thread");
                    s = packetQueue.poll();
                    IPPacketParser ip = s.get(myPacketID);
                    
                    byte[] flags = ip.getFlags();
                    
                    ip.printAll();
                    
                    if(Integer.parseInt(ip.getFragmentOffsetString()) == 0)
                    {
                        first = true;
                        firstIP = ip;
                    } else if(((int)(flags[2])) == 0) {
                        last = true;
                        lastIP = ip;
                    } else {
                        fragments.add(ip);                        
                    }
    
                    if(first & last)
                    {
                        //try to join first and last without intermediate packet
                        int firstFragmentEnd = Integer.parseInt(firstIP.getLengthString()) - 20;
                        int lastFragmentStart = Integer.parseInt(lastIP.getFragmentOffsetString()) * 8;
                        int lastFragmentEnd = Integer.parseInt(lastIP.getLengthString()) - 20;
                        
                        if(firstFragmentEnd == lastFragmentStart)
                        {
                            System.out.println("First and last packet can be joined");    
                            
                            byte[] totalPayload;
                            byte[] firstPacketRaw = firstIP.getPacket();
                            byte[] lastPacketRaw = lastIP.getPacket();
                            byte[] firstPacketPayload = Arrays.copyOfRange(firstPacketRaw,34, 34 + firstFragmentEnd);
                            byte[] lastPacketPayload = Arrays.copyOfRange(lastPacketRaw, 34, 34 + lastFragmentEnd);
                            byte[] header = Arrays.copyOfRange(firstPacketRaw, 0, 34);
                            
                            totalPayload = (byte[])ArrayUtils.addAll(firstPacketPayload, lastPacketPayload);
                            
                            String payloadString = new String();
                            int counter = 0;
                            for(byte current : totalPayload)
                            {
                                payloadString += String.format("%02X ",current);
                            }
                            
                            byte[] newPacket = (byte[])ArrayUtils.addAll(header,totalPayload);
                            
                            IPPacketParser ipNew = new IPPacketParser();
                            
                            ipNew.parsePacket(newPacket);
                            
                            System.out.println("Assembled IP Packet is: ");
                            ipNew.printAll();
                            
                            //System.out.println(payloadString);
                        }
                    }
                }
            }
        }
    }
}