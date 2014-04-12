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
public class Logger {
	int testId;
	String filedir;
	ArrayList<ExpressionData> expressionList;
	BufferedWriter out;
	HashMap hm=new HashMap();
	public Logger() {
		
		this.expressionList=new ArrayList<ExpressionData>();
		
		// TODO Auto-generated constructor stub
	}
	public void setTestId(int id){
		this.testId=id;
	}
	public void setDir(String dir){
		this.filedir=dir;
	}
	public void add(int id, double[] data, String name){
		ExpressionData e=new ExpressionData(id,data);
		e.setName(name);
		expressionList.add(e);
		
	}
	public int getLength(){
		return expressionList.size();
	}
	public void replace(int id,double[]data){
		ExpressionData a=expressionList.get(id-1);
		a.changeData(data);
	}
	public void logFile(){
		boolean isExist=false;
		
		String filename="a";
		DecimalFormat df = new DecimalFormat("0.#########E0");
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
					out.write(expressionList.get(i).getName());
					out.newLine();
					
				}
				out.write(testId + " ");
				for (int j=0;j<expressionList.get(i).getDataLength();j++){
					out.write(df.format(expressionList.get(i).getData(j))+" ");				
				}
			out.newLine();
			
			out.close();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	public int getID(){
		return testId;
	}
	public void clear(){
		expressionList.clear();
		//testId=0;
		
	}
	public static void main(String[] args){}
}
