package sourceparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import japa.parser.ast.body.VariableDeclarator;

/**
 * The VariableVisitor uses the javaparser library to parse given source
 * files and extract the declared variables and used variables at every
 * line.
 * @author johngunderman
 *
 * @param <A> We don't actually know what this is used for
 */
public class VariableVisitor<A> extends VoidVisitorAdapter<A> {
	/** Switch of mustation testing*/
	private boolean mutation=true;
	/** Lines map for this file */
	private Lines lines = new Lines();	
	private Lines2 lines2= new Lines2();
	private BEcollection be=new BEcollection();
	private BEcollection be_constant=new BEcollection();
	
	/** The column of the last field we've seen.  This is cleared
	 * if the next thing isn't a thisExpr
	 */
	private int fieldCol = -1;

	/** The name of the last field we've seen.  This is also cleared
	 * if the next thing isn't a thisExpr
	 */
	private String lastField = "";

	/**
	 * Retrieves the mapping of line numbers to variables for this file
	 * @return A map of line numbers to variables
	 */
	public Lines getLines() {
		return lines;
	}
	public Lines2 getLines2(){
		return lines2;
	}
	
	public BEcollection getBEcollection(int isConstant){
		if (isConstant==0)
		{return this.be;}
		else
		{return this.be_constant;}
	}
	/**
	 * Visiting names expression clears the fieldCol and lastField
	 */
	@Override
	public void visit(BinaryExpr n, A arg){
		/** if mutation was turned on, then began to collect binary variables*/
		if (mutation==true)
		{
		testBinaryExpr(n);
//		System.out.println(n.toString());
//		System.out.println(n.getLeft().toString());
		int linum=n.getBeginLine();
		int a =linum;
		}
		
		super.visit(n, arg);
	}
	
	public void testBinaryExpr(BinaryExpr n){
		boolean isConstant=false;
		addBinaryExpression(this.be, n.getBeginLine(),n);
		if (getExpressionName(n.getLeft().getClass().getName())
				.compareTo("IntegerLiteralExpr") == 0
				|| getExpressionName(n.getLeft().getClass().getName())
						.compareTo("DoubleLiteralExpr") == 0)
		{isConstant=true;}
		
		if (getExpressionName(n.getRight().getClass().getName())
				.compareTo("IntegerLiteralExpr") == 0
				|| getExpressionName(n.getRight().getClass().getName())
						.compareTo("DoubleLiteralExpr") == 0)
		{isConstant=true;}
		
		if (isConstant==true){
			addBinaryExpression(this.be_constant, n.getBeginLine(),n);	
		}
	}
	
	private void addBinaryExpression(BEcollection bc, int line, BinaryExpr n){
			BExpressions bexpressions = bc.get(line);
		
		// If there are not any binary expressions known for this line, a List must be created
		if (bexpressions == null) {
			bexpressions = new BExpressions();

			bc.put(line, bexpressions);
		}
		
		// Add the binary expression to the list if it is not there already
		if (bexpressions.contains(n) == false) {
			bexpressions.add(n);
		}
	}
	
	@Override
	public void visit(AssignExpr n, A arg){
		int linum=n.getBeginLine();
		int a=linum;
		super.visit(n, arg);
	}
	
	@Override
	public void visit(ExpressionStmt n, A arg) {
		int linum=n.getBeginLine();
		//System.out.println(n.getExpression().getClass().getName());
		testExpressionStmtType(n.getExpression());
		int a=linum;
		super.visit(n, arg);
		}
	
	@Override
	public void visit(ArrayAccessExpr n, A arg){
		int linum=n.getBeginLine();
		int a=linum;
		super.visit(n, arg);
	}
	
	@Override
	public void visit(NameExpr n, A arg) {	
		String test=n.getName();
		int linum=n.getBeginLine();
		addVariableToLines(n.getBeginLine(), n.getName());

		fieldCol = -1;
		lastField  = "";

		// Continue the chain
		super.visit(n, arg);
	}

