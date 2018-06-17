/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author joker
 */
//  This is the Lexical Analyzer for Tiny Language.
//  Definition of Tiny :: 

//  Keywords                       :  	WRITE READ IF ELSE RETURN BEGIN END MAIN STRING INT REAL
//  Single-character separators    :   	;  ,  (   )
//  Single-character operators     :   	+  -  *   /
//  Multi-character operators      :   	:=  ==   !=
//  Identifier: An identifier consists of a letter followed by any number of letters or digits. The following are examples of identifiers: x, x2, xx2, x2x, End, END2.Note that End is an identifier while END is a keyword. The following are not identifiers:

/*    IF, WRITE, READ, .... (keywords are not counted as identifiers)
      2x (identifier can not start with a digit)
      Strings in comments are not identifiers. */

//  Number is a sequence of digits, or a sequence of digits followed by a dot, and followed by digits.   

/*    Number -> Digits | Digits '.' Digits
      Digits -> Digit | Digit Digits
      Digit  -> '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' */

//  Comments: string between /** and **/. Comments can be longer than one line.

enum Type {
    KEYWORD,
    IDENTIFIER,
    STRING_LITERAL,
    NUMBER,
    DELIMITER,
    OPERATOR,
    UNDEF,
    EOT
}

class Token {
    final int tokenID;
    final int lineNum;
    final String instance;
    final Type tokenType;
    
    public Token(int tokenID,int lineNum,String instance,Type tokenType){
        this.tokenID = tokenID;
        this.instance = instance;
        this.tokenType = tokenType;
        this.lineNum = lineNum;
    }
}


public class Lexer {
    String filePath;
    String keywords[] = { "WRITE","READ","IF","ELSE","RETURN","BEGIN","END","MAIN","STRING","INT","REAL"};
    int lineNum=1;
    String multiCharOps[] = { ":=","==","!="};
    char delimiter[] = {';',',','(',')'};
    //char multiCharFirst[] = {':','=','!'};
    char operators[] = {'+','-','*','/'};
    ArrayList<String> words = new ArrayList();
    //boolean comment=true;
    
    public Lexer(String filePath){
        this.filePath = filePath;
    }
    
    boolean isOperator(char c){
        for(char i:operators)
            if(c==i)
                return true;
        return false;
    }
    
    boolean isMultiCharOp(char c, int i){
        for(String s:multiCharOps)
            if(s.charAt(0)==c && i==0)
                return true;
            else if(s.charAt(1)==c && i==1)
                return true;
        return false;
    }
    
    boolean isDelimiter(char c){
        for(char i:delimiter)
            if(c==i)
                return true;
        return false;
    }
    
    boolean isKeyword(String s){
        for(String d:keywords)
            if(d.equals(s))
                return true;
        return false;
    }
    
    public ArrayList<Token> generateTokens(){
        ArrayList<Token> Tokens;
        Tokens = new ArrayList();
        int tid = 0;
        //FileReader fr = new FileReader(this.filePath);
        int i;
        char c;
        try {
        RandomAccessFile fr = new RandomAccessFile(filePath,"r");
        long length = fr.length();
        while(fr.getFilePointer()<length-1) {
            c = (char)fr.read();            
            //To check whether It's a comment or not
            if(c == '/'){
                c = (char)fr.read();
                if(c == '*'){
                    c = (char)fr.read();
                    if(c == '*'){
                        //JOptionPane.showMessageDialog(null, "It's a comment!!","Good News",JOptionPane.INFORMATION_MESSAGE);
                        while((i=fr.read()) != -1){
                            if((char)i == '*'){
                                c = (char)fr.read();
                                if(c == '*'){
                                    c = (char)fr.read();
                                    if(c == '/'){
                                        //JOptionPane.showMessageDialog(null, "Commment Ended!!",Awesome!,JOptionPane.INFORMATION_MESSAGE);                             
                                        break;
                                    }    
                                    else if(c == '\n')
                                        lineNum++;
                                }
                                else if(c == '\n')
                                    lineNum++;
                            }
                            else if(c == '\n')
                                lineNum++;
                        }
                        continue;
                    }
                    else{
                        fr.seek(fr.getFilePointer()-3);
                        continue;
                    }
                }
                else {
                    fr.seek(fr.getFilePointer()-2);
                    continue;
                }
            }
            if(c == '\n') {
		lineNum++;
                continue;
            }
            
            if(isOperator(c)) {
                Tokens.add(new Token(++tid, lineNum, Character.toString(c), Type.OPERATOR));
                continue;
            }
            
            if(isMultiCharOp(c,0)){
                String temp = Character.toString(c);
                c = (char)fr.read();
                if(isMultiCharOp(c, 1)){
                    temp = temp + Character.toString(c);
                    Tokens.add(new Token(++tid, lineNum, temp, Type.OPERATOR));
                }
                else {
                    //fr.seek(fr.getFilePointer()-2);
                    JOptionPane.showMessageDialog(null, "Error!\nOperators is fucked! at line " + Integer.toString(lineNum), "Error!", JOptionPane.ERROR_MESSAGE);
                    //continue;
                }
            }
            
            if(isDelimiter(c)){
                Tokens.add(new Token(++tid, lineNum, Character.toString(c), Type.DELIMITER));
                continue;
            }
            
            if(c == '"'){
                String temp="";
                c = (char)fr.read();
                while(c != '"'){
                    temp = temp + Character.toString(c);
                    c = (char)fr.read();
                }
                Tokens.add(new Token(++tid, lineNum, temp, Type.STRING_LITERAL));
                continue;
            }
            
            if(Character.isAlphabetic(c)){
                String temp = Character.toString(c);
                c = (char)fr.read();
                while(Character.isAlphabetic(c) || Character.isDigit(c)) {
                    temp = temp + Character.toString(c);
                    c = (char)fr.read();
                }
                //System.out.println(temp);
                fr.seek(fr.getFilePointer()-1);
                if(isKeyword(temp))
                    Tokens.add(new Token(++tid, lineNum, temp, Type.KEYWORD));
                else 
                    Tokens.add(new Token(++tid, lineNum, temp, Type.IDENTIFIER));
                continue;
            }
            if(Character.isDigit(c)){
                String temp = Character.toString(c);
                c = (char)fr.read();
                while(Character.isDigit(c) || c == '.'){
                    temp = temp + Character.toString(c);
                    c = (char)fr.read();
                }
                //System.out.println(temp);
                fr.seek(fr.getFilePointer()-1);
                Tokens.add(new Token(++tid, lineNum, temp, Type.NUMBER));
                continue;
            }
            if(c != ' ' && c != '\t'){
                Tokens.add(new Token(++tid, lineNum, Character.toString(c), Type.UNDEF));
                continue;
            }
        }
        }
        catch(HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(null, e,"Error!",JOptionPane.ERROR_MESSAGE);
        }
        Tokens.add(new Token(++tid, lineNum, null, Type.EOT));
        return Tokens;
    }
}
