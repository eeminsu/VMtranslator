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
    private int labelCnt = 0;

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
                        bw.write(pushArgLclThisThat("ARG", index));
                        break;
                    case "local" :
                        bw.write(pushArgLclThisThat("LCL", index));
                        break;
                    case "static" :
                        bw.write(pushStatic(16 + index));
                        break;
                    case "constant" :
                        bw.write(pushConstant(index));
                        break;
                    case "this" :
                    case "that" :
                        bw.write(pushArgLclThisThat(segment.toUpperCase(), index));
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

    private boolean labelChk(String label){
        if(label.matches("^\\D[\\w.:]*$")){
            return true;
        }
        return false;
    }

    public void writeInit() throws IOException {
        bw.write("@256\n" +
                "D=A\n" +
                "@SP\n" +
                "M=D\n");

        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) throws IOException {
        if(!labelChk(label)){
            return;
        }

        bw.write("(" + label + ")\n");
    }

    public void writeGoto(String label) throws IOException {
        if(!labelChk(label)){
            return;
        }

        String goTo = "@" + label + "\n" +
                "0;JMP\n";

        bw.write(goTo);
    }

    public void writeIf(String label) throws IOException {
        if(!labelChk(label)){
            return;
        }

        String If = "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@" + label + "\n" +
                "D;JNE\n";

        bw.write(If);
    }

    public void writeCall(String functionName, int numArgs) throws IOException {
        String label = "RETURN" + labelCnt++;

        //push return addr
        bw.write("@" + label + "\n" +
                "D=A\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n");

        //push LCL
        writePushPop("C_PUSH", "local", 0);

        //push ARG
        writePushPop("C_PUSH", "argument", 0);

        //push THIS
        writePushPop("C_PUSH", "this", 0);

        //push THAT
        writePushPop("C_PUSH", "that", 0);

        //ARG = SP - numArgs - 5
        bw.write("@SP\n" +
                "D=M\n" +
                "@5\n" +
                "D=D-A\n" +
                "@" + numArgs + "\n" +
                "D=D-A\n" +
                "@ARG\n" +
                "M=D\n");

        //LCL = SP
        bw.write("@SP\n" +
                "D=M\n" +
                "@LCL\n" +
                "M=D\n");

        //goto f
        writeGoto(functionName);

        //(return addr)
        bw.write("(" + label + ")\n");
    }

    public void writeFunction(String functionName, int numLocals) throws IOException {
        //(f)
        bw.write("(" + functionName + ")\n");

        //repeat k times
        for(int i=0; i<numLocals; i++){
            //PUSH 0
            writePushPop("C_PUSH", "constant", 0);
        }
    }

    public void writeReturn() throws IOException {
        //FRAME = LCL
        bw.write("@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n");

        //RET = *(FRAME-5)
        bw.write("@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n");

        //*ARG = pop()
        writePushPop("C_POP", "argument", 0);

        //SP = ARG + 1
        bw.write("@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n");

        //THAT = *(FRAME-1)
        bw.write(framePop("THAT"));

        //THIS = *(FRAME-2)
        bw.write(framePop("THIS"));

        //ARG = *(FRAME-3)
        bw.write(framePop("ARG"));

        //LCL = *(FRAME-4)
        bw.write(framePop("LCL"));

        //goto RET
        bw.write("@R12\n" +
                "A=M\n" +
                "0;JMP\n");
    }

    private String framePop(String segment){
        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + segment + "\n" +
                "M=D\n";
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

    private String pushArgLclThisThat(String segment, int index){
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
