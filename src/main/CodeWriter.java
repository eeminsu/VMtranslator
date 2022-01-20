package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CodeWriter {
    private BufferedWriter bw;

    public CodeWriter(File file) throws IOException {
        bw = new BufferedWriter(new FileWriter(file));
    }

    public void setFileName(String fileName){

    }

    public void writerArithmetic(String command) throws IOException {
        switch (command){
            case "add" :
                bw.write(addSubAndOr() + "M=D+M\n");
                break;
            case "sub" :
                bw.write(addSubAndOr() + "M=M-D\n");
                break;
            case "neg" :
                bw.write(negNot() + "M=D-M\n");
                break;
            case "eq" :
                bw.write(eqGtLt("eq", "JEQ"));
                break;
            case "gt" :
                bw.write(eqGtLt("gt", "JGT"));
                break;
            case "lt" :
                bw.write(eqGtLt("lt", "JLT"));
                break;
            case "and" :
                bw.write(addSubAndOr() + "M=D&M\n");
                break;
            case "or" :
                bw.write(addSubAndOr() + "M=D|M\n");
                break;
            case "not" :
                bw.write(negNot() + "M=!M\n");
                break;
        }

    }

    public void writePushPop(String command, String segment, String index) throws IOException {
        switch (command){
            case "C_PUSH" :
                switch (segment){
                    case "argument" :
                        bw.write(pushArgLcl("ARG", index));
                        break;
                    case "local" :
                        bw.write(pushArgLcl("LCL", index));
                        break;
                    case "static" :
                        bw.write(pushStatic(16 + Integer.parseInt(index)));
                        break;
                    case "constant" :
                        bw.write(pushConstant(index));
                        break;
                    case "this" :
                    case "that" :
                        bw.write(pushThisThat(segment.toUpperCase(), index));
                        break;
                    case "pointer" :
                        bw.write(pushPointerTmp("R3", index));
                        break;
                    case "temp" :
                        bw.write(pushPointerTmp("R5", index));
                        break;
                }
                break;
            case "C_POP" :
                switch (segment){
                    case "argument" :
                        bw.write(popSegment("ARG", index));
                        break;
                    case "local" :
                        bw.write(popSegment("LCL", index));
                        break;
                    case "static" :
                        bw.write(popStatic(16 + Integer.parseInt(index)));
                        break;
                    case "this" :
                    case "that" :
                        bw.write(popSegment(segment.toUpperCase(), index));
                        break;
                    case "pointer" :
                        bw.write(popSegment("R3", index));
                        break;
                    case "temp" :
                        bw.write(pushPointerTmp("R5", index));
                        break;
                }
        }
    }

    public void close() throws IOException {
        bw.close();
    }

    private String negNot(){
        return "@SP\n" +
                "AM=M-1\n";
    }

    private String addSubAndOr(){
        return negNot() +
                "D=M\n" +
                "A=A-1\n";
    }

    private String eqGtLt(String command, String jump){
        return addSubAndOr() +
                "D=D-M\n" +
                "@" + command + "\n" +
                "D;" + jump + "\n" +
                "@SP\n" +
                "A=M\n" +
                "M=0\n" +
                "(" + command +")\n" +
                "@SP\n" +
                "A=M\n" +
                "M=-1\n";
    }

    private String pushCom(){
        return "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";
    }

    private String pushArgLcl(String segment, String index){
        return "@" + segment + "\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                pushCom();
    }

    private String pushStatic(int index){
        return "@" + index + "\n" +
                "D=M\n" +
                pushCom();
    }

    private String pushConstant(String index){
        return "@" + index + "\n" +
                "D=A\n" +
                pushCom();
    }

    private String pushThisThat(String segment, String index){
        return "@" + segment + "\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                pushCom();
    }

    private String pushPointerTmp(String segment, String index){
        return "@" + index + "\n" +
                "D=A\n" +
                "@" + segment + "\n" +
                "A=A+D\n" +
                "D=M\n" +
                pushCom();
    }

    private String popCom(){
        return "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";
    }

    private String popSegment(String segment, String index){
        return "@" + segment + "\n" +
                "D=A\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                popCom();
    }

    private String popStatic(int index){
        return "@" + index + "\n" +
                "D=A\n" +
                popCom();
    }

}
