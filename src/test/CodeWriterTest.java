import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CodeWriterTest {

    private CodeWriter codeWriter;

    @BeforeEach
    void setUp() throws IOException {
        codeWriter = new CodeWriter(new File("C:\\Users\\ADMIN\\Desktop\\project\\VMtranslator\\src\\test\\main\\test.asm"));
    }

    @Test
    void writerArithmetic() throws IOException {
        String testCom ="add";
        String readFile = "";
        codeWriter.writerArithmetic(testCom);
        codeWriter.close();

        Scanner sc = new Scanner(new File("C:\\Users\\ADMIN\\Desktop\\project\\VMtranslator\\src\\test\\main\\test.asm"));

        while(sc.hasNext()){
            readFile += sc.nextLine() + "\n";
        }
        sc.close();

        String testBed ="@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "M=D+M\n";

        assertEquals(readFile, testBed);

    }

    @Test
    void writePushPop() throws IOException {
        String readFile = "";
        codeWriter.writePushPop("C_PUSH", "argument", 1);
        codeWriter.close();

        Scanner sc = new Scanner(new File("C:\\Users\\ADMIN\\Desktop\\project\\VMtranslator\\src\\test\\main\\test.asm"));

        while(sc.hasNext()){
            readFile += sc.nextLine() + "\n";
        }
        sc.close();

        String testBed = "@ARG\n" +
                "D=M\n" +
                "@1\n" +
                "A=D+A\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";

        assertEquals(readFile, testBed);
    }
}