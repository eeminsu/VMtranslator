package main;

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
    }

    @Test
    void hasMoreCommands() {
        assertEquals(parser.hasMoreCommands(), true);
    }

    @Test
    void advance() {
    }

    @Test
    void commandType() {
    }

    @Test
    void arg1() {
    }

    @Test
    void arg2() {
    }
}