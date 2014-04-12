package log;

interface abstractLogger {
	
	public void logFile();
	public void add( double[] data, String name);
	public void clear();
	public void setId(int id);
	
}
