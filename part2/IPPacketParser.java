// references: https://github.com/floodlight/floodlight/blob/master/src/main/java/net/floodlightcontroller/packet/TCP.java
import java.lang.*;
import java.util.Arrays;

public class IPPacketParser{
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
    private byte [] flags;
    private String flagString;
    
    private int fragmentOffset;
    private String fragmentOffsetString;
    
    private boolean isFragment;
    
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
    
    private byte [] headerBytes;
    
    private int calculatedChecksum;
    
    private byte [] packetIP;
    
    private byte [] payload;
    
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
    
    public boolean getIfFragment() { return isFragment; }
    
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
        int versionIHLByte = 0;
        int dscpECNByte = 0;
        
        versionIHLByte = (int)(packet[14] & 0xFF); 
        dscpECNByte = (int)(packet[15] & 0xFF);
        
        totalLength = (int)(packet[16] & 0xFF);
        totalLength = totalLength << 8;
        totalLength = totalLength + (int)(packet[17] & 0xFF);

        id = (int)(packet[18] & 0xFF);
        id = id << 8;
        id = id + (int)(packet[19] & 0xFF);
        
        flag = (int)(packet[20]);
        
        fragmentOffset = (int)(packet[20] & 0x1F);
        fragmentOffset = fragmentOffset << 8;
        fragmentOffset = fragmentOffset + (int)(packet[21] & 0xFF);
        
        ttl = (int)(packet[22] & 0xFF);
        
        protocol = (int)(packet[23] & 0xFF);
        
        headerChecksum = (int)(packet[24] & 0xFF);
        headerChecksum = headerChecksum << 8;
        headerChecksum = headerChecksum + (int)(packet[25] & 0xFF);
        
        
        
        sourceAddress[0] = (int)(packet[26] & 0xFF);
        sourceAddress[1] = (int)(packet[27] & 0xFF);
        sourceAddress[2] = (int)(packet[28] & 0xFF);
        sourceAddress[3] = (int)(packet[29] & 0xFF);
        
        destinationAddress[0] = (int)(packet[30] & 0xFF);
        destinationAddress[1] = (int)(packet[31] & 0xFF);
        destinationAddress[2] = (int)(packet[32] & 0xFF);
        destinationAddress[3] = (int)(packet[33] & 0xFF);
        
        packetIP = packet;
        packetIP[24] = 0x00;
        packetIP[25] = 0x00;
        
        calculatedChecksum = calculateCheckSum();
        
        payload = Arrays.copyOfRange(packet, 34, packet.length - 1);
        
        versionString = Integer.toString((versionIHLByte >> 4) & 15);
        
        ihlString = Integer.toString(versionIHLByte & 15);
  
        dscpString = Integer.toString((dscpECNByte >> 2) & 63);
        
        ecnString = Integer.toString((dscpECNByte) & 3);
        
        lengthString = Integer.toString(totalLength);

        idString = Integer.toString(id);
        
        flagString = Integer.toString((flag >> 5) & 0xFF);
        flags[2] = (byte)((flag >> 5) & 0x01);
        flags[1] = (byte)((flag >> 6) & 0x01);
        flags[0] = (byte)((flag >> 7) & 0x01);
        
        if(((int)(flags[2]) == 1) || ((int)fragmentOffset > 0))
        {
            isFragment = true;
        } else {
            isFragment = false;
        }
        
        
        fragmentOffsetString = Integer.toString(fragmentOffset);
        
        ttlString = Integer.toString(ttl);
        
        protocolString = Integer.toString(protocol);
        
        headerChecksumString = Integer.toString(headerChecksum);
        
        for(int index = 0; index < sourceAddress.length; index++)
        {
            sourceAddressString += Integer.toString(sourceAddress[index]);
                if(index != 3)
                    sourceAddressString += ".";
        }
        
        for(int index = 0; index < destinationAddress.length; index++)
        {
            destinationAddressString += Integer.toString(destinationAddress[index]);
            if (index != 3)
                destinationAddressString += ".";
                
        }
    
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
        
        fragmentOffset = 0;
        fragmentOffsetString = "";
        
        ttl = 0;
        ttlString = "";
        
        protocol = 0;
        protocolString = "0";
        
        headerChecksum = 0;
        headerChecksumString = "";
        
        sourceAddress = new int[] {0,0,0,0};
        sourceAddressString = "";
        
        destinationAddress = new int[] {0,0,0,0};
        destinationAddressString = "";
        
        flags = new byte[3];
        
        payload = new byte[10];
    }

    private int calculateCheckSum()
    {
        int checkSum = 0;
        for(int x = 0; x < 20; x+=2)
        {
            int inter = (int)(((packetIP[14 + x] & 0xff) << 8) | (packetIP[14 + (x + 1)] & 0xff));

            checkSum += inter;
        }
        
        byte [] result = new byte[5];
        
        result[0] = ((byte)(checkSum >> 16 & 0x0F));
        result[1] = ((byte)(checkSum >> 12 & 0x0F));
        result[2] = ((byte)(checkSum >> 8 & 0x0F));
        result[3] = ((byte)(checkSum >> 4 & 0x0F));
        result[4] = ((byte)(checkSum & 0x0F));
        
        int done = (result[1] << 12) | (result[2] << 8) | (result[3] << 4) | (result[4]);
        done= done + (int)(result[0] & 0xFF);

        return ((~done) & 0x0000FFFF);
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
        //System.out.println("Flags: " + flagString);
        System.out.println("Reserved Flag is: " + (int)(flags[0]));
        System.out.println("Don't Fragment flag is: " + (int)(flags[1]));
        System.out.println("More Fragments flag is: " + (int)(flags[2]));
  
        System.out.println("Fragment Offset: " + fragmentOffsetString);
        System.out.println("TTL: " + ttlString);
        System.out.println("Protocol: " + protocolString);
        System.out.println("Header Checksum: " + headerChecksumString);
        System.out.println("Source IP Address: " + sourceAddressString);
        System.out.println("Destination IP Address: " + destinationAddressString);
        System.out.println("Caculated checSum is: " + calculatedChecksum);
        
        try
        {
            String payloadString = new String(payload, "UTF-8");           
            System.out.println(payloadString);
        } catch(Exception e)
        {
            System.out.println("Could not convert payload to string");
        }

        System.out.println("\n\n\n");
    }
    
    public void printHeaderOnly()
    {
        System.out.println("IP Header:");
        System.out.println("IP version: " + versionString);
        System.out.println("IP length: " + ihlString);
        System.out.println("IP DSCP: " + dscpString);
        System.out.println("IP ECN: " + ecnString);
        System.out.println("IP packet length: "+ lengthString);
        System.out.println("Identification: " + idString);
        //System.out.println("Flags: " + flagString);
        System.out.println("Reserved Flag is: " + (int)(flags[0]));
        System.out.println("Don't Fragment flag is: " + (int)(flags[1]));
        System.out.println("More Fragments flag is: " + (int)(flags[2]));
        System.out.println("Fragment Offset: " + fragmentOffsetString);
        System.out.println("TTL: " + ttlString);
        System.out.println("Protocol: " + protocolString);
        System.out.println("Header Checksum: " + headerChecksumString);
        System.out.println("Source IP Address: " + sourceAddressString);
        System.out.println("Destination IP Address: " + destinationAddressString);
        System.out.println("Calculated Checksum: " + calculatedChecksum);
        
        System.out.println("\n\n\n");
    }
}
