import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

/**
 * A class to represent a basic file created when scanning through directory contents. 
 */
public class File {
    
    private INode iNode;
    private RandomAccessFile file;
    private int iNodeNumber;
    private int length;
    private int nameLength;
    private int fileType;
    private String fileName;

    /**
     * Constructor class for a File.
     * @param f - The file to read from.
     * @param i - This file's INode Number.
     * @param l - This file's length in its parent directory's contents
     * @param nl - This file's Name's length in its parent directory's contents
     * @param ft - This file's file type
     * @param n - This file's name
     */
    public File(RandomAccessFile f, int i, int l, int nl, int ft, String n) {
        file = f;
        iNodeNumber = i; 
        length = l;
        nameLength = nl;
        fileType = ft;
        fileName = n;
        iNode = new INode(file,iNodeNumber, fileName);
    }

    /**
     * Returns all the bytes for a given file
     * Using all the data block pointers the iNode provides us, this method reads every block for this file and adds them to one byteArray
     * @return
     */
    public byte[] outputFileBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 1; i < (iNode.getpointersToDataBlocks()).size(); i++) { // For every dataBlock the iNode points too....
            
            int dataBlockNumber = (int) iNode.getpointersToDataBlocks().get(i);
            //System.out.println(dataBlockNumber);
            try {
                outputStream.write(readDataBlock(file, dataBlockNumber)); // Read that dataBlock...
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte c[] = outputStream.toByteArray();  //and add it to byteArray c
        return c;
    }

    /**
     * Reads a data block given a file and a block number.
     * @param file - The file to read from.
     * @param dataBlockNumber - The block number of the specified block.
     * @return - A byte array of the block
     */
    private byte[] readDataBlock(RandomAccessFile file, int dataBlockNumber) {
        byte[] bytes = new byte[1024];
        try {
            file.seek(dataBlockNumber * 1024); //!!!
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return bytes;
        }
    }

    /** Returns the iNode for this Directory. @return - The iNode for the Directory */
    public INode getINode() {return iNode;}   

    /** Returns the File Name for the Directory. @return - The File Name for the Directory. */
    public String getFileName() {return fileName;}

    /** Returns Itself. @return - Itself */
    public File getFile() {return this;}
}
