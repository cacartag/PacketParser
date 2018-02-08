public class TCPParser{
    private int sourcePort;
    private String sourcePortString;
    
    private int destinationPort;
    private String destinationPortString;

    private int sequenceNumber;
    private String sequenceNumberString;
    
    private int acknowledgementNumber;
    private String acknowledgementNumberString;
    
    public String getSourcePortString() { return sourcePortString; }
    
    public String getDestinationPortString() { return destinationPortString; }
    
    public void parsePacket(byte [] packet)
    {
        sourcePort = (int)(packet[34] & 0xFF);
        sourcePort = sourcePort << 8;
        sourcePort = sourcePort + (int)(packet[35] & 0xFF);
        
        sourcePortString = Integer.toString(sourcePort);
        
        destinationPort = (int)(packet[36] & 0xFF);
        destinationPort = destinationPort * 256;
        destinationPort = destinationPort + (int)(packet[37] & 0xFF);
        
        destinationPortString = Integer.toString(destinationPort);
        
        //sequenceNumber = (int)
        
    }

    TCPParser()
    {
        sourcePort = 0;
        sourcePortString = "";
        
        destinationPort = 0;
        destinationPortString = "";
    }
}