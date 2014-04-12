package sourceparser;

import java.util.ArrayList;

public class SubExpression {
	private int id;
	private String op;
	private ArrayList<Integer> cmbdList;

	public SubExpression(int id, BasicExpression n) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.op = n.getOp();
		cmbdList = new ArrayList<Integer>();

	}

	public SubExpression(int id, String op, ArrayList<Integer> cmbdList) {
		this.id = id;
		this.op = op;
		this.cmbdList = cmbdList;
	}

	public void merge(SubExpression m) {
		cmbdList.add(m.getId());
		cmbdList.addAll(m.getList());
		// do the merge thing;
	}

	public SubExpression copy() {
		SubExpression cp = new SubExpression(this.id, this.op, this.cmbdList);
		return cp;
	}

	public int getId() {
		return id;
	}

	public String getOp() {
		return op;
	}

	public ArrayList getList() {
		return cmbdList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
