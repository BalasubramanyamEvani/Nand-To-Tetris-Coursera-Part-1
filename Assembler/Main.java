/* @Author: Balasubramanyam Evani
* Manipal University Jaipur
* Assembler Program Written in JAVA
*/ 

package com.company.java;

import javax.rmi.CORBA.Util;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static class Utilities {
        public String omitSpaces(String line) {
            String result = "";
            if(line.length() != 0){
                for (String broken : line.split(" "))
                    result += broken;
            }
            return result;
        }

        public String omitComments(String line) {
            int index = line.indexOf("//");
            if(index != -1)
                 line = line.substring(0, index);
            return line;
        }

        public boolean isASM(String file) {
            int index = file.indexOf(".");
            //System.out.println(index);
            String ext = file.substring(index+1, file.length());
            if (ext.toLowerCase().equals("asm"))
                return true;
            else
                return false;
        }
        public String addZero(String str , int count){
            for(int i=str.length();i<count;i++)
                str = "0"+str;
            return str;
        }
    }
    private static HashMap<String, Integer> SymbolsTable;
    private static HashMap<String, String> Possible_comp1 = new HashMap<String, String>();
    private static HashMap<String, String> Possible_dest = new HashMap<String, String>();
    private static HashMap<String, String> Possible_comp2 = new HashMap<String, String>();
    private static HashMap<String, String> Possible_jump = new HashMap<String, String>();

    static {
        Possible_comp1.put("0", "101010");
        Possible_comp1.put("1", "111111");
        Possible_comp1.put("-1", "111010");
        Possible_comp1.put("D", "001100");
        Possible_comp1.put("A", "110000");
        Possible_comp1.put("!D", "001101");
        Possible_comp1.put("!A", "110001");
        Possible_comp1.put("-D", "001111");
        Possible_comp1.put("-A", "110011");
        Possible_comp1.put("D+1", "011111");
        Possible_comp1.put("A+1", "110111");
        Possible_comp1.put("D-1", "001110");
        Possible_comp1.put("A-1", "110010");
        Possible_comp1.put("D+A", "000010");
        Possible_comp1.put("D-A", "010011");
        Possible_comp1.put("A-D", "000111");
        Possible_comp1.put("D&A", "000000");
        Possible_comp1.put("D|A", "010101");
    }
    static {
        Possible_comp2.put("M","110000");Possible_comp2.put("!M","110001");Possible_comp2.put("-M","110011");
        Possible_comp2.put("M+1","110111");Possible_comp2.put("M-1","110010");Possible_comp2.put("D+M","000010");
        Possible_comp2.put("D-M","010011");Possible_comp2.put("M-D","000111");Possible_comp2.put("D&M","000000");
        Possible_comp2.put("D|M","010101");
    }
    static{
        Possible_dest.put("","000");
        Possible_dest.put("M","001");
        Possible_dest.put("D","010");
        Possible_dest.put("MD","011");
        Possible_dest.put("A","100");
        Possible_dest.put("AM","101");
        Possible_dest.put("AD","110");
        Possible_dest.put("AMD","111");

        Possible_jump.put("","000");
        Possible_jump.put("JGT","001");
        Possible_jump.put("JEQ","010");
        Possible_jump.put("JGE","011");
        Possible_jump.put("JLT","100");
        Possible_jump.put("JNE","101");
        Possible_jump.put("JLE","110");
        Possible_jump.put("JMP","111");
    }
    static Utilities util = new Utilities();
    static HashMap<String,Integer> findlabels(String command){
        HashMap<String,Integer> h = new HashMap<>();
        Scanner sc = new Scanner(command);
        int pc = 0;
        while(sc.hasNextLine()){
            String temp = sc.nextLine();
            char start = temp.charAt(0);
            char last = temp.charAt(temp.length()-1);
            if(start == '(' && last == ')' )
                h.put(temp.substring(1,temp.length()-1),pc);
            else
                pc++;
        }
        return h;
    }
    static String convertASMtoHACK(String command) throws IllegalAccessException {
        HashMap<String, Integer> labels = findlabels(command);
        HashMap<String, Integer> variables = new HashMap<>();
        Scanner sc = new Scanner(command);
        int pc = 0;
        String temp = "";
        String instruction = "";
        int start = 16;
        while (sc.hasNextLine()) {
            String st = sc.nextLine();
            //System.out.println(st);
            if (st.charAt(0) == '@') {
                //System.out.println("A");
                temp = st.substring(1);
                if (temp.matches("[0-9]+"))
                    temp = util.addZero(Integer.toBinaryString(Integer.parseInt(temp)), 15);
                else if (SymbolsTable.containsKey(temp))
                    temp = util.addZero(Integer.toBinaryString(SymbolsTable.get(temp)), 15);
                else if(labels.containsKey(temp))
                    temp = util.addZero(Integer.toBinaryString(labels.get(temp)),15);
                else {
                    if (variables.containsKey(temp))
                        temp = util.addZero(Integer.toBinaryString(variables.get(temp)), 15);
                    else {
                        int address = variables.size() + start;
                        if (address > 16384)
                            throw new IllegalAccessException("Illegal Memory Access!");
                        variables.put(temp, address);
                        temp = util.addZero(Integer.toBinaryString(variables.get(temp)),15);
                    }
                }
                instruction += "0" + temp + "\n";
            }
            pc++;
            if (st.charAt(0) == '(') {
                continue;
            } else if(st.charAt(0)!='@'){
                //System.out.println("C");
                int index1 = st.indexOf('=');
                int index2 = st.indexOf(';');
               // System.out.println(index1+" "+index2);
                String comp = "";
                String jmp = "";
                String dest = "";
                if (index1 != -1 && index2 != -1) {
                    dest = st.substring(0, index1);
                    comp = st.substring(index1 + 1, index2);
                    jmp = st.substring(index2 + 1);
                }
                else if (index1 != -1 && index2 == -1) {
                    dest = st.substring(0, index1);
                    comp = st.substring(index1 + 1);
                    //System.out.println(comp+" "+dest);
                }
                else if (index1 == -1 && index2 != -1) {
                    comp = st.substring(0, index2);
                    jmp = st.substring(index2 + 1);
                } else
                    comp = st;
                String a = "";
                String compu = "";
                //System.out.println(comp+" "+dest);
                if (Possible_comp1.containsKey(comp) && Possible_dest.containsKey(dest) || Possible_comp2.containsKey(comp) && Possible_jump.containsKey(jmp)) {
                    if (Possible_comp1.containsKey(comp)) {
                        a = "0";
                        compu = Possible_comp1.get(comp);
                    }
                    if (Possible_comp2.containsKey(comp)) {
                        a = "1";
                        compu = Possible_comp2.get(comp);
                    }
                    instruction += "111" + a + compu + Possible_dest.get(dest) + Possible_jump.get(jmp) + "\n";
                }
            }
            //System.out.println(instruction);
        }
        return instruction;
    }
       static void MyParser(String File) throws IOException {
            if(!util.isASM(File))
                throw new IllegalArgumentException("Provided file is not of .asm extension ! Please Provide .asm file");
            SymbolsTable = new HashMap<String,Integer>();
            initialize(SymbolsTable);
            try {
                Compute(File);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    public static void Compute(String file) throws IOException, IllegalAccessException {
        File f = new File(file);
        System.out.println(f);
        Scanner sc = new Scanner(f);
        String separator = "";
        while(sc.hasNextLine())
        {
               String command = sc.nextLine();
               //System.out.println(command);
               String cmd = util.omitSpaces(util.omitComments(command));
               if(!(cmd.length() == 0)) {
                   separator += cmd+"\n";
               }

               //System.out.println(separator);
        }
        separator.trim();
        //System.out.println(separator);
        String result = convertASMtoHACK(separator);
        String filename = f.getName().substring(0,f.getName().indexOf("."));
        PrintWriter p = new PrintWriter(new File(f.getParentFile().getAbsolutePath() + "/" + filename + ".hack"));
        p.print(result);
        p.close();
    }
    static void initialize(HashMap<String,Integer> hm){
        hm.put("R0",0);
        hm.put("R1",1);
        hm.put("R2",2);
        hm.put("R3",3);
        hm.put("R4",4);
        hm.put("R5",5);
        hm.put("R6",6);
        hm.put("R7",7);
        hm.put("R8",8);
        hm.put("R9",9);
        hm.put("R10",10);
        hm.put("R11",11);
        hm.put("R12",12);
        hm.put("R13",13);
        hm.put("R14",14);
        hm.put("R15",15);
        hm.put("SCREEN",16384);
        hm.put("KBD",24576);
        hm.put("SP",0);
        hm.put("LCL",1);
        hm.put("ARG",2);
        hm.put("THIS",3);
        hm.put("THAT",4);
    }
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        String filePath = sc.next();
        MyParser(filePath);
    }
}
