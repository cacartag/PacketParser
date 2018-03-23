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
    boolean threadsStillAlive;
    AtomicInteger mainDone; 
    IPFragmentAssembler threadsArray[];
  
    FragmentAdministrator(ConcurrentLinkedQueue<FragmentModel> r, AtomicInteger m)
    {
        reassembledPacketQueue = r;
        threadsStillAlive = true;
        mainDone = m;
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

                        out.write("Reassembled Packet is\n");
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
                    }
                    catch (Exception e)
                    {
                        System.err.println("Error out: " + e);
                    }
                    
                    s = reassembledPacketQueue.peek();
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