package sourceparser;

import japa.parser.ast.stmt.ExpressionStmt;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This maps integer line numbers to linked lists of variable names.  Also contains
 * a list of declared variables.
 * @author jrs213
 *
 */
public class Lines extends HashMap<Integer, Variables> {

	private static final long serialVersionUID = -8935595136268832020L;

	public HashSet<String> declVars = new HashSet<String>();

}
