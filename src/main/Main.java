import main.Parser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    static Queue<File> fileList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        fileSearch(new File(args[0]));
        String[] filePath = args[0].split("\\\\");

        CodeWriter codeWriter = new CodeWriter(new File(args[0] + "\\" + filePath[filePath.length-1] + ".asm"));

        while (!fileList.isEmpty()){
            File file = fileList.poll();

            Parser parser = new Parser(file);

            while(parser.hasMoreCommands()){
                parser.advance();

                if(parser.commandType().equals("C_ARITHMETIC")){
                    codeWriter.writerArithmetic(parser.arg1());
                } else if (parser.commandType().equals("C_PUSH") | parser.commandType().equals("C_POP")){
                    codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                } else if(parser.commandType().equals("C_LABEL")){
                    codeWriter.writeLabel(parser.arg1());
                } else if(parser.commandType().equals("C_GOTO")){
                    codeWriter.writeGoto(parser.arg1());
                } else if(parser.commandType().equals("C_IF")){
                    codeWriter.writeIf(parser.arg1());
                }
            }

        }
        codeWriter.close();

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
