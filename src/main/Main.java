import main.Parser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    static Queue<File> fileList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        fileSearch(new File(args[0]));

        while (!fileList.isEmpty()){
            File file = fileList.poll();

            Parser parser = new Parser(file);
            CodeWriter codeWriter = new CodeWriter(new File(file.toString().substring(0, file.toString().indexOf(".vm")) + ".asm"));

            while(parser.hasMoreCommands()){
                parser.advance();

                if(parser.commandType().equals("C_ARITHMETIC")){
                    codeWriter.writerArithmetic(parser.arg1());
                }
                if (parser.commandType().equals("C_PUSH") | parser.commandType().equals("C_POP")){
                    codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                }
            }

            codeWriter.close();
        }

    }

    public static void fileSearch(File path){
        if(path.isDirectory()){
            for (File file : path.listFiles()) {
                if (!file.isDirectory()) {
                    if(file.getName().contains(".vm")){
                        fileList.offer(file);
                    }
                } else {
                    fileSearch(file);
                }
            }
        } else {
            if(path.getName().contains(".vm")){
                fileList.offer(path);
            }
        }
    }
}
