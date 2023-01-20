import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyntaxAnalyzer {
	public static boolean isEmpty = true;
	static String badSymbol = "";
	String file;
	static String line;
	static int lineNumber;
	static int position = 1;
	static String curr = "";
	static Lexeme currLex;
	static boolean done = false, correct = false;
	boolean nonsense = true;
	public static String[] keyWords = new String[] { "program", "print", "and", "or", "bool", "int", "if", "then", "fi",
			"while", "do", "od", "false", "true", "else", "not" };
	static List<String> kw = new ArrayList<>(Arrays.asList(keyWords));
	static ArrayList<Lexeme> symbolTable = new ArrayList<>();
	private static BufferedReader fis = null;

	// main method
	public static void main(String[] args) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			String pathway;
			System.out.println("Enter pathway to File: ");
			pathway = input.readLine();
			File file = new File(pathway);
			FileReader fr = new FileReader(file);
			fis = new BufferedReader(fr);
			nextLine();

			next();
			while (kind() != "end-of-text") {
				// print(position(), kind(), value());
				program();
				System.out.println("Syntax Analysis Finished Without Error");
			}

		} catch (Exception e) {
			
			if(isEmpty) {
				System.out.println("File is empty or file path is incorrect");
			} else {
				System.out.println(e.getMessage());
			}
		}

	}
	
	// Returns the next Lexeme
	public static void next() {
		if (position == line.length()) {
			nextLine();
			if (done) {
				currLex = new Lexeme("end-of-text", lineNumber, position, "");
				if(correct) {
					System.out.println("Syntax Analysis Finished Without Error");
					System.exit(0);
				}else {
					error();
				}
				
				symbolTable.add(currLex);
				// return currLex;
			}
		}

		char c = line.charAt(0);
		if (position == 0) {
			c = line.charAt(0);
		} else {
			c = line.charAt(position);
		}

		while ((c == ' ')) {
			position++;
			c = line.charAt(position);

		}

		if (c == ':') {
			if ((position + 1 < line.length()) && (line.charAt(position + 1) == '=')) {
				currLex = new Lexeme(":=", lineNumber, position, "");
				position++;
				position++;
			} else {
				currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
				position++;
			}
		} else if (c == '<') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;
		} else if (c == '=') {
			if ((position + 1 < line.length()) && (line.charAt(position + 1) == '<')) {
				currLex = new Lexeme("=<", lineNumber, position, "");
				position++;
				position++;

			} else {
				currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
				position++;
			}
		} else if (c == '!') {
			if ((position + 1 < line.length()) && (line.charAt(position + 1) == '=')) {
				currLex = new Lexeme("!=", lineNumber, position, "");
				position++;
				position++;

			} else {
				System.out.println((lineNumber) + " : " + (position + 1) + ">>>>> Illegal character '" + c + "'");
				System.exit(0);
			}

		} else if (c == '>') {
			if ((position + 1 < line.length()) && (line.charAt(position + 1) == '=')) {
				currLex = new Lexeme(">=", lineNumber, position, "");
				position++;
				position++;

			} else {
				currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
				position++;
			}

		} else if (c == ';') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;

		}

		else if (c == '*') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;

		} else if (c == '/') {
			if ((position + 1 < line.length()) && (line.charAt(position + 1) == '/')) {
				nextLine();
				if (done) {
					currLex = new Lexeme("end-of-text", lineNumber, position, "");
					return;
				}
				next();

			} else {
				currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
				position++;
			}
		} else if (c == '+') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;
		} else if (c == '-') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;
		} else if (c == '(') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;
		} else if (c == ')') {
			currLex = new Lexeme(Character.toString(c), lineNumber, position, "");
			position++;
		} else if (c >= '0' && c <= '9') {
			int temp = position;
			while (c >= '0' && c <= '9') {
				curr = curr + c;
				position++;
				if (position < line.length())
					c = line.charAt(position);
				else
					break;
				currLex = new Lexeme("NUM", lineNumber, temp, curr);

			}
			currLex = new Lexeme("NUM", lineNumber, temp, curr);
		} else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
			int temp = position;
			while ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_') || (c >= '0' && c <= '9')) {
				curr = curr + c;
				position++;
				if (position < line.length())
					c = line.charAt(position);
				else
					break;
			}

			if (kw.contains(curr)) {
				currLex = new Lexeme(curr, lineNumber, temp, "");
			} else if (curr.equals("end")) {
				currLex = new Lexeme(curr, lineNumber, temp, "");
			} else {
				currLex = new Lexeme("ID", lineNumber, temp, curr);
			}

		} else {
			if(c == ' ') {
				nextLine();
			}else {
				System.out.println((lineNumber) + " : " + (position + 1) + ">>>>> Illegal character '" + c + "'");
				System.exit(0);
			}
			

		}
		curr = "";
		symbolTable.add(currLex);
		// return currLex;
	}

	// Allows the next() function to move to the next line of the file
	public static void nextLine() {
		try {
			line = fis.readLine();
			line = line.trim();
			if (line.isEmpty()) {
				nextLine();
			}
			isEmpty = false;
			lineNumber++;
			position = 0;
			
		} catch (Exception e) {
			done = true;
			return;
		} // end of catch
	}

	// Prints the position, kind, and value of a Lexeme
	public static void print(String p, String k, String v) {

		System.out.println(p + " : '" + k + "'" + "  " + v);

	}

	// returns the kind of lexeme
	public static String kind() {
		return currLex.kind;
	}

	// returns the value of a lexeme
	public static String value() {
		return currLex.value;
	}

	// returns the position of a lexeme
	public static String position() {

		return (currLex.lineNumber) + " : " + (currLex.position + 1);
	}

	//Parser Code

	//checks program for correct syntax
	public static void program() {
		match("program");
		match("ID");
		match(":");
		body();
		match("end");
	}// end of Program
	
	//checks if current symbol is in the correct spot
	public static void match(String symbol) {
		if (kind().equals(symbol)) {
			if(symbol.equals("end"))
				correct = true;
			next();
		} else {
			badSymbol = "'"+symbol+"'";
			error();
		}
	}
	
	//checks body for correct syntax
	public static void body() {
		if (kind().equals("bool") || kind().equals("int"))
			declarations();
		statements();
	}
	
	//checks declarations for correct syntax
	public static void declarations() {
		if (kind().equals("bool") || kind().equals("int")) {
			next();
			declaration();
			while (kind().equals("bool") || kind().equals("int")) {
				next();
				declaration();
			}
		} else {
			badSymbol = "'bool' or 'int'";
			error();
		}
	}
	
	//checks a declaration for correct syntax
	public static void declaration() {
		match("ID");
		match(";");
	}

	//checks statements for correct syntax
	public static void statements() {
		statement();
		while (kind().equals(";")) {
			next();
			statement();
		}
	}
	
	//checks a statement for correct syntax
	public static void statement() {
		if (kind().equals("ID")) {
			assignmentStatement();
		} else if (kind().equals("if")) {
			conditionalStatement();
		} else if (kind().equals("while")) {
			iterativeStatement();
		} else if (kind().equals("print")) {
			printStatement();
		} else {
			badSymbol = "one of 'ID', 'if', 'while' or 'print'";
			error();
		}
	}
	
	//checks an assignment statement for correct syntax
	public static void assignmentStatement() {
		badSymbol = "'ID'";
		match("ID");
		badSymbol = "':='";
		match(":=");
		expression();
	}

	//checks a conditional statement for correct syntax
	public static void conditionalStatement() {
		match("if");
		expression();
		match("then");
		body();
		if (kind().equals("else")) {
			next();
			body();
		}
		match("fi");
	}
	
	//checks an iterative statement for correct syntax
	public static void iterativeStatement() {
		match("while");
		expression();
		match("do");
		body();
		match("od");
	}
	
	//checks a print statement for correct syntax
	public static void printStatement() {
		match("print");
		expression();
	}
	
	//checks an expression for correct syntax
	public static void expression() {
		simpleExpression();
		if (kind().equals("<") || kind().equals("=<") || kind().equals("=") || kind().equals("!=")
				|| kind().equals(">=") || kind().equals(">")) {
			next();
			simpleExpression();
		}

	}
	
	//checks a simple expression for correct syntax
	public static void simpleExpression() {
		term();
		while (kind().equals("+") || kind().equals("-") || kind().equals("or")) {
			next();
			term();
		}
	}
	
	//checks term for correct syntax
	public static void term() {
		factor();
		while (kind().equals("*") || kind().equals("/") || kind().equals("and")) {
			next();
			factor();
		}
	}
	
	//checks factor for correct syntax
	public static void factor() {
		if (kind().equals("-") || kind().equals("not"))
			next();

		if (kind().equals("ID")) {
			match("ID");
		} else if (kind().equals("(")) {
			match("(");
			expression();
			match(")");
		} else {
			literal();
		}

	}
	
	//checks literal for correct syntax
	public static void literal() {
		if (kind().equals("true") || kind().equals("false") || kind().equals("NUM")) {
			next();
		} else {
			badSymbol = "one of 'true, 'false', or 'NUM'";
			error();
		}

	}
	
	//prints error message and terminates program
	public static void error() {
		System.out.println("Syntax error at " + position() + " >>>>> Bad Symbol '" + kind() + "': expected " + badSymbol);
		System.exit(0);
	}

}

//This is an object used to represent a symbol
class Lexeme {
	String kind;
	int lineNumber;
	int position;
	String value;

	Lexeme(String k, int ln, int pos, String val) {
		this.kind = k;
		lineNumber = ln;
		position = pos;
		value = val;
	}
}