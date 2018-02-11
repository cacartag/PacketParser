public class EthernetParser{
    private byte [] destBytes;
    private String destString;    
    
    private byte [] srcBytes;
    private String srcString;

    private byte [] typeBytes;
    private String typeString;
    
    public byte [] getDestinationBytes(){ return destBytes; }
    public String getDestinationString(){ return destString; };

    public byte [] getSourceBytes(){ return srcBytes; }
    public String getSourceString(){ return srcString; }

    public byte [] getTypeBytes(){ return typeBytes; }
    public String getTypeString(){ return typeString; }

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
    }
    
    public void clear()
    {
        destBytes = new byte[6];
        destString = "";
        
        srcBytes = new byte[6];
        srcString = "";
        
        typeBytes = new byte[2];
        typeString = "";
    }
    
}


