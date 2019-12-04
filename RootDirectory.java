import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class RootDirectory extends Directory {
   
    public RootDirectory(RandomAccessFile file, int iNodeLocation) {
        iNode = new INode(file,iNodeLocation);
        scanFileContents(iNode, file);

        // Helper help = new Helper();
        // help.outputBlock(file, 1024 + (8192 * 1024) + 1024);

        // Helper help = new Helper();
        // help.outputBlock(file, 16385);
    }

    public INode getINode() {
        return iNode;
    }
}