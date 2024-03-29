import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * A class to represent a directory, which can either be a NormalDirectory or a RootDirectory, both of which inherit from this class.
 * @param file - A file to read from.
 * @param iNodeLocation - The location of the iNode for this directory .
 */
public class Directory {
    
    //Every directory will have an iNode, an Array of its files and an array of Sub-Directories.
    INode iNode;
    ArrayList<NormalDirectory> subDirectories = new ArrayList();
    ArrayList<File> subFiles = new ArrayList();
    
    /**
     * Returns contents of a directory in a form like:   
     *   drwxr-xr-x  4 root root   1024 Aug 13 20:20 .
     *   drwxr-xr-x 25 root root   4096 Aug 11 11:15 ..
     *   drwxr-xr-x  3 acs  staff  1024 Aug 13 20:20 home
     *   drwx------  2 root root  12288 Aug 11 11:06 lost+found
     *   -rw-r--r--  1 acs  staff     0 Aug 11 22:17 test
     * @return FileInfo[] 
     */
    //public FileInfo[] getFileInfo () {
    public void getFileInfo() {
        System.out.println("\u001B[33m" + " "); //text colour = yellow
        for (NormalDirectory i : subDirectories) { //for every sub-directory in the directory...
            int tempFileMode = (i.getINode().getFileMode() & 0x0000ffff) ; 
            String s = "d" + getPermissions(tempFileMode); //use the filemode to find the permissions
            System.out.format("%11s  %3d  %5d  %5d  %12d   %28tc  %-32s", s, i.getINode().getNumberOfHardLinksToFile(), i.getINode().getIDOfOwnerLower16Bits(), i.getINode().getGroupIDOfOwner(), i.getINode().getFileSize(), i.getINode().getLastModifiedTime(), i.getFileName());
            System.out.println();
        }
        for (File i : subFiles) { //for every file in the directory...
            int tempFileMode = (i.getINode().getFileMode() & 0x0000ffff) ;
            String s = "-" + getPermissions(tempFileMode); //use the filemode to find the permissions
            System.out.format("%11s  %3d  %5d  %5d  %12d   %28tc  %-32s", s, i.getINode().getNumberOfHardLinksToFile(), i.getINode().getIDOfOwnerLower16Bits(), i.getINode().getGroupIDOfOwner(), i.getINode().getFileSize(), i.getINode().getLastModifiedTime(), i.getFileName());
            System.out.println();
        }
        System.out.println("\u001B[0m" + " "); //reset text colour
    }

    /**
     * Returns the permissions for a file/directory using a given File Mode.
     * @param tempFileMode - The file mode to get the permissions from.
     * @return - A 9 digit string showing the permissions for the File Mode.
     */
    public String getPermissions(int tempFileMode) {
        int secondNibble = (tempFileMode >> 8) & 0xf;
        int thirdNibble = tempFileMode >> 4 & 0xf;
        int fourthNibble = tempFileMode & 0xf;
        String s = "";
        if (secondNibble == 1) {
            s = s + "r";
        } else {
            s = s + "-";
        }
        if (thirdNibble == 0) {
            s = s + "----";
        } else if (thirdNibble == 1) {
            s = s + "---w";
        } else if (thirdNibble == 2) {
            s = s + "--r-";
        } else if (thirdNibble == 3) {
            s = s + "--rw";
        } else if (thirdNibble == 4) {
            s = s + "-x--";
        } else if (thirdNibble == 5) {
            s = s + "-x-w";
        } else if (thirdNibble == 6) {
            s = s + "-xr-";
        } else if (thirdNibble == 7) {
            s = s + "-xrw";
        } else if (thirdNibble == 8) {
            s = s + "w---";
        } else if (thirdNibble == 9) {
            s = s + "w--w";
        } else if (thirdNibble == 10) {
            s = s + "w-r-";
        } else if (thirdNibble == 11) {
            s = s + "w-rw";
        } else if (thirdNibble == 12) {
            s = s + "wx--";
        } else if (thirdNibble == 13) {
            s = s + "wx-w";
        } else if (thirdNibble == 14) {
            s = s + "wxr-";
        } else if (thirdNibble == 15) {
            s = s + "wxrw";
        }if (fourthNibble == 0) {
            s = s + "----";
        } else if (fourthNibble == 1) {
            s = s + "---x";
        } else if (fourthNibble == 2) {
            s = s + "--w-";
        } else if (fourthNibble == 3) {
            s = s + "--wx";
        } else if (fourthNibble == 4) {
            s = s + "-r--";
        } else if (fourthNibble == 5) {
            s = s + "-r-x";
        } else if (fourthNibble == 6) {
            s = s + "-rw-";
        } else if (fourthNibble == 7) {
            s = s + "-rwx";
        } else if (fourthNibble == 8) {
            s = s + "x---";
        } else if (fourthNibble == 9) {
            s = s + "x--x";
        } else if (fourthNibble == 10) {
            s = s + "x-w-";
        } else if (fourthNibble == 11) {
            s = s + "x-wx";
        } else if (fourthNibble == 12) {
            s = s + "xr--";
        } else if (fourthNibble == 13) {
            s = s + "xr-x";
        } else if (fourthNibble == 14) {
            s = s + "xrw-";
        } else if (fourthNibble == 15) {
            s = s + "xrwx";
        }
        return s;
    }

