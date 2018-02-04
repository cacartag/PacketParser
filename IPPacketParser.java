public class IPPacketParser{
    
    private String versionString;
    
    private String ihlString;

    private String dscpString;
    
    private String ecnString;
    
    public String getVersionString() { return versionString; }
    
    public String getIHLString() { return ihlString; }
     
    public String getDSCPString() { return dscpString; }
    
    public String getECNString() { return ecnString; }
     
    public void parsePacket(byte [] packet)
    {
        int versionIHLByte = 0;
        int dscpECNByte = 0;
        
        versionIHLByte = (int)packet[14]; 
        dscpECNByte = (int)packet[15];
        
        versionString = Integer.toString((versionIHLByte >> 4) & 15);
        
        ihlString = Integer.toString(versionIHLByte & 15);
  
        dscpString = Integer.toString((dscpECNByte >> 2) & 63);
        
        ecnString = Integer.toString((dscpECNByte) & 3);
    }
    
    IPPacketParser()
    {
        versionString = "";
        
        ihlString = "";
    }
}