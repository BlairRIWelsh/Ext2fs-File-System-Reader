import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class File {
    INode iNode;
    byte[] data;

    int iNodeNumber;
    int length;
    int nameLength;
    int fileType;
    String fileName;

    public File(RandomAccessFile file, int i, int l, int nl, int ft, String n) {
        iNodeNumber = i; 
        length = l;
        nameLength = nl;
        fileType = ft;
        fileName = n;
        iNode = new INode(file,iNodeNumber);

        System.out.println("\u001b[31m File created \u001b[0m");
        //!read all its data into the data bytearray!
    }

    public void outputFileText() {

    }

    public INode getINode() {
        return iNode;
    }   

    public String getFileName() {
        return fileName;
    }
}
