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
    
    public String getSourcePortString(){ return sourcePortString; }
    
    public String getDestinationPortString() { return destinationPortString; }
    
    public String getLengthString() { return lengthString; }
    
    public String getCheckSumString() { return checkSumString; }
    
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
        
    }
    
    UDPParser()
    {
        
    }
}