public class IPPacketParser{
    
    private String versionString;
    
    private String ihlString;

    private String dscpString;
    
    private String ecnString;
    
    private int totalLength;
    private String lengthString;
    
    private int id;
    private String idString;
    
    private int flag;
    private String flagString;
    
    public String getVersionString() { return versionString; }
    
    public String getIHLString() { return ihlString; }
     
    public String getDSCPString() { return dscpString; }
    
    public String getECNString() { return ecnString; }
     
    public String getLengthString() { return lengthString; }
    
    public String getIdentification() { return idString; }
     
    public String getFlagString() { return flagString; }
     
    public void parsePacket(byte [] packet)
    {
        int versionIHLByte = 0;
        int dscpECNByte = 0;
        
        versionIHLByte = (int)packet[14]; 
        dscpECNByte = (int)packet[15];
        
        totalLength = (int)(packet[16] & 0xFF);
        totalLength = totalLength * 256;
        totalLength = totalLength + (int)(packet[17] & 0xFF);

        id = (int)(packet[18] & 0xFF);
        id = id * 256;
        id = id + (int)(packet[19] & 0XFF);
        
        flag = (int)(packet[20]);
        
        versionString = Integer.toString((versionIHLByte >> 4) & 15);
        
        ihlString = Integer.toString(versionIHLByte & 15);
  
        dscpString = Integer.toString((dscpECNByte >> 2) & 63);
        
        ecnString = Integer.toString((dscpECNByte) & 3);
        
        lengthString = Integer.toString(totalLength);

        idString = Integer.toString(id);
        
        flagString = Integer.toString((flag >> 5) & 7);
        
    }
    
    IPPacketParser()
    {
        versionString = "";        
        ihlString = "";
        dscpString = "";
        ecnString = "";
        totalLength = 0;
        lengthString = "";
        id = 0;
        idString = "";
        flag = 0;
        flagString = "";
    }
}