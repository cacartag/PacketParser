import java.util.Arrays;
import java.lang.*;

public class TCPParser{
    private int sourcePort;
    private String sourcePortString;
    
    private int destinationPort;
    private String destinationPortString;

    private long sequenceNumber;
    private String sequenceNumberString;
    
    private long acknowledgementNumber;
    private String acknowledgementNumberString;
    
    private int offset;
    private String offsetString;
    
    private int reserved;
    private String reservedString;
    
    private int tcpFlags;
    private String tcpFlagsString;
    
    private int window;
    private String windowString;
    
    private int checkSum;
    private String checkSumString;
    
    private int urgentPointer;
    private String urgentPointerString;
    
    private byte[] payload;
    
    public String getSourcePortString() { return sourcePortString; }
    
    public String getDestinationPortString() { return destinationPortString; }
    
    public String getSequenceNumberString() { return sequenceNumberString; }
    
    public String getAcknowledgementNumberString() { return acknowledgementNumberString; }
    
    public String getOffsetString() { return offsetString; }
    
    public String getReservedString() { return reservedString; }
    
    public int getTCPFlags() { return tcpFlags; }
    
    public String getTCPFlagsString() { return tcpFlagsString; }
    
    public String getWindowString() { return windowString; }
    
    public String getCheckSumString() { return checkSumString; }
    
    public String getUrgentPointerString() { return urgentPointerString; }
    
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
        
        int interrumByte = 0;
        
        sequenceNumber = (long)(packet[38] & 0xFF);
        sequenceNumber = sequenceNumber << 8;
        
        interrumByte = (int)(packet[39] & 0xFF);
        sequenceNumber = sequenceNumber + interrumByte;
        sequenceNumber = sequenceNumber << 8;
        
        interrumByte = (int)(packet[40] & 0xFF);
        sequenceNumber = sequenceNumber + interrumByte;
        sequenceNumber = sequenceNumber << 8;
        
        interrumByte = (int)(packet[41] & 0xFF);
        sequenceNumber = sequenceNumber + interrumByte;
        
        sequenceNumberString = Long.toString(sequenceNumber);
        
        acknowledgementNumber = (long)(packet[42] & 0xFF);
        acknowledgementNumber = acknowledgementNumber << 8;
        
        interrumByte = (int)(packet[43] & 0xFF);
        acknowledgementNumber = acknowledgementNumber + interrumByte;
        acknowledgementNumber = acknowledgementNumber << 8;
        
        interrumByte = (int)(packet[44] & 0xFF);
        acknowledgementNumber = acknowledgementNumber + interrumByte;
        acknowledgementNumber = acknowledgementNumber << 8;
        
        interrumByte = (int)(packet[45] & 0xFF);
        acknowledgementNumber = acknowledgementNumber + interrumByte;
        
        acknowledgementNumberString = Long.toString(acknowledgementNumber);
        
        offset = (int)(packet[46] & 0xF0);
        offset = offset >> 4;
        
        offsetString = Integer.toString(offset);
        
        reserved = (int)(packet[46] & 0x0F);
        
        reservedString = Integer.toString(reserved);
        
        tcpFlags = (int)(packet[47] & 0xFF);
        
        if((tcpFlags & 0x80) == 0x80)
            tcpFlagsString += "C: 1 \n";
        else
            tcpFlagsString += "C: 0 \n";
        
        if((tcpFlags & 0x40) == 0x40)
            tcpFlagsString += "E: 1 \n";
        else
            tcpFlagsString += "E: 0 \n";
        
        if((tcpFlags & 0x20) == 0x20)
            tcpFlagsString += "U: 1 \n";
        else 
            tcpFlagsString += "U: 0 \n";
        
        if((tcpFlags & 0x10) == 0x10)
            tcpFlagsString += "A: 1 \n";
        else
            tcpFlagsString += "A: 0 \n";
        
        if((tcpFlags & 0x08) == 0x08)
            tcpFlagsString += "P: 1 \n";
        else
            tcpFlagsString += "P: 0 \n";
        
        if((tcpFlags & 0x04) == 0x04)
            tcpFlagsString += "R: 1 \n";
        else
            tcpFlagsString += "R: 0 \n";
        
