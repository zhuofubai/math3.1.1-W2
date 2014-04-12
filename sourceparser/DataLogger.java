/**
 * 
 */
package sourceparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuofu
 *
 */
public class DataLogger {
	static String dir;
	/**
	 * 
	 */
	public DataLogger() {
		
		// TODO Auto-generated constructor stub
	}
	public void setDir(String dir) {
		this.dir=dir;
	}
	public static void logToConsole(List<BasicExpression> mlineList,List<SubExpression> subExpListcopy){
		SubExpression e=null;
		BasicExpression be=null;
		int subExpId=-1;
		int cmbdExpId=-1;
		double target=0;
		int index;
		ArrayList<Double> covarList=new ArrayList<Double>();
		for(int i=0;i<subExpListcopy.size();i++){
			e=subExpListcopy.get(i);
			subExpId=e.getId();
			target=mlineList.get(subExpId).getTarget();
			covarList=mlineList.get(subExpId).getCovarList();
			for (int j=0;j<e.getList().size();j++){
				cmbdExpId=(Integer) e.getList().get(j);
				be=mlineList.get(cmbdExpId);
				index=covarList.indexOf(be.getTarget());
				covarList.remove(index);
				covarList.addAll(be.getCovarList());
			}
			print(target, covarList);
		}
	}
	public void logToFile(int testid,int lineNum,List<BasicExpression> mlineList,List<SubExpression> subExpListcopy){
		SubExpression e=null;
		BasicExpression be=null;
		int subExpId=-1;
		int cmbdExpId=-1;
		double target=0;
		int index;
		ArrayList<Double> covarList=new ArrayList<Double>();
		for(int i=0;i<subExpListcopy.size();i++){
			e=subExpListcopy.get(i);
			subExpId=e.getId();
			target=mlineList.get(subExpId).getTarget();
			covarList=mlineList.get(subExpId).getCovarList();
			for (int j=0;j<e.getList().size();j++){
				cmbdExpId=(Integer) e.getList().get(j);
				be=mlineList.get(cmbdExpId);
				index=covarList.indexOf(be.getTarget());
				covarList.remove(index);
				covarList.addAll(be.getCovarList());
			}
			print(target, covarList);
			printToFile(dir,testid,lineNum,subExpId,target, covarList);
		}
	}
		
	public static void printToFile(String dir,int testid, int lineNum,int subExpId,Double target, ArrayList<Double> covarList){
		String filename=dir+"/"+ lineNum+"_"+subExpId+".txt";
		BufferedWriter out;
		try {
			File f = new File(filename);
			if (f.exists()){
				//System.out.println("file exist");
				out = new BufferedWriter(new FileWriter(filename,true));
			}else
			{
				out = new BufferedWriter(new FileWriter(filename));
				out.write(filename);
				out.newLine();
				//out.flush();
			}
			out.write(testid + " "+target+" ");
			for (int i=0;i<covarList.size();i++){
				out.write(covarList.get(i)+" ");				
			}
		out.newLine();
		//out.flush();
		out.close();				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void print(double t, ArrayList<Double>list){
		System.out.print(t+" ");
		for (int i=0;i<list.size();i++){
			System.out.print(list.get(i)+" ");
		}
		System.out.println("");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
