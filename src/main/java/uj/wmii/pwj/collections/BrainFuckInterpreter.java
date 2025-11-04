package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BrainFuckInterpreter implements Brainfuck{
    private final String code;
    private final PrintStream out;
    private final InputStream in;
    private byte[] memory;
    private Map<Integer, Integer> loopMap = new HashMap<>();

    private BrainFuckInterpreter(String code, PrintStream out, InputStream in, int stackSize){
        this.code = checkCode(code);
        this.out = out;
        this.in = in;
        this.memory = new byte[stackSize];
        this.buildLoopMap();
    }

    private String checkCode(String code){
        StringBuilder finalCode = new StringBuilder();
        String commands = "<>+-.,[]";
        for(int i = 0; i < code.length(); i++){
            if(commands.indexOf(code.charAt(i)) != -1){
                finalCode.append(code.charAt(i));
            }
        }
        return finalCode.toString();
    }

    private void buildLoopMap(){
        Stack<Integer> loopStack = new Stack<>();
        for(int i = 0; i < code.length(); i++){
            if(code.charAt(i) == '['){
                loopStack.push(i);
            }else if(code.charAt(i) == ']'){

                int peek = loopStack.pop();
                loopMap.put(peek, i);
                loopMap.put(i, peek);
            }
        }
    }

    public static Brainfuck createInstance(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || program.isEmpty()) {
            throw new IllegalArgumentException("program nie może być null'em lub pusty");
        }
        if (out == null) {
            throw new IllegalArgumentException("strumien wyjsciowy nie moze byc null'em");
        }
        if (in == null) {
            throw new IllegalArgumentException("strumien wejsciowy nie moze byc null'em");
        }
        if (stackSize < 1) {
            throw new IllegalArgumentException("rozmiar pamieci nie moze byc mniejszy niz 1");
        }
        return new BrainFuckInterpreter(program, out, in, stackSize);
    }
    @Override
    public void execute(){
        int memoryPointer = 0;
        int loopPointer = 0;
        for(int i = 0; i < code.length(); i++){
            char instruction = code.charAt(i);
            switch(instruction){
                case '<':
                    memoryPointer--;
                    break;
                case '>':
                    memoryPointer++;
                    break;
                case '+':
                    memory[memoryPointer]++;
                    break;
                case '-':
                    memory[memoryPointer]--;
                    break;
                case '.':
                    out.write(memory[memoryPointer]);
                    break;
                case ',':
                    try {
                        memory[memoryPointer] = (byte) in.read();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case '[':
                    if(memory[memoryPointer] == 0){
                        i = loopMap.get(i);
                        i--;                    }
                    break;
                case ']':
                    if(memory[memoryPointer] != 0){
                        i = loopMap.get(i);
                        i--;
                    }
            }
        }
    }
}
