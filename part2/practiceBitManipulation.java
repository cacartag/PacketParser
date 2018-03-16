public class practiceBitManipulation
{


    public static void main(String[] args)
    {
     byte[] packetIP = {0x45,0x00,0x00,0x73,0x00,0x00,0x40,0x00,0x40,0x11,(byte)0xb8,(byte)0x61,(byte)0xc0,(byte)0xa8,0x00,0x01,(byte)0xc0,(byte)0xa8,0x00,(byte)0xc7};
    
    int checkSum = 0;
    for(int x = 0; x < 20; x+=2)
    {
        int inter = (int)(((packetIP[x] & 0xff) << 8) | (packetIP[(x + 1)] & 0xff));
        
        //System.out.println("inter is: " + inter);
        
        
        checkSum += inter;

        ///System.out.println("Check sum is: " + checkSum);

    }
    
    //System.out.println("Checksum is: " + checkSum);
    byte [] result = new byte[5];
    
    result[0] = ((byte)(checkSum >> 16 & 0x0F));
    result[1] = ((byte)(checkSum >> 12 & 0x0F));
    result[2] = ((byte)(checkSum >> 8 & 0x0F));
    result[3] = ((byte)(checkSum >> 4 & 0x0F));
    result[4] = ((byte)(checkSum & 0x0F));
    
    int done = (result[1] << 12) | (result[2] << 8) | (result[3] << 4) | (result[4]);
    done= done + (int)(result[0] & 0xFF);
    //
    System.out.println("Check sum is " + (long)((~done)&0x0000ffff));
    //for(int x = 0; x < 5; x++)
    //System.out.printf("0x%02X\n", result[x]);
    //System.out.println("hello world");
    }
    
}