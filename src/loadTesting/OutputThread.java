package loadTesting;

import java.io.PrintWriter;

public class OutputThread extends Thread{

	private String command;
	private PrintWriter out;
	public OutputThread(String command,PrintWriter out)
	{
		this.command = command;
		this.out = out;	
	}
	@Override
	public void run() {
		synchronized (out) {
			out.println(command);
		}
		
	}

}
