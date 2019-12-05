public class Driver {
    public static void main(String[] args) {
        System.out.println("\u001B[32m");



        Volume  vol = new Volume("ext2fs.txt");
        Helper help = new Helper();


        // Ext2File  f = new Ext2File (vol, "/two-cities.txt");
        //Ext2File  f = new Ext2File (vol, "/deep/down/in/the/filesystem/there/lived/a/file.txt");

        //vol.viewFileContents("/lost+found");
        vol.viewFileContents("/root");
        //f.seek(10);
        //byte buf[] = f.read(f.getSize());
        // byte buf[] = f.read(0L,f.getSize());
        // System.out.format ("%s\n", new String(buf));
        // help.dumpHexBytes(buf);

        // vol.getSuperBlock().outputSuperblockInformation();
        // System.out.println("\u001B[36m" + "INode table pointer = " + vol.getINodeTablePointer() + "\u001B[0m");



        System.out.println("\u001B[0m");
    }

    
}