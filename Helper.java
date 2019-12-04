import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.RandomAccessFile;
import java.io.IOException;

public class Helper {
    /**
     * Outputs an array of bytes as returned by read() in a readable hexadecimal format with printable ASCII codes by the side. 
     * Need to be able to neatly handle having too few bytes -- note the XX entries. For example,
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 00 00 00 | ........ | ........
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 00 00 00 | ........ | ........
     *       48 65 6C 6C 6F 20 57 6F | 72 6C 44 00 00 00 00 00 | Hello Wo | rld.....
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 00 00 00 | ........ | ........
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 XX XX XX | ........ | .....
     * @param bytes
     */
    public void dumpHexBytes (byte[] bytes) {
        System.out.println("\u001B[35m" + " "); //set colour to purple
        String asciiString = "";
        String hexString = "";
        String asciiLine = "";
        String hexLine = "";

        //change byte array to little endian order
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < 64; i++) {
            for (int x = 0; x < 2; x++) {
                for (int j = 0; j < 8; j++) {
                    byte tempByte = wrapped.get();
                    
                    //output byte as hex string
                    String st = String.format("%1$02X",tempByte);
                    hexString = hexString + st + " ";

                    //ouput byte as ascii char
                    char c = (char) tempByte;
                    if (c == ' ' | c < 32 | c > 126){
                        asciiString = asciiString + "_  ";
                    } else { 
                        asciiString = asciiString + c + "  ";  
                    }
                    
                }
                hexLine = hexLine + "| " + hexString;
                asciiLine= asciiLine + "| " + asciiString;
                hexString = "";
                asciiString = "";
            }
            System.out.println(hexLine + "" + asciiLine + "|");
            hexLine = "";
            asciiLine = "";
        }
        System.out.println("\u001B[0m" + "  "); //reset colour
    }

    public void outputBlock(RandomAccessFile file, int blockLocation) {
        try {
            byte[] block = new byte[1024];
            file.seek(blockLocation * 1024);
            file.read(block);
            dumpHexBytes(block);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}