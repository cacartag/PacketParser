public class ARPParser
{
    private int hardwareType;
    private String hardwareTypeString;
    
    private int protocolType;
    private String protocolTypeString;

    private int hardwareAddLength;
    private String hardwareAddLengthString;
    
    private int protocolAddLength;
    private String protocolAddLengthString;
    
    private int operation;
    private String operationString;
    
    private byte[] senderHardwareAddress;
    private String senderHardwareAddressString;
    
    private byte[] senderIPAddress;
    private String senderIPAddressString;
    
    private byte[] targetHardwareAddress;
    private String targetHardwareAddressString;
    
    private byte[] targetIPAddress;
    private String targetIPAddressString;
    
    public String getHardwareType() { return hardwareTypeString; }
    
    public String getProtocolType() { return protocolTypeString; }
    
    public String getHardwareAddLength() { return hardwareAddLengthString; }
    
    public String getProtocolAddLength() { return protocolAddLengthString; }
    
    public String getOperation() { return operationString; }
    
    public String getSenderHardwareAddress() { return senderHardwareAddressString; }
    
    public String getSenderIPAddress() { return senderIPAddressString; }
    
    public String getTargetHardwareAddress() { return targetHardwareAddressString; }
    
    public String getTargetIPAddress() { return targetIPAddressString; }
    
    public void parsePacket(byte [] packet)
    {
        // hardware type extraction
        hardwareType = (int)(packet[14] & 0xFF);
        hardwareType = hardwareType << 8;
        hardwareType = hardwareType + (int)(packet[15] & 0xFF);
        
        hardwareTypeString = Integer.toString(hardwareType);
        
        // protocol extraction
        protocolType = (int)(packet[16] & 0xFF);
        protocolType = protocolType << 8;
        protocolType = protocolType + (int)(packet[17] & 0xFF);
        
        protocolTypeString = Integer.toString(protocolType);
        
        // hardware add length extraction
        hardwareAddLength = (int)(packet[18] & 0xFF);
        
        hardwareAddLengthString = Integer.toString(hardwareAddLength);
        
        // protocol add length extraction
        protocolAddLength = (int)(packet[19] & 0xFF);
        
        protocolAddLengthString = Integer.toString(protocolAddLength);
        
        // operation extraction
        operation = (int)(packet[20] & 0xFF);
        operation = operation << 8;
        operation = operation + (int)(packet[21] & 0xFF);
        
        operationString = Integer.toString(operation);
        
        // sender hardware address extraction
        for (int count = 0; count < 6; count++)
        {
            senderHardwareAddress[count] = packet[22 + count];
            senderHardwareAddressString += String.format("%02X",senderHardwareAddress[count]);
        }
        
        
        // sender ip address extraction
        for (int count = 0; count < 4; count++)
        {
            senderIPAddress[count] = packet[28 + count];
            senderIPAddressString += Integer.toString((int)(senderIPAddress[count] & 0xFF)) + ".";
        }

        // target hardware address extraction
        for (int count = 0; count < 6; count++)
        {
            targetHardwareAddress[count] = packet[32 + count];
            targetHardwareAddressString += String.format("%02X",targetHardwareAddress[count]);
        }
        
        // target ip address extraction
        for (int count = 0; count < 4; count++)
        {
            targetIPAddress[count] = packet[38 + count];
            targetIPAddressString += Integer.toString((int)(targetIPAddress[count] & 0xFF)) + ".";
        }
        
    }
    
    ARPParser()
    {
        hardwareType = 0;
        hardwareTypeString = "";
        
        protocolType = 0;
        protocolTypeString = "";
        
        hardwareAddLength = 0;
        hardwareAddLengthString = "";
        
        protocolAddLength = 0;
        protocolAddLengthString = "";
        
        operation = 0;
        operationString = "";
        
        senderHardwareAddress = new byte[6];
        senderHardwareAddressString = "";
        
        senderIPAddress = new byte[4];
        senderIPAddressString = "";
        
        targetHardwareAddress = new byte[6];
        targetHardwareAddressString = "";
        
        targetIPAddress = new byte[4];
        targetIPAddressString = "";
    }
}