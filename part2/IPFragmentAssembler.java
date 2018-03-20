import java.lang.Thread;  
import java.util.concurrent.ConcurrentLinkedQueue;
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
    
public class IPFragmentAssembler extends Thread {
        
    ConcurrentLinkedQueue<Map<String,IPPacketParser>> packetQueue;
    String myPacketID;
    Timestamp arrival;
    TreeMap<Integer,IPPacketParser> fragments;
    boolean first;
    IPPacketParser firstIP;
    boolean last;
    IPPacketParser lastIP;
        
    IPFragmentAssembler(ConcurrentLinkedQueue<Map<String,IPPacketParser>> p,String ID)
    {
        packetQueue = p;
        myPacketID = ID;
        arrival = new Timestamp(System.currentTimeMillis());
        fragments = new TreeMap<Integer,IPPacketParser>();
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
                    
                    //ip.printAll();
                    
                    if(Integer.parseInt(ip.getFragmentOffsetString()) == 0)
                    {
                        first = true;
                        firstIP = ip;
                    } else if(((int)(flags[2])) == 0) {
                        last = true;
                        lastIP = ip;
                    } else {
                        fragments.put(Integer.parseInt(ip.getFragmentOffsetString()),ip);                        
                    }
    
                    if(first & last)
                    {
                        
                        //try to join first and last without intermediate packet
                        int firstFragmentEnd = Integer.parseInt(firstIP.getLengthString()) - 20;
                        int lastFragmentStart = Integer.parseInt(lastIP.getFragmentOffsetString()) * 8;
                        int lastFragmentEnd = Integer.parseInt(lastIP.getLengthString()) - 20;
                        
                        if(firstFragmentEnd == lastFragmentStart)
                        {
                            //System.out.println("First and last packet can be joined");    
                            
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
                        }else
                        {
                            System.out.println("More than Two Fragments");
                             
                            if(fragments.firstEntry() != null)
                            {
                                TreeMap<Integer, IPPacketParser> ifragments = new TreeMap<Integer, IPPacketParser>();
                                Byte [] totalPayload = new Byte[10];
                                
                                for (Map.Entry<Integer, IPPacketParser> entry : fragments.entrySet())
                                {
                                    ifragments.put(entry.getKey(),entry.getValue());
                                }
                                
                                //HashMap<boolean, byte[]> done = packetReassembly(firstIP, ifragments, totalPayload);
                                if(packetReassembly(firstIP, ifragments, totalPayload))
                                {
                                    System.out.println("Reassembly is successful");
                                    String payloadString = new String();
                                    for(Byte cur : totalPayload)
                                    {
                                        payloadString += String.format("%02X ",cur);
                                    }
                                    
                                    System.out.println(payloadString);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public boolean packetReassembly(IPPacketParser current,TreeMap<Integer,IPPacketParser> ifragments,Byte[] totalPayload)
    {
        boolean completed = false;
        
        if(ifragments.firstEntry() != null)
        {
            Integer nextStart = ifragments.firstKey() * 8;
            Integer previousEnd = (Integer.parseInt(current.getLengthString()) - 20) + (Integer.parseInt(current.getFragmentOffsetString()) * 8);
            
            System.out.println("Next Start is: " + nextStart);
            System.out.println("Previous end is: " + previousEnd);
            
            if(nextStart == previousEnd)
            {
                System.out.println("No Overlapping");
                
                // Get Raw packets and calculate starting and end points
                IPPacketParser nextFragment = ifragments.remove(ifragments.firstKey());
                byte[] nextFragmentRaw = nextFragment.getPacket();
                byte[] currentFragmentRaw = current.getPacket();
                int nextFragmentLength = Integer.parseInt(nextFragment.getLengthString()) - 20;
                int previousLength = Integer.parseInt(current.getLengthString()) - 20;
                
                // Retrieve payload of packets
                byte[] nextPacketPayload = Arrays.copyOfRange(nextFragmentRaw, 34, 34 + nextFragmentLength);
                byte[] currentPacketPayload = Arrays.copyOfRange(currentFragmentRaw, 34, 34 + previousLength);
                
                totalPayload = (Byte[])ArrayUtils.addAll(totalPayload, convertbyteToByte(currentPacketPayload));
                totalPayload = (Byte[])ArrayUtils.addAll(totalPayload, convertbyteToByte(nextPacketPayload));
                
                if(packetReassembly(nextFragment,ifragments,totalPayload))
                {
                    return true;
                } else {
                    return false;
                }
                // System.out.println(payloadString);
                
            } else if (nextStart < previousEnd)
            {
                System.out.println("Overlap is detected");
            }
            
            
        }
        
        
        // When this part is reached it's usually due to the last packet being ready to be assembled
        Integer previousEnd = (Integer.parseInt(current.getLengthString()) - 20) + (Integer.parseInt(current.getFragmentOffsetString()) * 8);
        Integer lastStart = Integer.parseInt(lastIP.getFragmentOffsetString()) * 8;
        
        if(previousEnd == lastStart)
        {
            System.out.println("last packet no overlapping");
            
            // Get raw packets and length
            byte[] lastFragmentRaw = lastIP.getPacket();
            int lastFragmentLength = Integer.parseInt(lastIP.getLengthString()) - 20;
            
            // Retrieve payload of last packet
            byte[] nextPacketPayload = Arrays.copyOfRange(lastFragmentRaw, 34, 34 + lastFragmentLength);
            
            totalPayload = (Byte[])ArrayUtils.addAll(totalPayload, convertbyteToByte(nextPacketPayload));
            
            String payloadString = new String();
            for(Byte cur : totalPayload)
            {
                payloadString += String.format("%02X ",cur);
            }
            
            System.out.println(payloadString);
            
            completed = true;
        }else if (lastStart < previousEnd)
        {
            System.out.println("overlap detected");
            completed = true;
        }
        
        return completed;
    }
    
    public Byte[] convertbyteToByte(byte[] toConvert)
    {
        Byte[] converted = new Byte[toConvert.length];
        
        int i=0;    
        // Associating Byte array values with bytes. (byte[] to Byte[])
        for(byte b: toConvert)
        {
            converted[i] = b;
            i++;
        }
        
        return converted;
    }
}



