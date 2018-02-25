import java.util.Scanner;
import java.util.Arrays;
import java.lang.*;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.xml.bind.DatatypeConverter;

public class PacketGenerator{
    private SimplePacketDriver driver;
    private BufferedReader readStream;
    private boolean doneReading;
    
    PacketGenerator()
    {
        driver = new SimplePacketDriver();
        doneReading = false;
    }
    
    public void chooseNIC()
    {
        System.out.println();
        String [] nic = driver.getAdapterNames();
        
        if(nic.length > 0)
        {
            System.out.println("Adapters found: ");
            for(int index = 0; index < nic.length; index++)
            {
                System.out.println("(" + index + ") "+ nic[index]);
            }
            
            System.out.println("Which one to use?");
            try{
                Scanner in = new Scanner(System.in);
                
                int adapter = in.nextInt();
                
                if(driver.openAdapter(nic[adapter]))
                    System.out.println("Adapter is open");
                
                System.out.println("Enter filename of packet");
                
                String fileName = in.next();
                
                System.out.println("selected: " + fileName);
                
                FileInputStream read = new FileInputStream(fileName);
                readStream = new BufferedReader(new InputStreamReader(read, "UTF-8"));
                   

                while(!doneReading)
                {
                    byte [] packet = getPacket();      
                    if (!driver.sendPacket(packet)) System.out.println("Error sending packet!");
                    else System.out.println("packet sent");
                }
                
            } catch (Exception e)
            {
                System.out.println("Could not process that request\n\n\n");
            }
            

            
        }else{
            System.out.println("Couldn't find adapters\n\n\n");
        }
        
    }

    public byte[] getPacket() throws Exception
    {
        byte [] packet; // temporary initialization
        boolean endOfFile = false;
        
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
   
        return packet;
    }
    
    public static void main(String args[])
    {
        PacketGenerator pack = new PacketGenerator();
        
        pack.chooseNIC();
    }
    
}