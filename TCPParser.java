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
    
    public String getSourcePortString() { return sourcePortString; }
    
    public String getDestinationPortString() { return destinationPortString; }
    
    public String getSequenceNumberString() { return sequenceNumberString; }
    
    public String getAcknowledgementNumberString() { return acknowledgementNumberString; }
    
    public String getOffsetString() { return offsetString; }
    
    public String getReservedString() { return reservedString; }
    
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
    }
}