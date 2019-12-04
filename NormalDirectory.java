import java.io.RandomAccessFile;

public class NormalDirectory extends Directory  {
    int iNodeNumber;
    int length;
    int nameLength;
    int fileType;
    String fileName;
 
    public NormalDirectory(RandomAccessFile file, int i, int l, int nl, int ft, String n) {
        super();
        iNodeNumber = i; 
        length = l;
        nameLength = nl;
        fileType = ft;
        fileName = n;
        iNode = new INode(file, iNodeNumber);

        System.out.println("\u001b[31m Normal Directory created \u001b[0m");
        //scanFileContents(iNode, file);
    }

    public INode getINode() {
        return iNode;
    }

    public String getFileName() {
        return fileName;
    }

}