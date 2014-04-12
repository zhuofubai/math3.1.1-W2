package sourceparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;


import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.ExpressionStmt;

/**
 * The SourceParser class parses all of the files in a given root
 * source path.  It generates a list of all of the variables and 
 * declared variables.
 * @author jrs213
 *
 */
public class SourceParser2 {
	
	/** Collection of file maps */
	private Files files = new Files();
	private Files2 files2 = new Files2();

	/**
	 * This sets the root paths for the SourceParser to read from.  
	 * @param rootPaths Is a big String with each path in the list
	 * separated by the default file path separator for the host 
	 * operating system
	 * @return The number of files that failed to parse
	 */
	public int addRootPaths(String rootPaths) {
		int failed = 0;

		// Add all of the files
		for (String path : rootPaths.split(File.pathSeparator)) {
			failed += addPath(path, "");
		}

		return failed;
	}

	/**
	 * Add a single path
	 * @param rootPath The path to the root source directory
	 * @param subPath The relative path inside the root
	 * @return The number of files that failed to parse
	 */
	private int addPath(String rootPath, String subPath) {
		// Keep a count of failed files
		int failed = 0;

		File directory = new File(rootPath, subPath);

		if (directory.exists() == false) {
			return 1;
		}

		// Get all of the files
		File[] files = directory.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().endsWith(".java")) {
					// If this is a Java file, parse it
					//System.out.println(File.separator);
					String relativeFilePath = subPath + File.separator + file.getName();					
					while (relativeFilePath.startsWith("/")) {
						// Parse subdirectories
						relativeFilePath = relativeFilePath.substring(1);
					}

					// Maintain failed count
					failed += addFile(rootPath, relativeFilePath);
				} else if (file.isDirectory()) {
					// Parse subdirectories
					failed += addPath(rootPath, subPath + File.separator + file.getName());
				}
			}
		}

		return failed;
	}

	/**
	 * This adds a single file to the parser
	 * @param rootPath The root path to the source folder
	 * @param relativeFilePath The relative path inside the root path to the file.  The
	 * file will be added to the map with the key of the entire relative path so that 
	 * lookups from within Sorbet do not have any ambiguity
	 * @return 0 if success, 1 if failed
	 */
	public int addFile(String rootPath, String relativeFilePath) {		
		try {
			// Open file
			FileInputStream file = new FileInputStream(rootPath + File.separator + relativeFilePath);

			// Setup parser
			CompilationUnit cu = JavaParser.parse(file);
			
			// Visit the file with our visitor
			VariableVisitor<Object> variableVisitor = new VariableVisitor<Object>();
			variableVisitor.visit(cu, null);

			Lines lines = variableVisitor.getLines();
			Lines2 lines2 = variableVisitor.getLines2();
//			for (int key:lines2.keySet()){
//				System.out.println(key);
//			}
			if (files.containsKey(relativeFilePath)) {
				// Already contains this file, return failed
				return 1;
			} else {
				files.put(relativeFilePath, lines);
				files2.put(relativeFilePath, lines2);
			}			

		} catch (FileNotFoundException e) {
			// File doesn't exist, return failed
			return 1;
		} catch (ParseException e) {
			// File isn't valid, return failed
			return 1;
		}
		
		// Return success
		return 0;		
	}

	public List<String> getVariables(String filePath, int lineNumber) {
		Lines lines = files.get(filePath);		
		if (lines == null) {
			return null;
		}

		Variables variables = lines.get(lineNumber);		
		return variables;
	}
	
	public Expression getExpression(String filePath, int lineNumber){
		Lines2 lines2=files2.get(filePath);
		if (lines2 == null){
			return null;
		}
		
		Expression exp=lines2.get(lineNumber);
		return exp;
	}
	public Lines2 getLines(String filePath){
		Lines2 lines2=files2.get(filePath);
		return  lines2;
	}
	public HashSet<String> getDeclVars(String filePath) {
		Lines lines = files.get(filePath);
		if (lines == null) {
			return null;
		}
		
		return lines.declVars;
	}
}
