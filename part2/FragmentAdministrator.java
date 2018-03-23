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
        
        while((mainDone.get() == 0))
        {
            FragmentModel s = reassembledPacketQueue.peek();
            
            while(s != null)
            {
                s = reassembledPacketQueue.poll();
                BufferedWriter out = null;
                try  
                {
                    FileWriter fstream = new FileWriter("Fragment.txt", true); //true tells to append data.
                    out = new BufferedWriter(fstream);
                    out.write("Reassembled Packet is\n");
                    
                    String parsedPacket = (s.getReassembledPacket()).printAllReturn();
                    out.write(parsedPacket);
                    
                    out.close();
                }
                catch (Exception e)
                {
                    System.err.println("Error: " + e);
                }
                
                s = reassembledPacketQueue.peek();
                
            }
            
            //Object threads[] = threadQueue.toArray();
            //
            //if(threads != null)
            //{
            //    threadsStillAlive = false;
            //    for(int x = 0; x < threads.length; x++)
            //    {
            //        if(((IPFragmentAssembler)threads[x]).isAlive())
            //        {
            //            threadsStillAlive = true;
            //        }else{
            //            //threadQueue.remove(threads[x]);
            //        }
            //    }
            //}
            
            //if(!threadsStillAlive && (mainDone.get() == 1) && lastIteration)
            //{
            //    lastIteration = false;
            //    try{
            //        Thread.sleep(5000);
            //        threadsStillAlive = true;
            //    } catch(Exception e)
            //    {
            //        e.printStackTrace();
            //    }
            //}
        }
        
        System.out.println("main Done, and no more threads");
    }
}