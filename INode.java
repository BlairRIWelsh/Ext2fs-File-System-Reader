import java.io.RandomAccessFile;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.math.BigInteger; 
import java.nio.charset.Charset;
import java.util.*; 
import java.util.ArrayList;

/**
 * A class that represents an iNode
 */
public class INode {
    private int iNodeLocation;
    private int fileMode;
    private int userIDOfOwnerLower16Bits;
    private int fileSizeLower32Bits;
    private Date lastAccessTime;
    private Date creationTime;
    private Date lastModifiedTime;
    private Date deletedTime;
    private int groupIDOfOwner;
    private int numberOfHardLinksToFile;
    ArrayList<Integer> pointersToDataBlocks = new ArrayList<Integer>();
    private int indirectPointer;
    private int doubleIndirectPointer;
    private int tripleIndirectPointer;
    private int fileSizeUpper32Bits;
    
    /**
     * Creater class for iNode
     * Sets up an iNode given a file and the iNode number
     * @param file - The file to read from.
     * @param iNodeNumber - The index of this iNode in the iNode table
     */
    public INode(RandomAccessFile file, int iNodeNumber) {
        int blockSize = 1024;
        int numOfBlocksInBlockGroup0 = 8192;
        int iNodeSizeInBytes = 128;
        int sizeOfBootBlock = blockSize;
        int sizeOfSuperBlock = blockSize;
        int sizeOfBlockGroup = (numOfBlocksInBlockGroup0 * blockSize);
        int iNodeTablePointer = 84;
        
        if (iNodeNumber < 1712) { //the iNode is in BlockGroup0
            int iNodeTableBlockPointer = 84;
            iNodeLocation = (iNodeTableBlockPointer * blockSize) + (iNodeNumber - 1) * iNodeSizeInBytes; //((inode table pointer * block size) + (i x inode size)) - for finding the location of the inode in the inode table
        } else if (iNodeNumber > 1712 && iNodeNumber < 3424 ) { //else we have to look in the BlockGroup1
            int iNodeTableBlockPointer = 84;
            iNodeLocation = (sizeOfBlockGroup + (iNodeTableBlockPointer * blockSize)) + ((iNodeNumber-1713 )  * iNodeSizeInBytes);

        } else if (iNodeNumber > 3424) { //else we have to look in the BlockGroup2
            int iNodeTableBlockPointer = 3;
            iNodeLocation = (sizeOfBlockGroup + sizeOfBlockGroup + (iNodeTableBlockPointer * blockSize)) + ((iNodeNumber-3425)  * iNodeSizeInBytes);
            
        }

        fileMode = readNbytes(2, iNodeLocation,file);
        userIDOfOwnerLower16Bits = readNbytes(2, iNodeLocation + 2,file);
        fileSizeLower32Bits = readNbytes(4, iNodeLocation + 4,file);
        
        lastAccessTime = readDate(file, iNodeLocation + 8);
        creationTime = readDate(file, iNodeLocation + 12);
        lastModifiedTime = readDate(file, iNodeLocation + 16);
        deletedTime = readDate(file, iNodeLocation + 20);
        groupIDOfOwner = readNbytes(2, iNodeLocation + 24,file);
        numberOfHardLinksToFile = readNbytes(2, iNodeLocation + 26,file);
        readDataBlockPointers(file, iNodeLocation);
        //System.out.println(pointersToDataBlocks);
        fileSizeUpper32Bits = readNbytes(4, iNodeLocation + 108,file);

        //outputINodeInformation();
    }

    /**
     * Returns the file size of a file using the upper and lower 32 bits
     * @return - The file size of the file this iNode represents
     */
    public long getFileSize() {
        long upp = 0xFFFFFFFF & (long) fileSizeUpper32Bits;
        long low = 0xFFFFFFFF & (long) fileSizeLower32Bits;
        
        upp = upp << 32;

        
        return upp + low;
    }

