import java.util.Arrays;
import java.lang.*;

public class ICMPParser
{

    private int type;
    private String typeString;
    
    private int code;
    private String codeString;
    
    private int checkSum;
    private String checkSumString;
    
    private byte[] restOfHeader;
    private String restOfHeaderString;
    
    private byte [] payload;
    
    public String getTypeString() { return typeString; }
    
    public String getCodeString() { return codeString; }
    
    public String getCheckSumString() { return checkSumString; }
    
    public String getRestOfHeaderString() { return restOfHeaderString; }
    
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
        type = (int)(packet[34] & 0xFF);
        
        typeString = Integer.toString(type);
        
        code = (int)(packet[35] & 0xFF);
        
        codeString = Integer.toString(code);
        
        checkSum = (int)(packet[36] & 0xFF);
        checkSum = checkSum << 8;
        checkSum = checkSum + (int)(packet[37] & 0xFF);
        
        checkSumString = Integer.toString(checkSum);
        
        for(int index = 0; index < 4; index++)
        {
            restOfHeader[index] = packet[38 + index];
            restOfHeaderString += String.format("%02X",restOfHeader[index]);
        }
        
        payload = Arrays.copyOfRange(packet, 42, packet.length - 1);
        
    }

    ICMPParser()
    {
        type = 0;
        typeString = "";
        
        code = 0;
        codeString = "";
        
        checkSum = 0;
        checkSumString = "";
        
        restOfHeader = new byte[4];
        restOfHeaderString = "";
        
        payload = new byte[50];
    }
    
    public void printAll()
    {
        System.out.println("ICP Header:");
        System.out.println("Type: " + typeString);
        System.out.println("Code: " + codeString);
        System.out.println("Checksum: " + checkSumString);
        System.out.println("Rest of Header: " + restOfHeaderString);
        
        try
        {
            String payloadString = new String(payload, "UTF-8");           
            System.out.println(payloadString);
        } catch(Exception e)
        {
            System.out.println("Could not convert payload to string");
        }
    }

    public void printHeaderOnly()
    {
        System.out.println("ICP Header:");
        System.out.println("Type: " + typeString);
        System.out.println("Code: " + codeString);
        System.out.println("Checksum: " + checkSumString);
        System.out.println("Rest of Header: " + restOfHeaderString);
    }
}