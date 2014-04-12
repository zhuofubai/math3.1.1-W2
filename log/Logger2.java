package log;

public interface Logger2 extends abstractLogger {
	public int getID();
	public int getLength();
	public int checkId(String name);
	public void setDir(String dir);
}