    /**
     * read all the non-zero pointers to the data blocks into the pointersToDataBlocks arraylist
     * @param file - The file to read from 
     * @param iNodeLocation - The lcoation of the iNode
     */
    public void readDataBlockPointers(RandomAccessFile file, int iNodeLocation) {
         //adds all the pointers to data blocks to the arraylist (long as the are not 0)
        
        for (int i = 28; i <82; i = i + 4) {
            int temp = readNbytes(4, iNodeLocation + i,file);
            if (temp != 0) {
                pointersToDataBlocks.add(temp);
            }
        }
        

        
        indirectPointer = readNbytes(4, iNodeLocation + 88,file);
        //System.out.println(indirectPointer);
        if (indirectPointer != 0) {
            
            // System.out.println("im in");
            // for (int i = 0; i < 1024; i = i + 4) {
            //     int temp = readNbytes(4, (indirectPointer * 1024) + i, file);
            //     if (temp != 0) {
            //         System.out.println(temp);
            //         pointersToDataBlocks.add(temp);
            //     }
            // }
            //search through this block to find the pointer to more blocks and add those to the array list
            //System.out.println("\u001B[31m INode has indirect pointer = " + indirectPointer + "\u001B[0m");
        }
        
        
        
        
        doubleIndirectPointer = readNbytes(4, iNodeLocation + 92,file);
        if (doubleIndirectPointer != 0) {
            //search through this block to find the pointer to more blocks which have pointers to more blocks and add those to the array list
            for (int i = 0; i < 1024; i = i + 4) {
                int temp = readNbytes(4, (doubleIndirectPointer * 1024) + i, file);
                if (temp != 0) {
                    //System.out.println(temp);
                    pointersToDataBlocks.add(temp);
                }
            }
            //System.out.println("\u001B[31m INode has indirect pointer \u001B[0m");
        }
        tripleIndirectPointer = readNbytes(4, iNodeLocation + 96,file);
        if (tripleIndirectPointer != 0) {
            //search through this block to find the pointer to more blocks which have pointers to more blocks which have pointers to more blocks and add those to the array list
            for (int i = 0; i < 1024; i = i + 4) {
                int temp = readNbytes(4, (doubleIndirectPointer * 1024) + i, file);
                if (temp != 0) {
                    for (int j = 0; j < 1024; j = j + 4) {
                        int temp2 = readNbytes(4, (doubleIndirectPointer * 1024) + j, file);
                        if (temp2 != 0) {
                            //System.out.println(temp2);
                            pointersToDataBlocks.add(temp2);
                        }
                    }
                }
            }
            //System.out.println("\u001B[31m INode has indirect pointer \u001B[0m");
        }
    }

    public void outputINodeInformation() {
        System.out.println("\u001B[34m" + " ");
        System.out.println("File Mode: " + fileMode);
        System.out.println("User ID of Owner (lower 16 bits): " + userIDOfOwnerLower16Bits);
        System.out.println("File Size in bytes (lower 32 bits): " + fileSizeLower32Bits);
        System.out.println("Last Access Time: " + lastAccessTime );
        System.out.println("Creation Time " + creationTime);
        System.out.println("Last Modified Time: " + lastModifiedTime);
        System.out.println("Deleted Time: " + deletedTime);
        System.out.println("Group ID of Owner: " + groupIDOfOwner);
        System.out.println("Number of Hard Links: " + numberOfHardLinksToFile);
        System.out.println("Pointer to Data Blocks: " + pointersToDataBlocks);
        System.out.println("Indirect Pointer: " + indirectPointer);
        System.out.println("Double Indirect Pointer: " + doubleIndirectPointer);
        System.out.println("Triple Indirect Pointer: " + tripleIndirectPointer);
        System.out.println("File Size in bytes (upper 32 bits): " + fileSizeUpper32Bits);
        System.out.println("\u001B[0m" + " ");
    }

    public static int readNbytes(int numberOfBytesToRead, int startBit, RandomAccessFile file) {
        try {
            int num = 0;
            byte[] bytes = new byte[numberOfBytesToRead];
            file.seek(startBit);
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            if (numberOfBytesToRead == 2)
            {
                short s = wrapped.getShort();
                //return Integer.toHexString(s & 0xffff);
                
                return Integer.valueOf(s);
            } else if (numberOfBytesToRead == 4)
            {
                num = wrapped.getInt();
                //return Integer.toHexString(num);
                return num;
            } 
            return num;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
   }

    public static Date readDate(RandomAccessFile file, int startBit) {
        try {
            byte[] bytes = new byte[4];
            file.seek(startBit);
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            long myLongVar = 0xFFFFFFFF & (long)wrapped.getInt();
            Date date = new Date(myLongVar * 1000);
            return date;
        } catch (IOException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }

    /** Returns The last modified time for this File. @return - The last modified time */
    public Date getLastModifiedTime() {return lastModifiedTime;}

    /** Returns the Group ID. @return - The Group ID of the owner */
    public int getGroupIDOfOwner() {return groupIDOfOwner;}

    /** Returns The ID. @return - The ID of the owner */
    public int getIDOfOwnerLower16Bits() {return userIDOfOwnerLower16Bits;}

    /** Returns The file mode. @return - The file mode */
    public int getFileMode() {return fileMode;}

    /** Returns The ArrayList of pointers to data blocks. @return - The ArrayList of pointers to data blocks */
    public ArrayList getpointersToDataBlocks() {return pointersToDataBlocks;}

    /** Returns The number of hard links to this file. @return - The number of hard links to this file */
    public int getNumberOfHardLinksToFile() {return numberOfHardLinksToFile;}
}