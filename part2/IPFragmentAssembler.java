import java.lang.Thread;  
import java.util.concurrent.ConcurrentLinkedQueue;
    
public class IPFragmentAssembler extends Thread {
        
    String passedParameter;
    ConcurrentLinkedQueue<String[]> packetQueue;
    String myPacketID;
        
    IPFragmentAssembler(ConcurrentLinkedQueue<String[]> p,String ID)
    {
        packetQueue = p;
        myPacketID = ID;
    }
        
    public void run()
    {
        while(true)
        {
            String[] s = packetQueue.peek();
            if (s != null)
            {
                if(s[0].equals(myPacketID))
                {
                    s = packetQueue.poll();
                    System.out.println(myPacketID + "   Thread can see:  " + s[1]);
                }
            }
            
            //try{
            //    Thread.sleep(100);
            //} catch(Exception e)
            //{
            //    e.printStackTrace();
            //}
        }
    }
        

    //public static void main(String [] args)
    //{
    //    System.out.println("Start of threading");
    //    new threading().start();
    //}

}