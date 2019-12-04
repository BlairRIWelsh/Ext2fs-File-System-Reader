import java.io.RandomAccessFile;
    import java.io.IOException;
    import java.net.URL;
    import java.nio.ByteBuffer;
    import java.nio.ByteOrder;
    import java.math.BigInteger; 
    import java.nio.charset.Charset;

public class Superblock {
    int superblockLocation;
    int magicNumber;
    int totalNumINodesInFileSystem;
    int totalNumBlocksInFileSystem;
    int totalNumBlocksInGroup;
    int totalNumINodesInGroup;
    int sizeOfEachINodeInBytes;
    String diskName;

    public Superblock(int location, RandomAccessFile file) {
        superblockLocation = location;
        magicNumber = readNbytes(2,superblockLocation + 56,file);
        totalNumINodesInFileSystem = readNbytes(4,1024 + 0,file);
        totalNumBlocksInFileSystem = readNbytes(4,1024 + 4,file);
        totalNumBlocksInGroup = readNbytes(4,1024 + 32,file);
        totalNumINodesInGroup = readNbytes(4,1024 + 40,file);
        sizeOfEachINodeInBytes = readNbytes(4,1024 + 88,file);
        diskName = readString(16, 1024 + 120,file);
        //outputSuperblockInformation();
    }

    public void outputSuperblockInformation() {
        System.out.println("\u001B[32m" + " ");
        System.out.format("Magic number: %1$04X \n", magicNumber & 0xFFFF);

        System.out.println("Total Inodes in file system: " + totalNumINodesInFileSystem);
        System.out.println("Total Blocks in file system: " + totalNumBlocksInFileSystem);
        System.out.println("Total Blocks per Group: " + totalNumBlocksInGroup);
        System.out.println("Total Inodes per Group: " + totalNumINodesInGroup);
        System.out.println("Size of each Inode in bytes: " + sizeOfEachINodeInBytes);
        System.out.println("Volume Labelx (Disk Name): " + diskName);
        System.out.println("\u001B[0m" + " ");
    }

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

    
}