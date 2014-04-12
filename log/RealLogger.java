/**
 * 
 */
package log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Zhuofu
 *
 */
public class RealLogger implements Logger2 {
	int testId;
	String filedir;
	HashMap<String,Integer> hm;
	ArrayList<ExpressionData> expressionList;
	BufferedWriter out;
	int expressionID;
	/**
	 * 
	 */
	
	public RealLogger() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void logFile() {
		// TODO Auto-generated method stub
		boolean isExist=false;
		
		String filename="a";
		DecimalFormat df = new DecimalFormat("0.#####E0");
		for (int i=0;i<this.expressionList.size();i++){
			filename=filedir+"/"+ expressionList.get(i).expressionId+".txt";
			
			try {
				File f = new File(filename);
				if (f.exists()){
					//System.out.println("file exist");
					out = new BufferedWriter(new FileWriter(filename,true));
				}else
				{
					out = new BufferedWriter(new FileWriter(filename));
					out.write(expressionList.get(i).getName()+"\n");
					out.flush();
				}
				out.write(testId + " ");
				for (int j=0;j<expressionList.get(i).getDataLength();j++){
					out.write(df.format(expressionList.get(i).getData(j))+" ");				
				}
			out.write("\n");
			out.flush();
			out.close();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	@Override
	public void add( double[] data, String name) {
		// TODO Auto-generated method stub
		
		if(checkId(name)>0){
			ExpressionData e=new ExpressionData(hm.get(name),data);
			e.setName(name);
			expressionList.add(e);
		}
		else{
			ExpressionData e=new ExpressionData(expressionID,data);
			e.setName(name);
			expressionList.add(e);
		}
		
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		expressionList.clear();
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return testId;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return expressionList.size();
	}

	@Override
	public int checkId(String name) {
		// TODO Auto-generated method stub
		if(hm.get(name)==null){
			return 0;
		}else{
		return 1;
		}
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		testId=id;
	}

	@Override
	public void setDir(String dir) {
		// TODO Auto-generated method stub
		this.filedir=dir;
	}

}
