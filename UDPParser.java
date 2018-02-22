import java.util.Arrays;
import java.lang.*;

public class UDPParser 
{
    private int sourcePort;
    private String sourcePortString;
    
    private int destinationPort;
    private String destinationPortString;
    
    private int length;
    private String lengthString;
    
    private int checkSum;
    private String checkSumString;
    
    private byte[] payload;
    
    public String getSourcePortString(){ return sourcePortString; }
    
    public String getDestinationPortString() { return destinationPortString; }
    
    public String getLengthString() { return lengthString; }
    
    public String getCheckSumString() { return checkSumString; }
    
    public String getPayloadString() throws Exception
    { 
        try
        {               
            return new String(payload, "UTF-8"); 
        } catch (Exception e)
        {
            return "Could not convert payload to string";
        }
    }
    
    public void parsePacket(byte [] packet)
    {
        sourcePort = (int)(packet[34] & 0xFF);
        sourcePort = sourcePort << 8;
        sourcePort = sourcePort + (int)(packet[35] & 0xFF);
        
        sourcePortString = Integer.toString(sourcePort);
        
        destinationPort = (int)(packet[36] & 0xFF);
        destinationPort = destinationPort << 8;
        destinationPort = destinationPort + (int)(packet[37] & 0xFF);
        
        destinationPortString = Integer.toString(destinationPort);
        
        length = (int)(packet[38] & 0xFF);
        length = length << 8;
        length = length + (int)(packet[39] & 0xFF);
        
        lengthString = Integer.toString(length);
        
        checkSum = (int)(packet[40] & 0xFF);
        checkSum = checkSum << 8;
        checkSum = checkSum + (int)(packet[41] & 0xFF);
        
        checkSumString = Integer.toString(checkSum);
        
        payload = Arrays.copyOfRange(packet, 42, packet.length - 1);
        
    }
    
    UDPParser()
    {
        sourcePort = 0;
        sourcePortString = "";
        
        destinationPort = 0;
        destinationPortString = "";
        
        length = 0;
        lengthString = "";
        
        checkSum = 0;
        checkSumString = "";
        
    }
    
    public void printAll()
    {
        System.out.println("UDP Header: ");
        System.out.println("Source Port: " + sourcePortString);
        System.out.println("Destination Port: " + destinationPortString);
        System.out.println("Length: " + lengthString);
        System.out.println("Check Sum: " + checkSumString);
        
        try
        {
            String payloadString = new String(payload, "UTF-8");           
            System.out.println(payloadString);
        } catch(Exception e)
        {
            System.out.println("Could not convert payload to string");
        }
        
        System.out.println("\n\n\n");
    }
    
    public void printHeaderOnly()
    {
        System.out.println("UDP Header: ");
        System.out.println("Source Port: " + sourcePortString);
        System.out.println("Destination Port: " + destinationPortString);
        System.out.println("Length: " + lengthString);
        System.out.println("Check Sum: " + checkSumString);
        
        System.out.println("\n\n\n");
    }
}