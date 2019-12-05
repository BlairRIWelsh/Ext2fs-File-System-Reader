import java.io.RandomAccessFile;
    import java.io.IOException;
    import java.net.URL;
    import java.nio.ByteBuffer;
    import java.nio.ByteOrder;
    import java.math.BigInteger; 
    import java.nio.charset.Charset;

/**
 * A class to represent a superblock
 * This class ignores some information a usual superblock would have as is is deemed unneccesary for us
 */
public class Superblock {

    private int superblockLocation;
    private int magicNumber;
    private int totalNumINodesInFileSystem;
    private int totalNumBlocksInFileSystem;
    private int totalNumBlocksInGroup;
    private int totalNumINodesInGroup;
    private int sizeOfEachINodeInBytes;
    private String diskName;

    /**
     * Creates a new superblock from the location given.
     * @param location the start byte of the superblock in the file
     * @param file the file to read from 
     */
    public Superblock(int location, RandomAccessFile file) {
        superblockLocation = location;
        magicNumber = readNbytes(2,superblockLocation + 56,file);
        totalNumINodesInFileSystem = readNbytes(4,superblockLocation + 0,file);
        totalNumBlocksInFileSystem = readNbytes(4,superblockLocation + 4,file);
        totalNumBlocksInGroup = readNbytes(4,superblockLocation + 32,file);
        totalNumINodesInGroup = readNbytes(4,superblockLocation + 40,file);
        sizeOfEachINodeInBytes = readNbytes(4,superblockLocation + 88,file);
        diskName = readString(16, superblockLocation + 120,file);
        //outputSuperblockInformation();
    }

    /**
     * Output the Superblock Information into the console
     */
    public void outputSuperblockInformation() {
        System.out.println("\u001B[36m" + " ");
        System.out.format("Magic number: %1$04X \n", magicNumber & 0xFFFF);
        System.out.println("Total Inodes in file system: " + totalNumINodesInFileSystem);
        System.out.println("Total Blocks in file system: " + totalNumBlocksInFileSystem);
        System.out.println("Number of Block groups in file system: " + (int) Math.ceil((float) totalNumBlocksInFileSystem / (float)totalNumBlocksInGroup));
        System.out.println("Total Blocks per Group: " + totalNumBlocksInGroup);
        System.out.println("Total Inodes per Group: " + totalNumINodesInGroup);
        System.out.println("Size of each Inode in bytes: " + sizeOfEachINodeInBytes);
        System.out.println("Volume Labelx (Disk Name): " + diskName);
        System.out.println("\u001B[0m" + " ");
    }

    /**
     * Read a string from a given file
     * @param numberOfBytesToRead //how long the string is in bytes
     * @param startBit //the starting location of the string
     * @param file //the file to read from
     * @return //the string read from the file
     */
    public String readString(int numberOfBytesToRead, int startBit,RandomAccessFile file) {
        try {
            byte[] bytes = new byte[numberOfBytesToRead];
            file.seek(startBit);
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * Reads 2 or 4 bytes from a file given a location
     * @param numberOfBytesToRead //the number of bytes to read, can be 2 or 4
     * @param startBit //the starting location of what you want to read
     * @param file //the file to read from 
     * @return
     */
    public static int readNbytes(int numberOfBytesToRead, int startBit, RandomAccessFile file) {
        try {
            int num = 0;
            byte[] bytes = new byte[numberOfBytesToRead];
            file.seek(startBit);
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            if (numberOfBytesToRead == 2) {
                short s = wrapped.getShort();
                return Integer.valueOf(s);
            } else if (numberOfBytesToRead == 4) {
                num = wrapped.getInt();
                return num;
            } 
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** 
     * @return the total Number of INodes In the FileSystem 
     */
    public int getTotalNumINodesInFileSystem() {return totalNumINodesInFileSystem;}

    /** 
     * @return the total Number of blocks In the FileSystem 
     */
    public int getTotalNumBlocksInFileSystem() {return totalNumBlocksInFileSystem;}

    /** 
     * @return the total Number of blocks in a group 
     */
    public int getTotalNumBlocksInGroup() {return totalNumBlocksInGroup;}

    /** 
     * @return the total Number of INodes in a group
     */
    public int getTotalNumINodesInGroup() {return totalNumINodesInGroup;}

    /** 
     * @return the size of each iNode in bytes
     */
    public int getSizeOfEachINodeInBytes() {return sizeOfEachINodeInBytes;}

    /** 
     * @return the disk name as a String
     */
    public String getDiskName() {return diskName;}
    
}