import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * A Helper class able to output Blocks or ByteArrays in a readable hexadecimal format with printable ASCII codes by the side.
 */
public class Helper {

    /**
     * Outputs an array of bytes as returned by read() in a readable hexadecimal format with printable ASCII codes by the side. 
     * Handles having too few bytes by using XX entries. For example,
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 00 00 00 | ........ | ........
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 00 00 00 | ........ | ........
     *       48 65 6C 6C 6F 20 57 6F | 72 6C 44 00 00 00 00 00 | Hello Wo | rld.....
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 00 00 00 | ........ | ........
     *       00 00 00 00 00 00 00 00 | 00 00 00 00 00 XX XX XX | ........ | .....
     * @param bytes - The byte array to output.
     */
    public void dumpHexBytes (byte[] bytes) {
        System.out.println("\u001B[35m" + " "); //set colour to purple
        
        String asciiString = "";
        String hexString = "";
        String asciiLine = "";
        String hexLine = "";
        int length = bytes.length;
        int rows = (int)Math.floor(length / 16) + 1;
        boolean finished = false;
        int bytesWritten = 0;

        //change byte array to little endian order
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int i = 0; i < rows; i++) {    //for every row of 8 bytes...
            for (int x = 0; x < 2; x++) {       // print 2 rows together...
                for (int j = 0; j < 8; j++) {       //for every byte in a row...
                    if (bytesWritten < length) {        //if the number of bytes we have already written isn't greater than the file length...

                        //add this byte as hex to the String hexString
                        byte tempByte = wrapped.get();
                        String st = String.format("%1$02X",tempByte);
                        hexString = hexString + st + " ";

                        //add this byte as ASCII to the String asciiString
                        char c = (char) tempByte;
                        if (c == 20) {
                            asciiString = asciiString + "   ";
                        } else if (c < 32 | c > 126){
                            asciiString = asciiString + "_  ";
                        } else { 
                            asciiString = asciiString + c + "  ";  
                        }

                    } else {    //if the number of bytes we have already written is greater than the file length...
                        if (j == 0 && x == 2) {     //if we have written the second collum for a row to asciiString and hexString...
                            finished = true;            //change the boolean value to know we dont need to write any more
                        } 
                        hexString = hexString + "XX" + " ";         //add XX's to represnt having too few bytes in the Hex Collums
                        asciiString = asciiString + " " + "  ";     //add '  ' to represent having too few bytes in the ASCII Collums
                        
                    }
                    bytesWritten++;
                }
                if (finished == false) {    
                    hexLine = hexLine + "| " + hexString;       //add the first collum of hex to hexLine
                    asciiLine= asciiLine + "| " + asciiString;  //add the first collum of ascii to asciiLine
                    hexString = "";                             //reset hexString to get the next collum of hex
                    asciiString = "";                           //reset asciiString to get the next collum of ascii
                } 
            }
            System.out.println(hexLine + "" + asciiLine + "|"); //once we have 4 collums, 2 of hex and 2 of ascii, print them on a line together
            hexLine = "";                                       //reset hexLine to get the next line of hex
            asciiLine = "";                                     //reset asciiLine to get the next line of ascii
        }
        System.out.println("\u001B[0m" + "  "); //reset colour
    }

    /**
     * Ouutput a block as a set of hex values (and ASCII).
     * @param file - The file to read from.
     * @param blockLocation - The number of the block.
     */
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