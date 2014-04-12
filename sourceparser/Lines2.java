package sourceparser;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.ExpressionStmt;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This maps integer line numbers to linked lists of variable names.  Also contains
 * a list of declared variables.
 * @author jrs213
 *
 */
public class Lines2 extends HashMap<Integer, Expression> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6281780112822171202L;
	public HashSet<String> declVars = new HashSet<String>();

}
