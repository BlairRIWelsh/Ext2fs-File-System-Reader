import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An ext2File class to represent a file opened
 */
public class Ext2File {

    File reference; 
    private long position = 0;
    private long size;
    
    /**
     * A class created in driver to read a file.
     * @param vol - The volume of this file.
     * @param path - The path of this file.
     */
    public Ext2File(Volume vol, String path) {

        //Split the path into an array of strings containing each directory 
        String s = path;
        String[] splittedFileName = s.split("/"); 
        String fileName = splittedFileName[splittedFileName.length - 1];
        fileName = fileName.substring(0, fileName.lastIndexOf('.')); 
        splittedFileName[splittedFileName.length - 1] = "end";

        int levels = splittedFileName.length -1;
        boolean found = false;
        NormalDirectory tempDirectory = null;

        if (levels == 1) { //Check that the file is not located within the root directory
            ArrayList<File> tempContents = vol.getRoot().getSubFiles();
            for (File i: tempContents) {
                if (i.getFileName().equals(fileName) == true) {
                    reference = i.getFile();
                    found = true;
                }
            }
        } else {
            int counter = 1;
            ArrayList<NormalDirectory> tempSubDirectories = vol.getRoot().getSubDirectories(); //Create a temporary directory we will look in to find the file
            while (counter != levels) { //for every level in the file path...
                for (NormalDirectory i: tempSubDirectories) { //for every sub-directory in the current tempDirectory...
                    if (i.getFileName().equals(splittedFileName[counter]) == true) { //check if the current directory (i) equals the directory at that level
                        i.establishDirectory(); //establish the directory and scan through its contents setting up any files or sub-directories
                        tempDirectory = i.getNormalDirectory(); //set tempDirectory to directory i
                        tempSubDirectories = i.getSubDirectories(); //set the tempSubDirectories to be the subDirectories of i
                        break;
                    }
                }
                counter++;
            }

            //Once we reach the final level of the filePath we are at the directory that holds the file
            ArrayList<File> tempContents = tempDirectory.getSubFiles(); //get the file ArrayList of this directory...
            for (File i: tempContents) {    //look through every file in it...
                if (i.getFileName().equals(fileName) == true) { //see if the names match..
                    reference = i.getFile();    //if they do reference points to the file
                    found = true;
                }
            }
        }
        if (found == true) {
            size = reference.getINode().getFileSize(); 
        } else {
            size = 0;
        }
    }

    /**
     * Reads at most length bytes starting at byte offset startByte from start of file. Byte 0 is the first byte in the file.
     * startByte must be such that, 0 ≤ startByte < file.size or an exception should be raised.
     * @param startByte
     * @param length
     * @return
     */
    public byte[] read(long startByte, long length) {
        if (startByte < size & startByte > 0) {
            throw new UnsupportedOperationException("Start byte should be 0 ≤ startByte < file.size");
        } else {
            byte[] temp = reference.outputFileBytes();
            byte[] trim = new byte[temp.length];
            System.arraycopy((Object) temp, (int) startByte, (Object) trim, 0, (int) length);
            position = position + length;
            return trim;
        }
    }

    /**
     * Reads at most length bytes starting at current position in the file.
     * @param length
     * @return
     */
    public byte[] read(long length) {
        if (position> size) {
            throw new UnsupportedOperationException("Position is set beyond the end of the file");
        } else {
            byte[] temp = reference.outputFileBytes();
            byte[] trim = new byte[temp.length];
            System.arraycopy((Object) temp, (int) position, (Object)  trim, 0, (int) length);
            position = position + length;
            return trim;
        }
    }

    /**
     * Move to byte position in file.
     * @param p - position to move to
     */
    public void seek (long p) {
        position = p;
    }

    /** Accessor method for Size. @return the size of this file in bytes */
    public long getSize() {
        //System.out.println(reference.getINode().getFileSize());
        return reference.getINode().getFileSize();}
}