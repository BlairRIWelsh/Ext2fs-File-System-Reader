import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class RootDirectory extends Directory {
   
    public RootDirectory(RandomAccessFile file, int iNodeLocation) {
        iNode = new INode(file,iNodeLocation);
        scanFileContents(iNode, file);
    }

    public INode getINode() {
        return iNode;
    }
}