        if((tcpFlags & 0x02) == 0x02)
            tcpFlagsString += "S: 1 \n";
        else
            tcpFlagsString += "S: 0 \n";
        
        
        if((tcpFlags & 0x01) == 0x01)
            tcpFlagsString += "F: 1 \n";
        else
            tcpFlagsString += "F: 0 \n";
        
        window = (int)(packet[48] & 0xFF);
        window = window << 8;
        window = window + (int)(packet[49] & 0xFF);
        
        windowString = Integer.toString(window);
        
        checkSum = (int)(packet[50] & 0xFF);
        checkSum = checkSum << 8;
        checkSum = checkSum + (int)(packet[51] & 0xFF);
        
        checkSumString = Integer.toString(checkSum);
        
        urgentPointer = (int)(packet[52] & 0xFF);
        urgentPointer = urgentPointer << 8;
        urgentPointer = urgentPointer + (int)(packet[53] & 0xFF);

        urgentPointerString = Integer.toString(urgentPointer);
        
        if(packet.length > 54)
            payload = Arrays.copyOfRange(packet, 54, packet.length - 1);
        
    }

    TCPParser()
    {
        sourcePort = 0;
        sourcePortString = "";
        
        destinationPort = 0;
        destinationPortString = "";
        
        sequenceNumber = 0;
        sequenceNumberString = "";
        
        offset = 0;
        offsetString = "";
        
        reserved = 0;
        reservedString = "";
        
        tcpFlags = 0;
        tcpFlagsString = "";
        
        window = 0;
        windowString = "";
        
        checkSum = 0;
        checkSumString = "";
        
        urgentPointer = 0;
        urgentPointerString = "";
        
        payload = new byte[10];
    }
    
    public void clear()
    {
        sourcePort = 0;
        sourcePortString = "";
        
        destinationPort = 0;
        destinationPortString = "";
        
        sequenceNumber = 0;
        sequenceNumberString = "";
        
        acknowledgementNumber = 0;
        acknowledgementNumberString = "";
        
        offset = 0;
        offsetString = "";
        
        reserved = 0;
        reservedString = "";
        
        tcpFlags = 0;
        tcpFlagsString = "";
        
        window = 0;
        windowString = "";
        
        checkSum = 0;
        checkSumString = "";

        urgentPointer = 0;
        urgentPointerString = "";
    }
    
    public void printAll()
    {
        System.out.println("TCP Header:");
        System.out.println("Port source: " + getSourcePortString());
        System.out.println("Port destination: " + getDestinationPortString());
        System.out.println("Sequence Number: " + getSequenceNumberString());
        System.out.println("Acknowledgement Number: " + getAcknowledgementNumberString());
        System.out.println("Offset: " + getOffsetString());
        System.out.println("Reserved: " + getReservedString());
        System.out.println("TCP Flags: \n" + getTCPFlagsString());
        System.out.println("Window: " + getWindowString());
        System.out.println("Checksum: " + getCheckSumString());
        System.out.println("Urgent Pointer: " + getUrgentPointerString());
        
        try
        {
            String payloadString = new String(payload, "UTF-8");           
            System.out.println(payloadString);
        } catch(Exception e)
        {
            System.out.println("Could not convert payload to string, or no payload");
        }
        
        System.out.println("\n\n\n");
    }
    
    public void printHeaderOnly()
    {
        System.out.println("TCP Header:");
        System.out.println("Port source: " + getSourcePortString());
        System.out.println("Port destination: " + getDestinationPortString());
        System.out.println("Sequence Number: " + getSequenceNumberString());
        System.out.println("Acknowledgement Number: " + getAcknowledgementNumberString());
        System.out.println("Offset: " + getOffsetString());
        System.out.println("Reserved: " + getReservedString());
        System.out.println("TCP Flags: \n" + getTCPFlagsString());
        System.out.println("Window: " + getWindowString());
        System.out.println("Checksum: " + getCheckSumString());
        System.out.println("Urgent Pointer: " + getUrgentPointerString());
        
        System.out.println("\n\n\n");
        
    }
    
}