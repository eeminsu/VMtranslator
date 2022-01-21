import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CodeWriter {
    private BufferedWriter bw;
    private int eqCnt = 0;
    private int gtCnt = 0;
    private int ltCnt = 0;

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
                bw.write("D=0\n"+ negNot() + "M=D-M\n");
                break;
            case "eq" :
                bw.write(eqGtLt("eq", "JEQ", eqCnt++));
                break;
            case "gt" :
                bw.write(eqGtLt("gt", "JGT", gtCnt++));
                break;
            case "lt" :
                bw.write(eqGtLt("lt", "JLT", ltCnt++));
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

    public void writePushPop(String command, String segment, int index) throws IOException {
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
                        bw.write(pushStatic(16 + index));
                        break;
                    case "constant" :
                        bw.write(pushConstant(index));
                        break;
                    case "this" :
                    case "that" :
                        bw.write(pushThisThat(segment.toUpperCase(), index));
                        break;
                    case "pointer" :
                        if(index == 0){
                            bw.write(pushPointer("THIS"));
                        } else if(index == 1){
                            bw.write(pushPointer("THAT"));
                        }
                        break;
                    case "temp" :
                        bw.write(pushTemp("R5", index));
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
                        bw.write(popStatic(16 + index));
                        break;
                    case "this" :
                    case "that" :
                        bw.write(popSegment(segment.toUpperCase(), index));
                        break;
                    case "pointer" :
                        if(index == 0){
                            bw.write(popPointer("THIS"));
                        } else if(index == 1){
                            bw.write(popPointer("THAT"));
                        }
                        break;
                    case "temp" :
                        bw.write(popTemp("R5", index));
                        break;
                }
        }
    }

    public void writeLabel(String label) throws IOException {
        bw.write("(" + label + ")\n");
    }

    public void writeGoto(String label) throws IOException {
        String goTo = "@" + label + "\n" +
                "0;JMP\n";

        bw.write(goTo);
    }

    public void writeIf(String label) throws IOException {
        String If = "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@" + label + "\n" +
                "D;JNE\n";

        bw.write(If);
    }

    public void close() throws IOException {
        bw.close();
    }

    private String negNot(){
        return "@SP\n" +
                "A=M-1\n";
    }

    private String addSubAndOr(){
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n";
    }

    private String eqGtLt(String command, String jump, int cnt){
        return addSubAndOr() +
                "D=M-D\n" +
                "@" + command + cnt + "\n" +
                "D;" + jump + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@continue" + command + cnt + "\n" +
                "0;JMP\n" +
                "(" + command + cnt +")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(continue" + command + cnt + ")\n";
    }

    private String pushCom(){
        return "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";
    }

    private String pushArgLcl(String segment, int index){
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

    private String pushConstant(int index){
        return "@" + index + "\n" +
                "D=A\n" +
                pushCom();
    }

    private String pushThisThat(String segment, int index){
        return "@" + segment + "\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                pushCom();
    }

    private String pushPointer(String segment){
        return "@" + segment + "\n" +
                "D=M\n" +
                pushCom();
    }

    private String pushTemp(String segment, int index){
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

    private String popSegment(String segment, int index){
        return "@" + segment + "\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                popCom();
    }

    private String popTemp(String segment, int index){
        return "@" + segment + "\n" +
                "D=A\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                popCom();
    }

    private String popPointer(String segment){
        return "@" + segment + "\n" +
                "D=A\n" +
                popCom();
    }

    private String popStatic(int index){
        return "@" + index + "\n" +
                "D=A\n" +
                popCom();
    }

}
