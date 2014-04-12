package sourceparser;

/**
 * 
 */

import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhuofu
 * 
 */
public class FileParser {
	static DataLogger logger = new DataLogger();
	static HashMap<Integer, List<SubExpression>> subExpMap = new HashMap<Integer, List<SubExpression>>();
	static SourceParser2 sourceParser = new SourceParser2();

	/**
	 * 
	 */
	public FileParser() {
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
		String logging_dir = "TestParser/Data7";
		logger.setDir(logging_dir);// set the logging directory

		// FileInputStream in = new
		// FileInputStream("C:/Users/zhuofu/workspace/apacheCommonMath3.1.1/src/experiments/FastCosineTransformer_bug.java");
		String rootPath = folder_base
				+ "src/org/apache/commons/math3/geometry/euclidean/threed/";
		String fileName = "Rotation_bug7.java";//"BOBYQAOptimizer_bug.java";
		System.out.println("file exist");
		sourceParser.addFile(rootPath, fileName);
		HashMap<Integer, List<BasicExpression>> mMap = new HashMap<Integer, List<BasicExpression>>();
		for (int i = 0; i < 2000; i++) {

			String path = "out/executions/" + i + ".txt";
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(path));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(path + " does not exist");
				// e.printStackTrace();
				continue;
			}

			String scurrentLine;
			String[] terms;
			int lineNum = 0;
			int testId = 0;
			int currentLineNum = 0;
			String lineType = null;
			List<BasicExpression> mlineList = null;

			while ((scurrentLine = in.readLine()) != null) {
				System.out.println(scurrentLine);
				terms = scurrentLine.split("\t");
				lineNum = getLineNumber(terms[0]);
				lineType = getLineType(terms[1]);
				// System.out.println(lineNum);
			
				if (lineNum == 0 && currentLineNum == 0) {
					continue;
				} else if (lineType.compareTo("callEnter") == 0) {
					currentLineNum = lineNum;
					handleFunctionCall(in, currentLineNum, mMap);
				} else if (lineType.compareTo("funcExit") == 0) {
					System.out.println("This is the last line of file " + i);
					continue;
				} else if (lineType.compareTo("normal") == 0) {
					if (lineNum != currentLineNum) {
						if (mlineList != null) {

							mMap.put(currentLineNum, mlineList);
						}
						mlineList = new ArrayList<BasicExpression>();
						currentLineNum = lineNum;
					} else if (mlineList == null) {
						mlineList = new ArrayList<BasicExpression>();
						currentLineNum = lineNum;
					}
					BasicExpression mNode = new BasicExpression(terms);
					mlineList.add(mNode);
				} else if (lineType.compareTo("object") == 0) {
					continue;
				}

			}
			try{
			parse(fileName, mMap);}
			catch( java.lang.IndexOutOfBoundsException e){
				System.out.println("file "+i+" got problem");
			}

