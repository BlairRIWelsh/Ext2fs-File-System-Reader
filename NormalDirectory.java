import java.io.RandomAccessFile;

public class NormalDirectory extends Directory  {

    int iNodeNumber;
    int length;
    int nameLength;
    int fileType;
    String fileName;
    RandomAccessFile file;
 
    public NormalDirectory(RandomAccessFile f, int i, int l, int nl, int ft, String n) {
        super();
        file = f;
        iNodeNumber = i; 
        length = l;
        nameLength = nl;
        fileType = ft;
        fileName = n;
        iNode = new INode(file, iNodeNumber);
        
        //System.out.println("\u001b[31m Normal Directory created \u001b[0m");

    }

    public INode getINode() {
        return iNode;
    }

    public String getFileName() {
        return fileName;
    }

    public NormalDirectory getNormalDirectory() {
        return this;
    }

    public void establishDirectory() {
        
        scanFileContents(iNode, file);
    }
 
}