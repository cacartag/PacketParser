import java.lang.*;
import java.util.Arrays;

public class EthernetParser{
    private byte [] destBytes;
    private String destString;    
    
    private byte [] srcBytes;
    private String srcString;

    private byte [] typeBytes;
    private String typeString;
    
    private byte [] payload;
    
    public byte [] getDestinationBytes(){ return destBytes; }
    public String getDestinationString(){ return destString; };

    public byte [] getSourceBytes(){ return srcBytes; }
    public String getSourceString(){ return srcString; }

    public byte [] getTypeBytes(){ return typeBytes; }
    public String getTypeString(){ return typeString; }

    public String getStringPayload() throws Exception
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
        for(int index = 0; index < 6; index++)
        {
            destBytes[index] = packet[index];            
        }
        
        for(byte current : destBytes)
        {
            destString += String.format("%02X",current);
        }         

        for(int index = 0; index < 6; index++)
        {
            srcBytes[index] = packet[index + 6]; 
        }

        for(byte current : srcBytes)
        {
            srcString += String.format("%02X",current);
        }        

        for(int index = 0; index < 2; index++)
        {
            typeBytes[index] = packet[index + 12];
        }
        
        for(byte current : typeBytes)
        {
            typeString += String.format("%02X",current);
        }        
    }
    
    EthernetParser()
    {
        destBytes = new byte[6];
        destString = "";
        
        srcBytes = new byte[6];
        srcString = "";
        
        typeBytes = new byte[2];
        typeString = "";
        
        payload = new byte[10];
    }
    
    public void clear()
    {
        destBytes = new byte[6];
        destString = "";
        
        srcBytes = new byte[6];
        srcString = "";
        
        typeBytes = new byte[2];
        typeString = "";
        
        payload = new byte[10];
    }
    
    public void printAll()
    {
        System.out.println("Ethernet Header:");
        System.out.println("Destination: " + getDestinationString());
        System.out.println("Source: " + getSourceString());
        System.out.println("Type: " + getTypeString());
        
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
        System.out.println("Ethernet Header:");
        System.out.println("Destination: " + getDestinationString());
        System.out.println("Source: " + getSourceString());
        System.out.println("Type: " + getTypeString());
    
        System.out.println("\n\n\n");
    }
}


