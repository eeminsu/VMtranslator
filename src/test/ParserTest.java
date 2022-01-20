import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    static main.Parser parser;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        parser = new main.Parser(new File("C:\\Users\\ADMIN\\Desktop\\project\\VMtranslator\\src\\test\\main\\test.txt"));

        if(parser.hasMoreCommands()){
            parser.advance();
        }
    }

    @Test
    void hasMoreCommands() {
        assertEquals(parser.hasMoreCommands(), true);
    }

    @Test
    void commandType() {
        assertEquals(parser.commandType(), "C_PUSH");
    }

    @Test
    void arg1() {
        assertEquals(parser.arg1(), "constant");
    }

    @Test
    void arg2() {
        assertEquals(parser.arg2(), 7);
    }
}