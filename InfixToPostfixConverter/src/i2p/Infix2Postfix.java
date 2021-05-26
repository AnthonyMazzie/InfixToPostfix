package i2p;

/**
 * @author AMazzie
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

public class Infix2Postfix {

	public static ArrayList<String> infixList;
	public static ArrayList<String> postfixList;

	/**
	 * Reads a text file ("input.txt") of infix expression strings
	 * 
	 * Stores infix expression strings to list
	 * 
	 * Loops list, converting infix to postfix
	 * 
	 * Print postfix expressions to file ("output.txt")
	 * 
	 * If expression unable to be converted, appropriate error message printed
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		/**
		 * Step 1
		 * 
		 * Read all infix expressions from file "input.txt"
		 */
		scanInfixFile();

		/**
		 * Step 2
		 * 
		 * Loop through list of infix strings and convert to postfix
		 */
		loopConversion();

		/**
		 * Step 3
		 * 
		 * Write list of postfix strings to new file "output.txt"
		 */
		writePostfixFile();
	}

	/**
	 * Checks precedence of operator.
	 * 
	 * @param char evaluating
	 * @return precedence of char evaluting
	 */
	static int operatorPrecedence(char c) {
		switch (c) {
		case '+':
		case '-':
			return 1;
		case '*':
		case '/':
		case '%':
			return 2;
		case '^':
			return 3;
		}
		return -1;
	}

	public static boolean checkParenthesis(String text) {
		PureStack<Character> s = new ArrayBasedStack<Character>();

		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (c == '(') {
				s.push(c);
			} else if (c == ')') {
				try {
					char left = s.pop();
					if (left == '(' && c != ')') {
						return false;
					}
				} catch (NoSuchElementException e) {
					return false;
				}
			}
		}
		return s.isEmpty();
	}

	public static void loopConversion() throws Exception {

		String[] listOfStrings = infixList.toArray(new String[0]);

		for (int i = 0; i < infixList.size(); i++) {
			listOfStrings[i] = infixToPostFix(listOfStrings[i]);
		}
		postfixList = new ArrayList<String>();

		for (int i = 0; i < listOfStrings.length; i++) {
			postfixList.add(listOfStrings[i]);
		}
		System.out.println(infixList.toString());
	}

	public static void writePostfixFile() throws FileNotFoundException {
		try {
			FileWriter writer = new FileWriter("output.txt");
			for (String str : postfixList) {
				writer.write(str + System.lineSeparator());
			}
			writer.close();

			System.out.println("\nSuccessfully wrote to the file.");

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static boolean emptySubExpressionDet(String infixString) {

		removeAllSpaces(infixString);

		int intIndex = infixString.indexOf("()");

		if (intIndex != -1) {
			return false;
		} else {
			return true;
		}
	}

	public static void scanInfixFile() throws FileNotFoundException {

		File file = new File("input.txt");
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("File input.txt does not exist.");
		}

		infixList = new ArrayList<String>();

		while (fileScanner.hasNext()) {
			infixList.add(fileScanner.nextLine());
		}

		fileScanner.close();
	}

	public static boolean checkOperators(String infixExpression) {

		PureStack<Character> s = new ArrayBasedStack<Character>();

		for (int i = 0; i < infixExpression.length(); i++) {
			char currentChar = infixExpression.charAt(i);

			if (currentChar == '(') {
				s.push(currentChar);
			} else if (currentChar == ')') {
				try {
					char left = s.pop();
					if (left == '(' && currentChar != ')') {
						return false;
					}
				} catch (NoSuchElementException e) {
					return false;
				}
			}
		}
		return false;
	}

	public static boolean checkTooManyDigits(String infixString) {

		boolean isDigit = false;

		for (int i = 0; i < infixString.length(); i++) {

			if (Character.isDigit(infixString.charAt(i))) {

				if (isDigit == true) {
					return true;
				}
				isDigit = true;
			}

			else if (infixString.charAt(i) != ' ') {
				isDigit = false;
			}
		}
		return false;
	}

	private static String infixToPostFix(String infixString) throws Exception {

		// Create function that returns boolean false if have two numbers

		String result = "";
		boolean prevOperator = false;

		if (!checkParenthesis(infixString)) {
			result = "Error: parenthesis mismatched";
			return result;
		}

		// Empty parenthesis found
		if (emptySubExpressionDet(infixString) == false) {
			result = "Error: no subexpression detected ()";
			return result;
		}

		if (checkTooManyDigits(infixString)) {
			// result = "Error: too many operands.";
			// return result;
		}

		Stack<Character> operatorStack = new Stack<>();

		int rank = 0; // Rank starts at 0

		for (int i = 0; i < infixString.length(); i++) {

			char currentValue = infixString.charAt(i);

			if (isOperator(infixString.charAt(i))) {
				if (infixString.charAt(i) == '-') {
					if (i == 0) {
						currentValue = '~';
					} else if ((i > 0 && infixString.charAt(i - 1) != ')' && isOperator(infixString.charAt(i - 1))
							|| Character.isDigit(infixString.charAt(i + 1)))) {
						currentValue = '~';
					}
				}
			}

			if (rank > 1) {
				result = "Error: too many operands (" + infixString.charAt(i - 1) + ")";
				return result;
			}

			// Check if char is an OPERATOR
			if (operatorPrecedence(currentValue) > 0) {

				rank = rank - 1;

				// Char is operator

				// If encounter two operators in a row, too many operators in input expression
				if (prevOperator == true && findNegativeNumber(currentValue, infixString.charAt(i + 1)) == false) {
					result = "Error: too many operators (" + infixString.charAt(i) + ")";
					return result;
				}

				/**
				 * While stack is not empty, if operator at top of stack has >= operator
				 * precedence than current char evaluating, add operator from top of stack to
				 * result.
				 */
				while (operatorStack.isEmpty() == false
						&& operatorPrecedence(operatorStack.peek()) >= operatorPrecedence(currentValue)
						&& findNegativeNumber(currentValue, infixString.charAt(i + 1)) == false) {
					result += operatorStack.pop();

				}

				/*
				 * Since an operator was just evaluated, set prevOperator to true to detect too
				 * many operands.
				 */
				prevOperator = true;

				if (currentValue == '-') {

					if (Character.isDigit(infixString.charAt(i + 1))) {
						result += currentValue;
					} else {
						/*
						 * Push current char evaluating to top of stack
						 */
						operatorStack.push(currentValue);
					}
				} else {
					operatorStack.push(currentValue);
				}

				/**
				 * Char evaluating is not an operator, check if parenthesis
				 */
			} else if (currentValue == ')') {
				prevOperator = false;

				char newChar = operatorStack.pop();

				while (newChar != '(') {
					result = result + newChar;
					newChar = operatorStack.pop();
				}
			} else if (currentValue == '(') {
				prevOperator = false;
				operatorStack.push(currentValue);
			} else {
				if (currentValue != ' ' && currentValue != '~') {
					prevOperator = false;

					/*
					 * If and only if next value is not an operand, increase rank
					 */
					if (i < infixString.length() - 1 && Character.isDigit(infixString.charAt(i + 1)) == false) {
						rank += 1;
					}
				}

				/*
				 * Current value is an operand, add to result
				 */
				result += currentValue;
			}
		}
		for (int i = 0; i <= operatorStack.size(); i++) {
			result += " " + operatorStack.pop();
		}
		result = removeExtraSpaces(result);
		return result;
	}

	protected static String removeExtraSpaces(String inputString) {
		String stringNoExtra = inputString;
		stringNoExtra = stringNoExtra.replace('\t', ' ');
		stringNoExtra = stringNoExtra.replaceAll("\\s+", " ");
		String noExtra = stringNoExtra.trim();
		return noExtra;
	}

	protected static boolean findNegativeNumber(char one, char two) {
		return Character.isDigit(two) && one == '-';
	}

	protected static String removeAllSpaces(String inputString) {
		String stringNoSpaces = inputString;
		for (int i = 0; i < stringNoSpaces.length(); i++) {
			if (stringNoSpaces.charAt(i) == ' ' || stringNoSpaces.charAt(i) == '	') {
				if (i < stringNoSpaces.length() - 1) {
					stringNoSpaces = stringNoSpaces.substring(0, i) + stringNoSpaces.substring(i + 1);
				} else
					stringNoSpaces = stringNoSpaces.substring(0, i);
				i--;
			}
		}
		return stringNoSpaces;
	}

	protected static boolean isOperator(char c) {
		switch (c) {
		case '~':
		case '+':
		case '-':
		case '*':
		case '/':
		case '%':
		case '^':
		case '(':
		case ')':
			return true;
		default:
			return false;
		}
	}
}