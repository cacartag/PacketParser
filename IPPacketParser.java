
public class IPPacketParser{
    
    private byte [] versionBytes;
    private String versionString;
    
    private byte [] ihlBytes;
    private String ihlString;
    

    public byte getVersionBytes() { return versionBytes; }
    public String getVersionString() { return versionString; }
    
    public byte getIHLBytes() { return ihlBytes; }
    public String getIHLString() { return ihlString; }
     
    public void parsePacket(byte [] packet)
    {
        public byte versionIHLByte = new byte;
        
        versionIHLByte = packet[14];
        
        for(byte current : versionBytes)
        {
            versionString += String.format("%02X",current);
        }       
        
        for(byte current : ihlBytes)
        {
            ihlString += String.format("%02X",current);
        }
        
    }
    
    IPPacketParser()
    {
        versionBytes = new byte[4];
        versionString = "";
        
        ihlBytes = new byte[4];
        ihlString = "";
    }
}