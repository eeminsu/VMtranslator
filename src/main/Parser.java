package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {

    private Scanner sc;
    private String command = "";
    String[] alrithmetic = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};

    public Parser(File file) throws FileNotFoundException {
        sc = new Scanner(file);
    }

    public boolean hasMoreCommands(){
        return sc.hasNext();
    }

    public void advance(){
        String val = sc.nextLine().trim();

        while (sc.hasNext()){
            if(val != null){
                if(!val.equals("")){
                    if(val.charAt(0) == '/'){
                        val = sc.nextLine().trim();
                    } else {
                        break;
                    }
                } else {
                    val = sc.nextLine().trim();
                }
            } else{
                val = sc.nextLine().trim();
            }
        }
        if(val.contains("//")){
            command = val.substring(0, val.indexOf("//")).trim();
        } else {
            command = val;
        }
    }

    public String commandType(){
        for(int i=0; i<9; i++){
            if(command.equals(alrithmetic[i])){
                return "C_ARITHMETIC";
            }
        }

        if(command.contains("push")){
          return "C_PUSH";
        } else if(command.contains("pop")){
            return "C_POP";
        } else if(command.contains("label")){
            return "C_LABEL";
        } else if(command.contains("goto") && !command.contains("if")){
            return "C_GOTO";
        } else if(command.contains("if-goto")){
            return "C_IF";
        } else if(command.contains("function")){
            return "C_FUNCTION";
        } else if(command.contains("return")){
            return "C_RETURN";
        } else if(command.contains("call")){
            return "C_CALL";
        }

        return "";
    }

    public String arg1(){
        if(commandType().equals("C_ARITHMETIC")) {
            return command;
        } else{
            String[] comArr = command.split(" ");

            return comArr[1];
        }
    }

    public int arg2(){
        if(commandType().equals("C_PUSH") | commandType().equals("C_POP")
        | commandType().equals("C_FUNCTION") | commandType().equals("C_CALL")){
            String[] comArr = command.split(" ");

            return Integer.parseInt(comArr[2]);
        }
        return 0;
    }
}