	/**
	 * Visiting a variable declaration adds it to the list of declared
	 * variables
	 */
	@Override
	public void visit(VariableDeclarationExpr n, A arg) {
		// Add to the declared variables for this file
		// this may cause issues as declVars doesn't monitor when things go out of scope.
		List<VariableDeclarator> test=n.getVars();
		lines.declVars.add(n.getVars().get(0).getId().getName());
		// Continue the chain
		super.visit(n, arg);
	}

	/**
	 * Visiting names expression clears the fieldCol and lastField
	 */
	@Override
	public void visit(VariableDeclarator n, A arg) {		
		int linenumber=n.getBeginLine();
		String test=n.getId().getName();
		Expression u=n.getInit();
		
		addVariableToLines(n.getBeginLine(), n.getId().getName());

		fieldCol = -1;
		lastField  = "";

		// Continue the chain
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldAccessExpr n, A arg) {	
		int linenumber=n.getBeginLine();
		String test=n.getField();
		addVariableToLines(n.getBeginLine(), n.getField());

		fieldCol = n.getBeginColumn();
		lastField = n.getField();

		super.visit(n, arg);
	}

	/**
	 * For this expressions, add this.variablename to the list of variables
	 */
	@Override
	public void visit(ThisExpr n, A arg) {
		if (fieldCol == n.getBeginColumn()) {
			// Get the variables of the current line
			Variables vars = lines.get(n.getBeginLine());
			
			if (vars == null) {
				//something must have broken in the sourceparser
				fieldCol = -1;
				lastField  = "";
				super.visit(n, arg);
			}
			
			// Add this.variablename to the list of varibales
			vars.removeLastOccurrence(lastField);
			vars.add("this." + lastField);
		}
		super.visit(n, arg);
	}

	/**
	 * Adds a variable to a given line
	 * @param line Line number
	 * @param variable Variable name
	 */
	private void addVariableToLines(int line, String variable) {
		Variables variables = lines.get(line);
		
		// If there are not any variables known for this line, a List must be created
		if (variables == null) {
			variables = new Variables();

			lines.put(line, variables);
		}
		
		// Add the variable to the list if it is not there already
		if (variables.contains(variable) == false) {
			variables.add(variable);
		}
	}
	
	private void addVariableToLines2(int line, Expression n){
		
		lines2.put(line,n);
	}
	
	private void testExpressionStmtType(Expression n){
		String name = getExpressionName(n.getClass().getName());
//		if(n.getBeginLine()==1064){
//			System.out.println("suspend"); 
//		}
		if (name.compareTo("VariableDeclarationExpr")==0){
			VariableDeclarationExpr vde=(VariableDeclarationExpr) n;
			List<VariableDeclarator> test=vde.getVars();
			if(test.get(0).getInit()!=null){
			name=getExpressionName(test.get(0).getInit().getClass().getName());
			if (name.compareTo("BinaryExpr")==0){
				
			addVariableToLines2(n.getBeginLine(),test.get(0).getInit());
			}else if(name.compareTo("MethodCallExpr")==0){
				addVariableToLines2(n.getBeginLine(),test.get(0).getInit());
			}
			}
		}
		else if(name.compareTo("AssignExpr")==0){
			AssignExpr ae= (AssignExpr)n;
			name=getExpressionName(ae.getValue().getClass().getName());
			if(name.compareTo("BinaryExpr")==0){
				//BinaryExpr be=(BinaryExpr)ae.getValue();
				//return true
				addVariableToLines2(n.getBeginLine(),ae.getValue());
			}
			else if(name.compareTo("MethodCallExpr")==0){
				
				addVariableToLines2(n.getBeginLine(),ae.getValue());
			}
				else if (name.compareTo("NameExpr")==0){
				addVariableToLines2(n.getBeginLine(),ae.getValue());
			}
			
		}else {
			
//			MethodCallExpr mce=(MethodCallExpr)test.get(0).getInit();
//			addVariableToLines2(n.getBeginLine(),test.get(0).getInit());
		}
		
		
		
	}
	private String getExpressionName(String fullname){
		
		String[] names=fullname.split("\\.");
		String name= names[names.length-1];
		return name;
	}
}
