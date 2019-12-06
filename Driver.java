public class Driver {
    public static void main(String[] args) {
        System.out.println("\u001B[32m");

        Volume  vol = new Volume("ext2fs.txt");
        Helper help = new Helper();
        //help.outputBlock(file, 15873);

        // // Read superblock information and group descriptor information
        // vol.getSuperBlock().outputSuperblockInformation();
        // System.out.println("\u001B[36m" + "INode table pointer = " + vol.getINodeTablePointer() + "\u001B[0m");

        // //Reading a tale of two cities 
        // Ext2File  f = new Ext2File (vol, "/two-cities.txt");
        // f.seek(0);
        // byte buf[] = f.read(f.getSize());
        // System.out.format ("%s\n", new String(buf));
        // help.dumpHexBytes(buf);

        vol.viewFileContents("/files");

        //Reading the deep file 
        //Ext2File  f = new Ext2File (vol, "/deep/down/in/the/filesystem/there/lived/a/file.txt");
        Ext2File  f = new Ext2File (vol,"/files/dir-e.txt");
        byte buf[] = f.read(f.getSize());
        System.out.format ("%s\n", new String(buf));
        //help.dumpHexBytes(buf);
        


        //vol.viewFileContents("/root");
       
        


        System.out.println("\u001B[0m");
    }

    
}