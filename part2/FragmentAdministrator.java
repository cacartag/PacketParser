import java.lang.Thread;  
import java.util.concurrent.ConcurrentLinkedQueue;
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Arrays;
import java.lang.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class FragmentAdministrator extends Thread
{
    ConcurrentLinkedQueue<FragmentModel> reassembledPacketQueue;
    ConcurrentLinkedQueue<IPFragmentAssembler> threadQueue; 
    ConcurrentLinkedQueue<Map<String,IPPacketParser>> packetQueue;
    boolean threadsStillAlive;
    AtomicInteger mainDone; 
    IPFragmentAssembler threadsArray[];
    Vector<String> doneID;
  
    FragmentAdministrator(ConcurrentLinkedQueue<FragmentModel> r, AtomicInteger m,ConcurrentLinkedQueue<Map<String,IPPacketParser>> p)
    {
        reassembledPacketQueue = r;
        packetQueue = p;
        threadsStillAlive = true;
        mainDone = m;
        doneID = new Vector<String>();
    }

    public void run()
    {
        boolean lastIteration = true;
        BufferedWriter out = null;       
        FileWriter fstream;
        
        try{
            fstream = new FileWriter("Fragment.txt", true); //true tells to append data.
            out = new BufferedWriter(fstream);        
            while((mainDone.get() == 0))
            {
                FragmentModel s = reassembledPacketQueue.peek();
                
                while(s != null)
                {
                    s = reassembledPacketQueue.poll();
    
                    try  
                    {

                        out.write("Reassembled Packet ID"+(s.getReassembledPacket()).getIdentification()+"\n");
                        if(s.getSid() == 2)
                        {
                            out.write("Overlap Detected\n");
                        }
                        
                        if(s.getSid() == 4)
                        {
                            out.write("Timeout Detected\n");
                        }
                        
                        String parsedPacket = (s.getReassembledPacket()).printAllReturn();
                        out.write(parsedPacket);
                        
                        doneID.addElement((s.getReassembledPacket()).getIdentification());
                        
                    }
                    catch (Exception e)
                    {
                        System.err.println("Error out: " + e);
                    }
                    
                    s = reassembledPacketQueue.peek();
                }
                
                // see if any of the finished id packets are holding up queue
                boolean needToFlush = true;
                Map<String,IPPacketParser> queueFlush = packetQueue.peek();
                Object doneArray[] = doneID.toArray();                 
                
                while(needToFlush && (queueFlush != null))
                {
                    needToFlush = false;
                    //check if the packets id matches finished packet
                
                    for(int x = 0; x < doneArray.length; x++)
                    {
                        if(queueFlush.containsKey((String)doneArray[x]))
                        {
                            packetQueue.poll();
                            queueFlush = packetQueue.peek();
                            needToFlush = true;
                        }
                    }
                }
                
                
            }
            
            out.close();
        } catch (Exception exce)
        {
            System.err.println("Error: " + exce);
        }
        
        
        
        System.out.println("main Done, and no more threads");
    }
}