import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

public class File {
    INode iNode;
    RandomAccessFile file;

    int iNodeNumber;
    int length;
    int nameLength;
    int fileType;
    String fileName;

    public File(RandomAccessFile f, int i, int l, int nl, int ft, String n) {
        file = f;
        iNodeNumber = i; 
        length = l;
        nameLength = nl;
        fileType = ft;
        fileName = n;
        iNode = new INode(file,iNodeNumber);

        //System.out.println("\u001b[31m File created \u001b[0m");

        //outputFileBytes(file, iNode);
    }

    public byte[] outputFileBytes() {//RandomAccessFile file, INode iNode) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

        for (int i = 1; i < (iNode.getpointersToDataBlocks()).size(); i++) {
            int dataBlockNumber = (int) iNode.getpointersToDataBlocks().get(i);
            //System.out.println(dataBlockNumber);
            try {
                outputStream.write(readDataBlock(file, dataBlockNumber));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte c[] = outputStream.toByteArray();
        //System.out.format ("%s\n", new String(c));
        return c;
    }

    private byte[] readDataBlock(RandomAccessFile file, int dataBlockNumber) {
        byte[] bytes = new byte[1024];
        try {
            file.seek(dataBlockNumber * 1024); //!!!
            file.read(bytes);
            ByteBuffer wrapped = ByteBuffer.wrap(bytes);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return bytes;
        }
    }

    public INode getINode() {
        return iNode;
    }   

    public String getFileName() {
        return fileName;
    }

    public File getFile() {
        return this;
    }
}
