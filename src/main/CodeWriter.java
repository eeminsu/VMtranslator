package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter bw;

    public CodeWriter(File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
    }

    public void setFileName(String fileName){

    }

    public void writerArithmetic(String command){

    }

    public void writePushPop(String command, String segment, String index){

    }

    public void close(){

    }

}
