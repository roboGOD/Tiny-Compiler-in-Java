/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Compiler;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author joker
 */
/*
    #######################
    #######################
    #### Lexical Rules ####
    ####   for TINY    ####
    #######################
    #######################
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




// Creating an enum to assign the Type of the Token 
enum Type {
    WRITEtk, READtk, IFtk, ELSEtk, RETURNtk, BEGINtk, ENDtk, MAINtk, // Keywords 
    STRINGtk, INTtk, REALtk, // Datatypes... They are practically Keywords
    IDENTIFIER,
    STRING_LITERAL,
    NUMBER,
    SCOLON, LPAREN, RPAREN, COMMA, // Delimiters
    IS_EQUALtk, NOT_EQUALtk, ASSIGNtk, ADDtk, SUBtk, MULTIPLYtk, DIVIDEtk, // Operators
    UNDEF, // The Token that is not recognized is assigned UNDEF
    EOT // Indicates the Last Token (End of Tokens)
}

// Class for Token Objects. Any Properties of Tokens can be added to this class
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

// Main Class. All the work is done here.
public class Lexer {
    String filePath;
    int lineNum=1;
    // Initializing Lists. Any addition can be done here.
    String keywords[] = { "WRITE","READ","IF","ELSE","RETURN","BEGIN","END","MAIN","STRING","INT","REAL"};
    String multiCharOps[] = { ":=","==","!="};
    char delimiter[] = {';',',','(',')'};
    char operators[] = {'+','-','*','/'};
    ArrayList<String> words = new ArrayList();
    
    // Constructor for Lexer Class Object. Use is illustrated in Home Class.
    public Lexer(String filePath){
        this.filePath = filePath;
    }
    
    // Checks whether character is an Operator or not
    boolean isOperator(char c){
        for(char i:operators)
            if(c==i)
                return true;
        return false;
    }
    
    // Checks whether character is a Multi Character Operator or not
    boolean isMultiCharOp(char c, int i){
        for(String s:multiCharOps)
            if(s.charAt(0)==c && i==0)
                return true;
            else if(s.charAt(1)==c && i==1)
                return true;
        return false;
    }
    
    // Checks whether character is a Delimiter or not
    boolean isDelimiter(char c){
        for(char i:delimiter)
            if(c==i)
                return true;
        return false;
    }
    
    // Checks whether character is a Keyword or not
    boolean isKeyword(String s){
        for(String d:keywords)
            if(d.equals(s))
                return true;
        return false;
    }
    
    // Tokenization function. Performs the major task.
    public ArrayList<Token> generateTokens(){
        ArrayList<Token> Tokens;
        Tokens = new ArrayList();
        int tid = 0; // Assigns an Unique Id to Tokens
        int i; // Just an Integer to check the End of File
        char c; // This little dude goes through a lot. Thanks 'c', I really appreciate that.
        try {
        // RandomAccessFile allows seeking the pointer while reading files
        // FileReader Class does not support seeking
        RandomAccessFile fr = new RandomAccessFile(filePath,"r");
        long length = fr.length(); // Length of File in terms of characters
        
        // In the loop you may encounter continue statements (a lot of them). They are just
        // there to tell that the processing of current character is done and go check the next character
        
        while(fr.getFilePointer()<length-1) { // getFilePointer() returns the current position of pointer
            c = (char)fr.read();            
            // To check whether It's a comment or not
            // Comments start with /** in TINY
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
                                    else if(c == '\n') // For Multiline Comment lineNum should also change
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
                    else{ // If the condition is false i.e. It's not a comment
                        fr.seek(fr.getFilePointer()-3); // Reset the File Pointer
                        continue;
                    }
                }
                else {
                    fr.seek(fr.getFilePointer()-2);
                    continue;
                }
            }
            
            // Increment lineNum at '\n' character
            if(c == '\n') {
		lineNum++;
                continue;
            }
            
            // If It's an operator add the corresponding type and generate Token
            if(isOperator(c)) {
                Type tempType = getOpType(c); // Function Defined At Bottom of Code
                Tokens.add(new Token(++tid, lineNum, Character.toString(c), tempType));
                continue;
            }
            
            // If It's a multi character operator add the corresponding type and generate Token
            if(isMultiCharOp(c,0)){
                String temp = Character.toString(c);
                c = (char)fr.read();
                if(isMultiCharOp(c, 1)){
                    temp = temp + Character.toString(c);
                    Type tempType = getMOpType(temp); // Function Defined At Bottom
                    Tokens.add(new Token(++tid, lineNum, temp, tempType));
                }
                else {
                    //fr.seek(fr.getFilePointer()-2);
                    JOptionPane.showMessageDialog(null, "Error!\nOperators is fucked! at line " + Integer.toString(lineNum), "Error!", JOptionPane.ERROR_MESSAGE);
                    //continue;
                }
            }
            
            // If It's Delimiter add the corresponding type and generate Token
            if(isDelimiter(c)){
                Type tempType = getDelType(c);
                Tokens.add(new Token(++tid, lineNum, Character.toString(c), tempType));
                continue;
            }
            
            // Checks for STRING LITERALS and quotes
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
            
            // Checks for Identifiers and Keywords
            if(Character.isAlphabetic(c)){
                String temp = Character.toString(c);
                c = (char)fr.read();
                // Fetch all the alphanumeric characters
                while(Character.isAlphabetic(c) || Character.isDigit(c)) {
                    temp = temp + Character.toString(c);
                    c = (char)fr.read();
                }
                fr.seek(fr.getFilePointer()-1);
                // Is it a keyword?
                if(isKeyword(temp)) {
                    Type tempType = getKeyType(temp);
                    Tokens.add(new Token(++tid, lineNum, temp, tempType));
                }
                // If not, it must be an Identifier
                else 
                    Tokens.add(new Token(++tid, lineNum, temp, Type.IDENTIFIER));
                continue;
            }
            
            // Checks for Numeric Literals
            if(Character.isDigit(c)){
                String temp = Character.toString(c);
                c = (char)fr.read();
                while(Character.isDigit(c) || c == '.'){
                    temp = temp + Character.toString(c);
                    c = (char)fr.read();
                }
                fr.seek(fr.getFilePointer()-1);
                Tokens.add(new Token(++tid, lineNum, temp, Type.NUMBER));
                continue;
            }
            
            // Ignoring white spaces and tabs, everything else is Undefined
            if(c != ' ' && c != '\t')
                Tokens.add(new Token(++tid, lineNum, Character.toString(c), Type.UNDEF));
          }
        }
        catch(HeadlessException | IOException e) {
            JOptionPane.showMessageDialog(null, e,"Error!",JOptionPane.ERROR_MESSAGE);
        }
        Tokens.add(new Token(++tid, lineNum, null, Type.EOT));
        return Tokens;
    }
    
    // Check the Type of Operator
    Type getOpType(char c){
        Type t;
        switch (c) {
            case '+':
                t = Type.ADDtk;
                break;
            case '-':
                t = Type.SUBtk;
                break;
            case '*':
                t = Type.MULTIPLYtk;
                break;
            case '/':
                t = Type.DIVIDEtk;
                break;
            default:
                t = Type.UNDEF;
                break;
        }
        return t;
    }
    
    // Check the Type of MultiChar Operator
    Type getMOpType(String s){
        Type t;
        switch(s){
            case ":=":
                t = Type.ASSIGNtk;
                break;
            case "==":
                t = Type.IS_EQUALtk;
                break;
            case "!=":
                t = Type.NOT_EQUALtk;
                break;
            default:
                t = Type.UNDEF;
                break;
        }
        return t;
    }
    
    // Check the Type of Delimiter
    Type getDelType(char c){
        Type t;
        switch(c){
            case ';':
                t = Type.SCOLON;
                break;
            case '(':
                t = Type.LPAREN;
                break;
            case ')':
                t = Type.RPAREN;
                break;
            case ',':
                t = Type.COMMA;
                break;
            default:
                t = Type.UNDEF;
                break;
        }
        return t;
    }
    
    // Check the Keyword Type
    Type getKeyType(String s){
        Type t;
        switch(s){ 
            case "WRITE":
                t = Type.WRITEtk;
                break;
            case "READ":
                t = Type.READtk;
                break;
            case "IF":
                t = Type.IFtk;
                break;
            case "ELSE":
                t = Type.ELSEtk;
                break;
            case "RETURN":
                t = Type.RETURNtk;
                break;
            case "BEGIN":
                t = Type.BEGINtk;
                break;
            case "END":
                t = Type.ENDtk;
                break;
            case "MAIN":
                t = Type.MAINtk;
                break;
            case "STRING":
                t = Type.STRINGtk;
                break;
            case "INT":
                t = Type.INTtk;
                break;
            case "REAL":
                t = Type.REALtk;
                break;
            default:
                t = Type.UNDEF;
                break;
        }
        return t;
    }
}
