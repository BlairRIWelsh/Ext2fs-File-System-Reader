import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * A class to represnt the Root Directory.
 */
public class RootDirectory extends Directory {
   
    /**
     * Set up the root directories iNode and its sub-files and sub-directories 
     */
    public RootDirectory(RandomAccessFile file, int iNodeLocation) {
        iNode = new INode(file,iNodeLocation, "root");
        scanFileContents(iNode, file);
    }

    /** Returns the iNode for this Directory. @return - The iNode for the Directory */
    public INode getINode() {return iNode;}
}