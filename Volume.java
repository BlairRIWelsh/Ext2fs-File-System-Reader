import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.io.IOException;

public class Volume {

    private int iNodeTablePointer;
    private int blockSize = 1024;

    public Volume(String filePath) {
        try {
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            Superblock superblock = new Superblock(blockSize * 1, file);
            //superblock.outputSuperblockInformation();
            iNodeTablePointer = findiNodeTablePointer(file);
            //System.out.println(iNodeTablePointer);
            RootDirectory root = new RootDirectory(file, 2);
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

    public int getINodeTablePointer() {
        return iNodeTablePointer;
    }

    public int getBlockSize() {
        return blockSize;
    }
}