			System.out.println("finish parsing test case " + i);
			mMap.clear();
		}
	}

	public static void handleFunctionCall(BufferedReader in, int curNum,
			HashMap<Integer, List<BasicExpression>> mMap) throws IOException {
		int currentLineNum = curNum;
		int funcLineNum = curNum;
		String scurrentLine = null;
		ArrayList<String> list = new ArrayList<String>();
		List<BasicExpression> mlineList_func = null;
		List<BasicExpression> mlineList = null;
		BasicExpression mNode = new BasicExpression();

		while ((scurrentLine = in.readLine()) != null) {
			String[] terms = scurrentLine.split("\t");
			int lineNum = getLineNumber(terms[0]);
			String lineType = getLineType(terms[1]);
			if(terms.length >= 3)
			{if(terms[2].contains("1.201878699"))
				{System.out.println("stop");}}
			if (lineType.compareTo("callExit") == 0) {
				return;
			} else if (lineType.compareTo("funcEnter") == 0) {
				if (terms.length == 3) {
					mNode.setCovarList(terms[2]);
					mNode.setOp("FUNC");
				}

				// store the input parameter
			} else if (lineType.compareTo("callEnter") == 0) {
				// funcLineNum=lineNum;
				handleFunctionCall(in, lineNum, mMap);
			} else if (lineType.compareTo("funcExit") == 0) {

				if (terms.length == 3) {
					if (!terms[2].contains("NonPrimitiveType")
							&& !terms[2].contains("Void"))
						mNode.setTarget(terms[2]);
				}
				if (mNode.check() == true) {
					mlineList_func = new ArrayList<BasicExpression>();
					mlineList_func.add(mNode);
					mMap.put(-funcLineNum, mlineList_func);
				}
				// clear List
				for (int i = 0; i < list.size(); i++) {
					scurrentLine = list.get(i);
					terms = scurrentLine.split("\t");
					lineNum = getLineNumber(terms[0]);
					if(lineNum==246)
					{System.out.println("stop");}
					lineType = getLineType(terms[1]);
					if (lineNum != currentLineNum) {
						if (mlineList != null) {

							mMap.put(currentLineNum, mlineList);
						}
						mlineList = new ArrayList<BasicExpression>();
						currentLineNum = lineNum;
					} else if (mlineList == null) {
						mlineList = new ArrayList<BasicExpression>();
						currentLineNum = lineNum;
					}
					mNode = new BasicExpression(terms);
					mlineList.add(mNode);
				}
				list.clear();
				if (mlineList != null) {
					mMap.put(currentLineNum, mlineList);
					mlineList=null;
				}
			} else if (lineType.compareTo("normal") == 0) {
				list.add(scurrentLine);// add current binary expression line to
										// the function list
			} else if (lineType.compareTo("object") == 0) {
				continue;
			} else {
				System.out.println("WARNING! SPECIAL CASE FOUNDED!");
			}

			// System.out.println(scurrentLine);
		}
	}

	public static String getLineType(String term) {
		if (term.contains("CallSite-Enter")) {
			if (term.contains("java.lang.Object"))
				return "object";
			else
				return "callEnter";
		} else if (term.contains("CallSite-Exit")) {

			if (term.contains("java.lang.Object"))
				return "object";
			else
				return "callExit";
		} else if (term.contains("Function-Enter"))
			return "funcEnter";
		else if (term.contains("Function-Exit"))
			return "funcExit";
		else
			return "normal";
	}

	/**
	 * read the binary expression file, and record the data into a HashMap.
	 * */
	public void readFile(BufferedReader in,
			HashMap<Integer, List<BasicExpression>> mMap) throws IOException {

		String scurrentLine;
		String[] terms;
		int lineNum = 0;
		int testId = 0;
		int currentLineNum = 0;

		List<BasicExpression> mlineList = null;
		while ((scurrentLine = in.readLine()) != null) {
			// System.out.println(scurrentLine);
			terms = scurrentLine.split("\t");
			lineNum = getLineNumber(terms[0]);
			// System.out.println(lineNum);
			if (lineNum != currentLineNum) {
				if (mlineList != null) {

					mMap.put(currentLineNum, mlineList);
				}
				mlineList = new ArrayList<BasicExpression>();
				currentLineNum = lineNum;
			}
			BasicExpression mNode = new BasicExpression(terms);
			mlineList.add(mNode);

		}
	}

	/**
	 * @param fileName
	 *            the file need to be parsed by java parser
	 * @param mMap
	 *            A hash map stored BasicExpressions
	 * */
	public static void parse(String fileName,
			HashMap<Integer, List<BasicExpression>> mMap) {
		Expression mExpression;
		List<SubExpression> subExpListcopy;
		List<BasicExpression> mlineList;
		for (int key : mMap.keySet()) {
			if(key==246){
				System.out.println("787");
			}
			if (key < 0) {
				mlineList = mMap.get(key);
				subExpListcopy = createSubExpList(mlineList);
				subExpMap.put(key, subExpListcopy);
				continue;
			}
			System.out.println(key);
			mlineList = mMap.get(key);
			subExpListcopy = subExpMap.get(key);
			mExpression = sourceParser.getExpression(fileName, key);

			if (mExpression == null) {
				continue;
			}
			if (subExpListcopy == null) {
				List<SubExpression> subExpList = createSubExpList(mlineList);
				subExpListcopy = new ArrayList<SubExpression>();
				// make a deep copy of subExpList;
				for (int i = 0; i < subExpList.size(); i++) {
					subExpListcopy.add(subExpList.get(i).copy());
				}

				reComposeSubExpression(mExpression, null, subExpList,
						subExpListcopy);
				subExpMap.put(key, subExpListcopy);
			}
			System.out.println("the subexpression list is:");
			// logger.logToConsole(mlineList,subExpListcopy);
			logger.logToFile(mlineList.get(0).getTestId(), key, mlineList,
					subExpListcopy);
			System.out.println("done");
		}
	}

	public static void setDir(String dir) {
		logger.setDir(dir);
	}

	public static ArrayList<SubExpression> createSubExpList(
			List<BasicExpression> nodeList) {
		ArrayList<SubExpression> subExpList = new ArrayList<SubExpression>();
		for (int i = 0; i < nodeList.size(); i++) {
			SubExpression se = new SubExpression(i, nodeList.get(i));
			subExpList.add(se);
		}
		return subExpList;
	}

	public static int getLineNumber(String t) {
		System.out.println(t);
		String m = t.substring(1).split("_")[2];
		if (m.compareTo("X") == 0) {
			return 0;
		}
		int lineNumber = Integer.parseInt(m);
		return lineNumber;
	}

	/**
	 * Recompose the basic subexpressions into expected subexpressions
	 * 
	 * @param Expression
	 *            e contains information of the sub expression.
	 * @param op
	 *            the operation of this sub expression.
	 * @param list
	 *            the list of basic sub expression related to this
	 *            subexpression.
	 * @param
	 * @return The id of the subexpression need to be combined.
	 */
	public static int reComposeSubExpression(Expression e, String parentop,
			List<SubExpression> list, List<SubExpression> listcopy) {
		int thisID = -1;// return value, initial as id of Last list Element;
		if (list != null) {
			thisID = list.get(list.size() - 1).getId();// the id of this
														// subexpression
		} else {
			return thisID;
		}

		int id2 = -1;// the id of the subexpression which need to be combined
						// with this subexpression
		int id3 = -1;// the return value of traverse the right branch of java
						// AST.
		List<SubExpression> sublist1 = null, sublist2 = null;
		// preprocess the Expression e
		BinaryExpr be = null;
		String eName = getExpressionName(e.getClass().getName());
		if (eName.compareTo("EnclosedExpr") == 0) {
			EnclosedExpr ecle = (EnclosedExpr) e;
			if (getExpressionName(ecle.getInner().getClass().getName())
					.compareTo("BinaryExpr") == 0) {
				parentop = null;
				be = (BinaryExpr) ecle.getInner();
			}

		} else if (eName.compareTo("MethodCallExpr") == 0) {
			MethodCallExpr mce = (MethodCallExpr) e;
			if (mce.getArgs() == null) {
				return -1;
			}
			if (getExpressionName(mce.getArgs().get(0).getClass().getName())
					.compareTo("BinaryExpr") == 0) {
				parentop = null;
				be = (BinaryExpr) mce.getArgs().get(0);
			}
		} else if (eName.compareTo("BinaryExpr") == 0) {
			be = (BinaryExpr) e;
		} else if (eName.compareTo("ArrayAccessExpr") == 0) {
			ArrayAccessExpr aae = (ArrayAccessExpr) e;
			if (getExpressionName(aae.getIndex().getClass().getName())
					.compareTo("BinaryExpr") == 0) {
				parentop = null;
				be = (BinaryExpr) aae.getIndex();
			}
		}

		String op = null;
		if (be == null) {
			thisID = -1;// if this expression is not a binary expression, then
						// this sub-expression doesn't need combined with any
						// other sub-expression
			return thisID;
		} else if (list.size() == 1) {
			op = be.getOperator().name();
		} else if (list.size() > 1) {
			// if condition
			op = be.getOperator().name();
			int leftsize = decideIndex(list, be);
			// if (leftsize == -1) {
			// return -1;
			// } else
			if (list.size() > 1) {
				if (leftsize == 0) {
					if (list.size() - 1 - leftsize == 1) {
						sublist2 = new ArrayList<SubExpression>();
						sublist2.add(list.get(list.size() - 2));
					} else {
						sublist2 = list.subList(leftsize, list.size() - 1);
					}

				} else if (leftsize == list.size() - 1) {
					if (leftsize == 1) {
						sublist1 = new ArrayList<SubExpression>();
						sublist1.add(list.get(0));
					} else {
						sublist1 = list.subList(0, leftsize);
					}
				} else {
					if (leftsize == 1) {
						sublist1 = new ArrayList<SubExpression>();
						sublist1.add(list.get(0));
					} else {
						sublist1 = list.subList(0, leftsize);
					}
					if (list.size() - 1 - leftsize == 1) {
						sublist2 = new ArrayList<SubExpression>();
						sublist2.add(list.get(list.size() - 2));
					} else {
						sublist2 = list.subList(leftsize, list.size() - 1);
					}
				}
				id2 = reComposeSubExpression(be.getLeft(), op, sublist1,
						listcopy);// get
									// the
									// combined
									// id;
				id3 = reComposeSubExpression(be.getRight(), null, sublist2,
						listcopy);
			}
		}
		boolean flag = combineSubExpression(thisID, id2, listcopy);// combind
																	// the two
																	// subexpression;
		if (flag == false) {
			System.out.println("fail combination");// combination fail, throw
													// warning;(change to
													// execption later)
		}

		if (compareOP(parentop, op) != 0)// if the opration from outer layer and
											// the operation of this
											// subexpression has same priority,
											// then return this sub expression
											// as combination.
		{
			thisID = -1;
		}

		return thisID;
	}

	/**
	 * @param id1
	 *            id of first sub expression
	 * @param id2
	 *            id of second sub expression
	 * @param list
	 *            sub expression list.
	 * */
	public static boolean combineSubExpression(int id1, int id2,
			List<SubExpression> list) {
		if (id1 >= 0 && id2 >= 0) {
			// System.out.println(list.size());
			int index1 = searchSubExpList(list, id1);
			int index2 = searchSubExpList(list, id2);
			if (index1 < 0 || index2 < 0) {
				System.out
						.println("the combined id does not exist in the subexpression list");
			}
			list.get(index1).merge(list.get(index2));
			list.remove(index2);
			return true;
		} else if (id1 < 0 && id2 > 0) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * @param op1
	 *            the first operator
	 * @param op2
	 *            the second operator
	 * @return 0 equal Precedence, positive op1<op2, negative op1>op2
	 */
	public static int searchSubExpList(List<SubExpression> list, int id) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}

	public static int compareOP(String op1, String op2) {
		int a = getOpPrecedence(op1);
		int b = getOpPrecedence(op2);
		// if(a==0||b==0){
		// System.out.println("unknown operator "+op1+" "+op2);
		// }
		return a - b;//
	}

	/**
	 * 0 or and negative 1 minus, plus, ADD, 2 times, divide
	 */
	public static int getOpPrecedence(String op) {
		if (op == null){
			return 0;
		}else if(op.compareTo("unknown")==0) {
			System.out.println("operator "+op+" is null or unknown");
			return 0;
		}
		else {
			BinaryExpr.Operator operator = BinaryExpr.Operator.valueOf(op);
			int p = 0;
			switch (operator) {
			case or:
				p = 12;
				break;// ||
			case and:
				p = 11;
				break;// &&
			case binOr:
				p = 10;
				break;// |
			case binAnd:
				p = 8;
				break;// &
			case xor:
				p = 9;
				break;// ^
			case equals:
				p = 7;
				break;// ==
			case notEquals:
				p = 7;
				break;// !=
			case less:
				p = 6;
				break;// <
			case greater:
				p = 6;
				break;// >
			case lessEquals:
				p = 6;
				break;// <=
			case greaterEquals:
				p = 6;
				break; // >=
			case lShift:
				p = 5;
				break;// <<
			case rSignedShift:
				p = 5;
				break;// >>
			case rUnsignedShift:
				p = 5;
				break;// >>>
			case plus:
				p = 4;
				break;// +
			case minus:
				p = 4;
				break;// -
			case times:
				p = 3;
				break;// *
			case divide:
				p = 3;
				break;// /
			case remainder:
				p = 3;
				break;// %
			}
			return p;
		}
	}

	public static int decideIndex(List<SubExpression> list, BinaryExpr e) {
		int leftsize = getASTsize(e.getLeft());
		int rightsize = getASTsize(e.getRight());
		if ((leftsize + rightsize + 1) != list.size()) {
			int totalsize = leftsize + rightsize + 1;
			if(totalsize==5)
			{System.out.println();}
			System.out.println("size error! left+right+1=" + totalsize
					+ " list size=" + list.size());
			{
				for (int i = 0; i < list.size() - totalsize; i++)
					list.remove(i);
			}
			return leftsize;
		} else {
			return leftsize;
		}
	}

	public static int getASTsize(Expression e) {
		boolean flag = false;
		BinaryExpr be = null;
		String eName = getExpressionName(e.getClass().getName());
		if (eName.compareTo("BinaryExpr") == 0) {
			flag = true;
			be = (BinaryExpr) e;
		} else if (eName.compareTo("MethodCallExpr") == 0) {
			MethodCallExpr mce = (MethodCallExpr) e;
			if (mce.getArgs() != null) {
				if (getExpressionName(mce.getArgs().get(0).getClass().getName())
						.compareTo("BinaryExpr") == 0) {
					flag = true;
					be = (BinaryExpr) mce.getArgs().get(0);
				}
			}
		} else if (eName.compareTo("EnclosedExpr") == 0) {
			EnclosedExpr ecle = (EnclosedExpr) e;
			if (getExpressionName(ecle.getInner().getClass().getName())
					.compareTo("BinaryExpr") == 0) {
				flag = true;
				be = (BinaryExpr) ecle.getInner();
			}
		} else if (eName.compareTo("ArrayAccessExpr") == 0) {
			ArrayAccessExpr aae = (ArrayAccessExpr) e;
			if (getExpressionName(aae.getIndex().getClass().getName())
					.compareTo("BinaryExpr") == 0) {
				flag = true;
				be = (BinaryExpr) aae.getIndex();
			}
		}
		int left = 0, right = 0;
		if (flag == true) {
			left = getASTsize(be.getLeft());
			right = getASTsize(be.getRight());
			return left + right + 1;
		} else {
			return 0;
		}
	}

	private static String getExpressionName(String fullname) {

		String[] names = fullname.split("\\.");
		String name = names[names.length - 1];
		return name;
	}
/**************************************************************/
	
	
}
