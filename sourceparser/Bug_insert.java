package sourceparser;

/**
 * 
 */

import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MethodCallExpr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import experiment.Foo;

/**
 * @author zhuofu
 * 
 */
public class Bug_insert {
	//static DataLogger logger = new DataLogger();
	static HashMap<Integer, List<SubExpression>> subExpMap = new HashMap<Integer, List<SubExpression>>();
	static SourceParser3 sourceParser = new SourceParser3();
	static Foo foo=new Foo();
	/**
	 * 
	 */
	public Bug_insert() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// Test a path that exists with only valid files

		String folder_base = "C:/Users/zhuofu/workspace2/apacheCommonMath3.1.1/";

		// FileInputStream in = new
		// FileInputStream("C:/Users/zhuofu/workspace/apacheCommonMath3.1.1/src/experiments/FastCosineTransformer_bug.java");
		String rootPath = folder_base
				+ "src/org/apache/commons/math3/geometry/euclidean/threed/";;// "src/experiments";
		String fileName = "Vector3D_bug1.java";// "FastCosineTransformer_bug2.java";
		//System.out.println("file exist");
		sourceParser.addFile(rootPath, fileName);

		/** start mutation testing */
		/** First select an operation */
		int operation = selectMutation();
		operation = 1;
		switch (operation) {

		case 1:
			addSmallNumToStmt();
			break;
		case 2:
			multiplySmallNumToSubExpr();
			break;
		case 3:
			switchOp();
			break;
//		case 4:
//			changeConstant();
			//break;

		}

