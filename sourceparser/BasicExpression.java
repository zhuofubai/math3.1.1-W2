package sourceparser;

import java.util.ArrayList;

public class BasicExpression {
	private String op;
	private double target;
	private ArrayList<Double> covarList;
	private int testId;
	public BasicExpression(){
		
	}
	public BasicExpression(String[] terms) {
		// TODO Auto-generated constructor stub
		if (terms.length<=2){
			System.out.println("warning there is no data recorded on the line " +terms[0] );
		}else {
			testId=setTestId(terms[0]);
			covarList=new ArrayList<Double>();
			//try{
			op=parseOp(terms[1]);//}
//			catch(java.lang.IllegalArgumentException e){
//				System.out.println(terms[1]);
//			}
			if(op==null)
			{System.out.println("unknown op"+terms[1]);}
			setTarget(terms[2]);
			setCovarList(terms[3]);
		}
	}

	/**
	 * @param args
	 */
	public boolean check(){
		if(this.op!=null&&this.target!=0&&this.covarList!=null)
		return true;
		else
			return false;
	}
	public void setTarget(String t){
		if(t.substring(1,t.length()-1).contains("true")||t.substring(1,t.length()-1).contains("false")){
			System.out.println("1");
			this.target=0;
			return;
		}
		this.target=Double.parseDouble(t.substring(1,t.length()-1));
	}
	public void setCovarList(String c){
		this.covarList= new ArrayList<Double>();
		String tmp=c.substring(1,c.length()-1);
		String[] cvs=tmp.split(",");
		for(int i=0;i<cvs.length;i++){
			if(cvs[i].compareTo("false")==0){
				this.covarList.add(0.0);
			}else if(cvs[i].compareTo("true")==0){
				this.covarList.add(1.0);
			}else{
			this.covarList.add(Double.parseDouble(cvs[i]));}
		}
	}
	public void setOp(String op){
		this.op=op;
	}
	public String getOp(){
		return op;
	}
	public double getTarget(){
		return target;
	}
	public ArrayList<Double> getCovarList(){
		return covarList;
	}
	public static String parseOp(String op){
		String operator=op.substring(1, op.length()-1);
		
		
		String d=null;
		try{FileOperator currentOp=FileOperator.valueOf(operator);
		switch(currentOp){
			case IDIV:
				d="divide";break;
			case IMUL:
				d="times";break;
			case IADD:
				d="plus";break;
			case ISUB:	
				d="minus";break;
			case DSUB:
				d="minus";break;
			case DADD:
				d="plus";break;
			case DMUL:
				d="times";break;
			case DDIV:
				d="divide";break;
			case ISHR:
				d="rSignedShift";break;
			case ISHL:
				d="lShift";break;
			case DREM:
				d="remainder";break;
			case IREM:
				d="remainder";break;
			default:
				d="unknown";break;
		}
		}
		catch(Exception e){
			d="unknown";
			//System.out.println("Exception "+operator);
		}
		return d;
	}
	public enum FileOperator{
		IDIV,
		IMUL,
		IADD,
		ISUB,
		DSUB,
		DADD,
		DMUL,
		DDIV,
		ISHR,
		ISHL,
		DREM,
		IREM,
		IINC,
	}
		
	private int setTestId(String t) {
		String m = t.substring(1).split("_")[0];
		int testId = Integer.parseInt(m);
		return testId;
	}
	public int getTestId(){
		return this.testId;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
