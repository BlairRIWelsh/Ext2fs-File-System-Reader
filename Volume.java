import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;
import java.util.ArrayList;

public class Volume {

    private RandomAccessFile file;
    private Superblock superblock;
    private RootDirectory root;
    private int iNodeTablePointer;
    public static int blockSize = 1024;

    public Volume(String filePath) {
        
        try {
            file = new RandomAccessFile(filePath, "r");
            superblock = new Superblock(blockSize, file);
            //superblock.outputSuperblockInformation();
            iNodeTablePointer = findiNodeTablePointer(file);
            //System.out.println(iNodeTablePointer);
            root = new RootDirectory(file, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findiNodeTablePointer(RandomAccessFile file) {
        try {
            byte[] bytes = new byte[4];
            file.seek(2048 + 8);
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            return wrapped.getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public RootDirectory getRoot() {
        return root;
    }

    public int getINodeTablePointer() {
        return iNodeTablePointer;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public Superblock getSuperBlock() {
        return superblock;
    }

    public void viewFileContents(String path) {
        String s = path;
        String[] splittedFileName = s.split("/"); 
        String fileName = splittedFileName[splittedFileName.length - 1];
        int levels = splittedFileName.length -1;
        boolean found = false;
        NormalDirectory tempDirectory = null;

        //System.out.println("= "+splittedFileName[1]);
        if (splittedFileName[1].isEmpty() | splittedFileName[0].equals(" ") | splittedFileName[1].equals("root") | splittedFileName[1].equals("/root")) {
            System.out.println("\u001B[33m"+ "Contents of: root");
            root.getFileInfo();
        } else {
            System.out.println("\u001B[33m"+ "Contents of: " + fileName);
            int counter = 1;
            ArrayList<NormalDirectory> tempSubDirectories = root.getSubDirectories();
            while (counter != levels + 1) {
                for (NormalDirectory i: tempSubDirectories) {
                    //System.out.println(i);
                    //System.out.println(i.getFileName() + " equals " + splittedFileName[counter]);
                    
                    if (i.getFileName().equals(splittedFileName[counter]) == true) {
                        //System.out.println("Establishing "+ i.getFileName());
                        i.establishDirectory();
                        //System.out.println("established ");
                        tempDirectory = i.getNormalDirectory();
                        tempSubDirectories = i.getSubDirectories();
                        break;
                        
                    }
                }
                counter++;
            }
            tempDirectory.getFileInfo();
        }
        
        




    }
}