    /**
     * Reads the contents of a directory and if any files or sub-directories exist populate the respective array.
     * @param iNode - The iNode of the current Directory.
     * @param file - The file to read from.
     */
    public void scanFileContents(INode iNode, RandomAccessFile file) {
        int tempINode;
        int counter = 0;

        //Create temporary dynamic arrays to hold the information of any files we might potentially find
        ArrayList iNodes = new ArrayList();
        ArrayList length = new ArrayList();
        ArrayList nameLength = new ArrayList();
        ArrayList fileType = new ArrayList();
        ArrayList fileName = new ArrayList();


        //System.out.println(iNode.getpointersToDataBlocks());
        for (int i = 0; i < iNode.getpointersToDataBlocks().size(); i++) {  //for every pointer in the pointersToDataBlocks array... 
            int location = (int) iNode.getpointersToDataBlocks().get(i);    //get a pointer...
            //System.out.println("in loop " + i + "  location " + location);
            if (location != 2) {
                location = location * 1024; //!!!                           //find the data block in the file its pointing to...
                while (true){
                    
                    tempINode = readNbytes(4, location + counter, file);    //read the next iNode starting at 'location'
                    //System.out.println("temp INode " + tempINode);

                    if (tempINode != 0) {                                   //if the iNode is valid (not 0) add its information to the respective ArrayList
                        iNodes.add(tempINode);
                        length.add(readNbytes(2, location + 4 + counter, file));
                        nameLength.add(readNbytes(1, location + 6 + counter, file));
                        fileType.add(readNbytes(1, location + 7 + counter, file));
                        fileName.add(readString((int) nameLength.get(nameLength.size() - 1)   , location + 8 + counter, file));


                        if (readNbytes(2, location + 4 + counter, file) == 0){  //!if the length of an item is zero something must be wrong so just break
                            break; 
                        }
                        counter = counter + (int) length.get(length.size() - 1);    //increase the counter by the length of this entry, so we can look at the next entry the next time round the loop
                        if (counter >= 1024) { //!!!                                 //!if the counter reaches over 1024, we are in a differnt block, so break
                            
                            break;
                        }
                    } else {    //if the iNode read is 0 it is not valid so break
                        break;  
                    }
                    
                }
            }
            counter = 0;
        }

        
        //viewFileContents(iNodes,length,nameLength,fileType,fileName);

        //create file or directory using the contents of the array lists
        for (int i = 0; i < iNodes.size(); i++) {
            int tempFileType = (int) fileType.get(i);
            if (tempFileType == 2) {
                //create directory 
                NormalDirectory f = new NormalDirectory(file, (int)iNodes.get(i) , (int)length.get(i) , (int)nameLength.get(i) , (int)fileType.get(i), (String)fileName.get(i));
                subDirectories.add(f);
            } else if (tempFileType == 1) {
                //create text file
                File f = new File(file, (int)iNodes.get(i) , (int)length.get(i) , (int)nameLength.get(i) , (int)fileType.get(i), (String)fileName.get(i));
                subFiles.add(f);
            }
        }
        //getFileInfo();
    }

    /**
     * Ouptut the iNode Number, Length, NameLength, File Type, and File Name of all the files within this Directory.
     * Can only be called within scanFileContents.
     * @param iNodes - The array list of iNode Numbers for every sub-file.
     * @param length - The array list of Lengths for every sub-file.
     * @param nameLength - The array list of Name Lengths for every sub-file.
     * @param fileType - The array list of File types for every sub-file.
     * @param fileName - The array list of Names for every sub-file.
     */
    public void viewFileContents(ArrayList iNodes, ArrayList length, ArrayList nameLength, ArrayList fileType, ArrayList fileName) {
        System.out.println("\u001B[33m"+ " ");
        System.out.format("%4s%12s%16s%16s%16s", "INode:","Length:", "NameLength:","File Type:","File Name:");
        for (int i = 0; i < iNodes.size();i++) {  
            System.out.println();
            System.out.format("%4s%12s%16s%16s%18s", (int) iNodes.get(i),(int) length.get(i), (int) nameLength.get(i),(int) fileType.get(i),fileName.get(i));
        }
        System.out.println("\u001B[0m" + " ");
        System.out.println();
    }

    /**
     * Read a string from a given file.
     * @param numberOfBytesToRead - How long the string is in bytes.
     * @param startBit - The starting location of the string.
     * @param file - The file to read from.
     * @return - The string read from the file.
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
     * Reads 2, 4 or 8 bytes from a file given a location
     * @param numberOfBytesToRead //the number of bytes to read, can be 2 or 4
     * @param startBit //the starting location of what you want to read
     * @param file //the file to read from 
     * @return
     */
    public static int readNbytes(int numberOfBytesToRead, int startBit, RandomAccessFile file) 
    {
    try {
        int num = 0;
        byte[] bytes = new byte[numberOfBytesToRead];
        file.seek(startBit);
        file.read(bytes);
        ByteBuffer wrapped = ByteBuffer.wrap(bytes);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        if (numberOfBytesToRead == 1) {
            int i = wrapped.get(); 
            return  i;
        }
        if (numberOfBytesToRead == 2) {
            short s = wrapped.getShort();
            return Integer.valueOf(s);
        } else if (numberOfBytesToRead == 4) {
            num = wrapped.getInt();
            return num;
        } else if (numberOfBytesToRead == 8) {
            num = (int) wrapped.getLong();
            return num;
        } 
        return 0;
    } catch (IOException e) {
        e.printStackTrace();
        return 0;
    }
   }

   /** Returns the iNode for the Directory. @return - The iNode for the Directory */
   public INode getINode() {return iNode;}

   /** Accessor method for the ArrayList of SubFiles @return - The ArrayList of SubFiles */
   public ArrayList<File> getSubFiles() {return subFiles;}

   /** Accessor method for the ArrayList of SubDirectories @return - The ArrayList of SubDirectories */
   public ArrayList<NormalDirectory> getSubDirectories() {return subDirectories;}
}