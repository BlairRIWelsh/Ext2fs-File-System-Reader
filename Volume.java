import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class to represnt a volume
 */
public class Volume {

    private RandomAccessFile file;              // The file the volume will be read from
    private Superblock superblock;              // The first superblock of this respective volume
    private RootDirectory root;                 // The RootDirectory of this volume
    private int iNodeTablePointer;              // The Pointer to the first INode table
    public static final int blockSize = 1024; 

    /**
     * Constructor class for volume. 
     * Creates a file, reads the superblock and iNode table pointer information and creates the Root directory.
     * @param filePath - The file path of the filesystem to read.
     */
    public Volume(String filePath) {
        try {
            file = new RandomAccessFile(filePath, "r");
            superblock = new Superblock(blockSize, file);
            iNodeTablePointer = findFirstiNodeTablePointer(file);
            root = new RootDirectory(file, 2);
            // Helper help = new Helper();
            // help.outputBlock(file, 15873);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the INode table pointer for GroupBlock0.
     * @param file - The file to read from.
     * @return - The iNode table pointer as an Integer.
     */
    private int findFirstiNodeTablePointer(RandomAccessFile file) {
        try {
            byte[] bytes = new byte[4]; 
            file.seek(2048 + 8);    //!!!!  
            file.read(bytes);   //read into a byte array of size 4 / read 4 bytes
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            return wrapped.getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *  Method for viewing the contents of a directory/file given a file path.
     *  @param path - The file path of the directory to read.
     */
    public void viewFileContents(String path) {
        String s = path;
        String[] splittedFileName = s.split("/");                           // Split the path into an Array of strings using the '/' between them
        String fileName = splittedFileName[splittedFileName.length - 1];    // Get the file name as the last item in the File Path
        int levels = splittedFileName.length -1;                            
        boolean found = false;
        NormalDirectory tempDirectory = null;

        //System.out.println("= "+splittedFileName[1]);
        if (splittedFileName[1].isEmpty() | splittedFileName[0].equals(" ") | splittedFileName[1].equals("root") | splittedFileName[1].equals("/root")) { 
            // If the path is the root file output the contents of the root file.
            System.out.println("\u001B[33m"+ "Contents of: root");
            root.getFileInfo();
        } else {
            System.out.println("\u001B[33m"+ "Contents of: " + fileName);
            int counter = 1;
            ArrayList<NormalDirectory> tempSubDirectories = root.getSubDirectories();   // Get the arrayList of SubDirectories for the Root file
            while (counter != levels + 1) {                                 //until we are at the right level...
                for (NormalDirectory i: tempSubDirectories) {                       //for every subdirectory of tempSubDirectories...
                    if (i.getFileName().equals(splittedFileName[counter]) == true) {    // If the name of the subDirectory matches with a parent Directory of the file
                        i.establishDirectory();                                         // Establish that directory, (Reading it's Contents)
                        tempDirectory = i.getNormalDirectory();                         // Set tempDirectory to that Directory 
                        tempSubDirectories = i.getSubDirectories();                     // Set tempSubDirectories to the sub directories of the new tempDirectory 
                        break;
                    }
                }
                counter++;
            }
            tempDirectory.getFileInfo();    // Once the directory in question is found, output its file information
        }
    }

    /** Accessor method for the Root Directory @return - This volumes Root Directory */
    public RootDirectory getRoot() {return root;}

    /** Accessor method for INodeTablePointer @return - This INode table pointer */
    public int getINodeTablePointer() {return iNodeTablePointer;}

    /** Accessor method for BlockSize @return - The size of each block in the File System */
    public int getBlockSize() {return blockSize;}

    /** Accessor method for the SuperBlock @return - This volumes Superblock */
    public Superblock getSuperBlock() {return superblock;}
}