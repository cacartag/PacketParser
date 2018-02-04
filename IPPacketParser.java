
public class IPPacketParser{
    
    private byte [] versionBytes;
    private String versionString;
    
    private byte [] ihlBytes;
    private String ihlString;
    

    public byte [] getVersionBytes() { return versionBytes; }
    public String getVersionString() { return versionString; }
    
    public byte [] getIHLBytes() { return ihlBytes; }
    public String getIHLString() { return ihlString; }
     
    public void parsePacket(byte [] packet)
    {
        int versionIHLByte = 0;
        
        versionIHLByte = (int)packet[14]; 
        
        versionString = Integer.toString((versionIHLByte >> 4) & 15);
        
        ihlString = Integer.toString(versionIHLByte & 15);
        
        //ihlBytes[0] = (versionIHLByte[0] << 4) & 240;
        
        
        //versionString = String.format("%02X",versionBytes[0]);       
        
        //ihlString = String.format("%02X",ihlBytes[0]);   
    }
    
    IPPacketParser()
    {
        versionBytes = new byte[4];
        versionString = "";
        
        ihlBytes = new byte[4];
        ihlString = "";
    }
}