import java.nio.ByteBuffer;
import java.util.Scanner;
// import org.apache.commons.cli.*;
// import java.util.Collection;
// javac -cp ".;commons-cli-1.4.jar" -d . Main.java
// java -cp ".;commons-cli-1.4.jar" Main

public class Main
{   
    public static void main(String [] args) throws Exception
    {
        OptionHandler optHandler = new OptionHandler();
        
        if(optHandler.parseOptions(args) == 1)
        {
            optHandler.runOptions();
            
        } else{
            System.out.println("Error in parsing of options, exiting");
        }
    }
}