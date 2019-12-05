import java.io.RandomAccessFile;

/**
 * A class to represent any Directory that is not the Root Directory.
 */
public class NormalDirectory extends Directory  {

    private int iNodeNumber;
    private int length;
    private int nameLength;
    private int fileType;
    private String fileName;
    private RandomAccessFile file;
 
    public NormalDirectory(RandomAccessFile f, int i, int l, int nl, int ft, String n) {
        super();
        file = f;
        iNodeNumber = i; 
        length = l;
        nameLength = nl;
        fileType = ft;
        fileName = n;
        iNode = new INode(file, iNodeNumber);
    }

    /**
     * A method to scan through this directorys contents and find its sub-directories and sub-files.
     */
    public void establishDirectory() {
        scanFileContents(iNode, file);
    }

    /** Returns the iNode for this Directory. @return - The iNode for the Directory */
    public INode getINode() {return iNode;}

    /** Returns the File Name for the Directory. @return - The File Name for the Directory. */
    public String getFileName() {return fileName;}

    /** Returns Itself. @return - Itself */
    public NormalDirectory getNormalDirectory() {return this;}
}