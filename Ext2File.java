import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;

public class Ext2File {
    File reference;
    private long position = 0;
    private long size;
    
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
        if (levels == 1) { 
            ArrayList<File> tempContents = vol.getRoot().getSubFiles();
            for (File i: tempContents) {
                if (i.getFileName().equals(fileName) == true) {
                    reference = i.getFile();
                    found = true;
                    //System.out.println("im in");
                }
            }
        } else {
            //System.out.println("havent found t");
            int counter = 1;
            ArrayList<NormalDirectory> tempSubDirectories = vol.getRoot().getSubDirectories();
            while (counter != levels) {
                
                
                for (NormalDirectory i: tempSubDirectories) {
                    //System.out.println(i.getFileName() + " equals " + splittedFileName[counter]);
                    if (i.getFileName().equals(splittedFileName[counter]) == true) {
                        
                        i.establishDirectory();
                        //System.out.println("here");
                        tempDirectory = i.getNormalDirectory();
                        tempSubDirectories = i.getSubDirectories();
                        break;
                        
                    }
                }
                counter++;
            }
            ArrayList<File> tempContents = tempDirectory.getSubFiles();
            for (File i: tempContents) {
                if (i.getFileName().equals(fileName) == true) {
                    reference = i.getFile();
                    found = true;
                    //System.out.println("im in");
                }
            }
        }

        size = reference.getINode().getFileSize();



        
    }

    public int getSize() {return reference.getINode().getFileSize();}

    public byte[] read(long startByte, long length) {

        byte[] temp = reference.outputFileBytes();
        byte[] trim = new byte[(int) length];
        System.arraycopy((Object) temp, (int) startByte, (Object) trim, 0, (int) length);
        position = position + length;
        return trim;
    }

    public byte[] read(long length) {
        byte[] temp = reference.outputFileBytes();
        byte[] trim = new byte[(int) length];
        System.arraycopy((Object) temp, (int) position, (Object)  trim, 0, (int) length);
        position = position + length;
        return trim;
    }

    public void seek (long p) {
        position = p;
    }
}