		String filePath = rootPath + "/" + fileName;
		int lineNumber = selectLine();
		double n = Math.random();
		// System.out.println("lineNumber" + lineNumber + "n" + n);
		//
	}

	public static int selectLine() {
		Lines2 lines2 = sourceParser.getLines2();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int key : lines2.keySet()) {
			list.add(key);
			// System.out.println(key);
		}
		Collections.sort(list);
		// for (int i = 0; i < list.size(); i++) {
		// System.out.println(list.get(i));
		// }
		int m = list.size();
		System.out.println(m);
		int u = (int) (Math.random() * m);
		return list.get(u);
	}

	// random generate a type of mutation
	public static int selectMutation() {
		int u = (int) (Math.random() * 4) + 1;
		return u;
	}

	// mutation 1: add a small number to the end of a statement
	public static void addSmallNumToStmt() {
		double n = getSmallNumber();// genearte a small number
		int lineNumber = selectLine();
		System.out
				.println("mutation type: add a small number to the end of a statement");
		System.out.println("Add " + n + " To the End of Line: " + lineNumber);
	}

	// mutation 2: multiply a small number to the end of a binary expression
	public static void multiplySmallNumToSubExpr() {
		double n = getSmallNumber();
		BEcollection bc = sourceParser.getBEcollection(0);
		BinaryExpr be = selectBe(bc);
		int Line = be.getBeginLine();
		// choose left right u=1 left, u=0 right
		int u = (int) (Math.random() * 2);
		System.out
				.println("mutation type: multiply a small number to a sub-Expression of a statement");
		System.out.println("Line: " + Line);
		System.out.println("whole Sub-Expression: " + be.toString());
		System.out.print("multiply " + n + " to ");
		if (u == 1) {
			System.out.println(be.getLeft().toString());
		} else {
			System.out.println(be.getLeft().toString());
		}

	}

	// mutation 3: switch an operator of a statement
	public static void switchOp() {
		BEcollection bc = sourceParser.getBEcollection(0);
		BinaryExpr be = selectBe(bc);
		int Line = be.getBeginLine();
		String op = be.getOperator().name();
		System.out.println("mutation type: switch an operator of a statement");
		System.out.println("Line: " + Line);
		System.out.println("whole Sub-Expression: " + be.toString());
		System.out.println("switch the operator '" + op + "' between "
				+ be.getLeft().toString() + " and " + be.getRight().toString()
				+ " to its counterpart");
	}

	// mutation 4: change a constant value;
	public static void changeConstant() {
		BEcollection bc = sourceParser.getBEcollection(1);
		BinaryExpr be = selectBe(bc);
		int Line = be.getBeginLine();
		System.out.println("mutation type: change a constant value");
		System.out.println("Line: " + Line);
		System.out.println("whole Sub-Expression: " + be.toString());
		System.out.println("change the constant value");
		boolean isInteger = false;
		boolean isDouble = false;
		if (getExpressionName(be.getLeft().getClass().getName()).compareTo(
				"IntegerLiteralExpr") == 0
				|| getExpressionName(be.getRight().getClass().getName())
						.compareTo("IntegerLiteralExpr") == 0) {
			isInteger = true;
		}

		if (getExpressionName(be.getLeft().getClass().getName()).compareTo(
				"DoubleLiteralExpr") == 0
				|| getExpressionName(be.getRight().getClass().getName())
						.compareTo("DoubleLiteralExpr") == 0) {
			isDouble = true;
		}

		if (isInteger && isDouble) { // if the subexpression contains both
										// double constant and integer constant,
										// randomly choose one of them.
			int u = (int) (Math.random() * 2);
			if (u == 1) {
				isInteger = false;
			} else {
				isDouble = false;
			}
		}
		if (isInteger == true) {
			System.out
					.println("plus 1 to one of integer costants of the subExpression");
		}
		if (isDouble == true) {
			double n = getSmallNumber();
			System.out.println("plus " + n
					+ " to one of double constants of the subExpression");
		} else {
			System.out
					.println("the subexpression doesn't containt any constant variable");
		}
	}

	public static BinaryExpr selectBe(BEcollection bc) {
		BinaryExpr be = new BinaryExpr();
		//
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int key : bc.keySet()) {
			list.add(key);
			// System.out.println(key);
		}
		Collections.sort(list);

		int m = list.size();
		// System.out.println(m);
		int u = (int) (Math.random() * m);
		BExpressions bexprs = bc.get(list.get(u));

		m = bexprs.size();
		u = (int) (Math.random() * m);
		be = bexprs.get(u);
		return be;
	}

	public static double getSmallNumber() {
		return foo.createSmallNumber();
//		double n = Math.random() * 2 - 1;
//		return n;
	}

	private static String getExpressionName(String fullname) {

		String[] names = fullname.split("\\.");
		String name = names[names.length - 1];
		return name;
	}
	// public void visit(Expression expression, ArrayList list) {
	// if (getExpressionName(expression.getClass().getName()).compareTo(
	// "BinaryExpr") != 0) {
	// System.out.println("Warning, the expession is not binary");
	// }
	// BinaryExpr e = (BinaryExpr) expression;
	// if (getExpressionName(e.getLeft().getClass().getName()).compareTo(
	// "BinaryExpr") == 0) {
	// visit(e.getLeft(),list);
	// }
	// if (getExpressionName(e.getRight().getClass().getName())
	// .compareTo("BinaryExpr") == 0) {
	// visit(e.getRight(),list);
	// }
	// if ((e.getLeft().getClass().getName())
	// .compareTo("IntegerLiteralExpr") == 0
	// || (e.getLeft().getClass().getName())
	// .compareTo("DoubleLiteralExpr") == 0) {
	// if((e.getLeft().getClass().getName()).compareTo("IntegerLiteralExpr") ==
	// 0){
	// IntegerLiteralExpr inte=(IntegerLiteralExpr) e.getLeft();
	// }else{
	// DoubleLiteralExpr inte=(DoubleLiteralExpr) e.getLeft();
	// }
	//
	// }
	// if ((e.getRight().getClass().getName())
	// .compareTo("IntegerLiteralExpr") == 0
	// || (e.getRight().getClass().getName())
	// .compareTo("DoubleLiteralExpr") == 0) {
	// if((e.getLeft().getClass().getName()).compareTo("IntegerLiteralExpr") ==
	// 0){
	// IntegerLiteralExpr inte=(IntegerLiteralExpr) e.getLeft();
	// }else{
	// DoubleLiteralExpr inte=(DoubleLiteralExpr) e.getLeft();
	// }
	// }
	// }

}
