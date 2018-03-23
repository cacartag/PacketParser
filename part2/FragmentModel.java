import java.util.TreeMap;
import java.sql.Timestamp;

public class FragmentModel
{
    private int sid;
    private TreeMap<Integer,IPPacketParser> fragments;
    private IPPacketParser reassembledPacket;
    private Timestamp receiveFirstPacketTime;
    
    FragmentModel()
    {
        sid = -1;
        fragments = new TreeMap<Integer,IPPacketParser>();
        reassembledPacket = new IPPacketParser();
        receiveFirstPacketTime = new Timestamp(0);
    }
    
    public int getSid() { return sid; }
    
    public void setSid(int s) { sid = s; }
    
    public TreeMap<Integer,IPPacketParser> getFragments() { return fragments; }

    public void setFragments(TreeMap<Integer,IPPacketParser> f) { fragments = f; }
    
    public IPPacketParser getReassembledPacket() { return reassembledPacket; }
    
    public void setReassembledPacket(IPPacketParser r) { reassembledPacket = r; }
    
    public Timestamp getReceiveFirstPacketTime() { return receiveFirstPacketTime; }
    
    public void setReceiveFirstPacketTime(Timestamp r) { receiveFirstPacketTime = r; }

}