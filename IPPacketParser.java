public class IPPacketParser{
    // passed in filter options
    private int sord;
    
    private int sand;
    
    private String srcFilter;
    
    private String dstFilter;
    
    // variables used for ip header values
    
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
    
    private int fragmentOffset;
    private String fragmentOffsetString;
    
    private int ttl;
    private String ttlString;
    
    private int protocol;
    private String protocolString;
    
    private int headerChecksum;
    private String headerChecksumString;
    
    private int [] sourceAddress;
    private String sourceAddressString;
    
    private int [] destinationAddress;
    private String destinationAddressString;
    
    public String getVersionString() { return versionString; }
    
    public String getIHLString() { return ihlString; }
     
    public String getDSCPString() { return dscpString; }
    
    public String getECNString() { return ecnString; }
     
    public String getLengthString() { return lengthString; }
    
    public String getIdentification() { return idString; }
     
    public String getFlagString() { return flagString; }
    
    public String getFragmentOffsetString() { return fragmentOffsetString; }
    
    public String getTTLString() { return ttlString; }
    
    public String getProtocolString() { return protocolString; }
     
    public String getHeaderChecksumString() { return headerChecksumString; }
    
    public String getSourceAddressString() { return sourceAddressString; }
    
    public String getDestinationAddressString() { return destinationAddressString; }
     
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
        id = id + (int)(packet[19] & 0xFF);
        
        flag = (int)(packet[20]);
        
        fragmentOffset = (int)(packet[20] & 0x1F);
        fragmentOffset = fragmentOffset * 256;
        fragmentOffset = fragmentOffset + (int)(packet[21] & 0xFF);
        
        ttl = (int)(packet[22] & 0xFF);
        
        protocol = (int)(packet[23] & 0xFF);
        
        headerChecksum = (int)(packet[24] & 0xFF);
        headerChecksum = headerChecksum * 256;
        headerChecksum = headerChecksum + (int)(packet[25] & 0xFF);
        
        sourceAddress[0] = (int)(packet[26] & 0xFF);
        sourceAddress[1] = (int)(packet[27] & 0xFF);
        sourceAddress[2] = (int)(packet[28] & 0xFF);
        sourceAddress[3] = (int)(packet[29] & 0xFF);
        
        destinationAddress[0] = (int)(packet[30] & 0xFF);
        destinationAddress[1] = (int)(packet[31] & 0xFF);
        destinationAddress[2] = (int)(packet[32] & 0xFF);
        destinationAddress[3] = (int)(packet[33] & 0xFF);
        
        versionString = Integer.toString((versionIHLByte >> 4) & 15);
        
        ihlString = Integer.toString(versionIHLByte & 15);
  
        dscpString = Integer.toString((dscpECNByte >> 2) & 63);
        
        ecnString = Integer.toString((dscpECNByte) & 3);
        
        lengthString = Integer.toString(totalLength);

        idString = Integer.toString(id);
        
        flagString = Integer.toString((flag >> 5) & 7);
        
        fragmentOffsetString = Integer.toString(fragmentOffset);
        
        ttlString = Integer.toString(ttl);
        
        protocolString = Integer.toString(protocol);
        
        headerChecksumString = Integer.toString(headerChecksum);
        
        for(int index: sourceAddress)
        {
            sourceAddressString += Integer.toString(index) + ".";
        }
        
        for(int index: destinationAddress)
        {
            destinationAddressString += Integer.toString(index) + ".";
        }
    
    }
    
    IPPacketParser()
    {
        sord = -1;
        sand = -1;
        srcFilter = null;
        dstFilter = null;
        
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
        fragmentOffset = 0;
        fragmentOffsetString = "";
        ttl = 0;
        ttlString = "";
        protocol = 0;
        protocolString = "";
        headerChecksum = 0;
        headerChecksumString = "";
        sourceAddress = new int[] {0,0,0,0};
        sourceAddressString = "";
        destinationAddress = new int[] {0,0,0,0};
        destinationAddressString = "";
    }
    
    public void clear()
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
        fragmentOffset = 0;
        fragmentOffsetString = "";
        ttl = 0;
        ttlString = "";
        protocol = 0;
        protocolString = "";
        headerChecksum = 0;
        headerChecksumString = "";
        sourceAddress = new int[] {0,0,0,0};
        sourceAddressString = "";
        destinationAddress = new int[] {0,0,0,0};
        destinationAddressString = "";
    }
    
    public void printAll()
    {
        System.out.println("IP Header:");
        System.out.println("IP version: " + versionString);
        System.out.println("IP length: " + ihlString);
        System.out.println("IP DSCP: " + dscpString);
        System.out.println("IP ECN: " + ecnString);
        System.out.println("IP packet length: "+ lengthString);
        System.out.println("Identification: " + idString);
        System.out.println("Flags: " + flagString);
        System.out.println("Fragment Offset: " + fragmentOffsetString);
        System.out.println("TTL: " + ttlString);
        System.out.println("Protocol: " + protocolString);
        System.out.println("Header Checksum: " + headerChecksumString);
        System.out.println("Source IP Address: " + sourceAddressString);
        System.out.println("Destination IP Address: " + destinationAddressString);
    }
}
