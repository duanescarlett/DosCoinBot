package core.modules;

import java.io.*;

public class FileWrapper {

    File file;
    BufferedWriter writer;

    public FileWrapper() throws IOException {

        this.file = new File("log.txt");

        //Create the file
        if (file.createNewFile()){
            System.out.println("File is created!");

        }
        this.writer = new BufferedWriter(new FileWriter(this.file));
    }

    public void write(String s) throws IOException {

        //this.writer.write(s);

        char[] st = s.toCharArray();

        for (char c: st) {
            this.writer.append(c);
        }

        this.writer.newLine();
    }

    public void close() throws IOException {
        this.writer.close();
    }
}
