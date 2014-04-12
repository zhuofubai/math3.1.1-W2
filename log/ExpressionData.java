/**
 * 
 */
package log;

/**
 * @author Zhuofu
 *
 */
public class ExpressionData {
	int expressionId;
	double [] expressionData;
	String expressionName;
	
	/**
	 * 
	 */
	public ExpressionData(int i, double []data) {
		this.expressionData=data;
		this.expressionId=i;
		// TODO Auto-generated constructor stub
	}
	public void setName(String name){
		this.expressionName=name;
	}
	public String getName(){
		return this.expressionName;
	}
	public int getDataLength(){
		return expressionData.length;
	}
	public double getData(int j){
		return expressionData[j];
	}
	public void changeData(double[]data){
		expressionData=data;
	}
	public void write(){
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
