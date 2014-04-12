/**
 * 
 */
package sourceparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author zhuofu
 *
 */
public class Testsourceparser {

	/**
	 * 
	 */
	public Testsourceparser() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		SourceParser2 sourceParser;
		
		// Test a path that exists with only valid files
		sourceParser = new SourceParser2();
		String folder_base = "C:/Users/zhuofu/workspace/apacheCommonMath3.1.1/";
		FileInputStream in = new FileInputStream("C:/Users/zhuofu/workspace/apacheCommonMath3.1.1/src/experiments/FastCosineTransformer_bug.java");
		String rootPath =folder_base+"src/experiments";
		 System.out.println("file exist");
		 sourceParser.addRootPaths(rootPath );
		 System.out.println("end");
	}

}
