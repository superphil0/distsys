package Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.PrivateKey;

public class ClientTimeStampResponder extends Thread{
	ServerSocket socket;
	private PrivateKey key;
	private int port;
	public ClientTimeStampResponder(int port,PrivateKey key)
	{
		super("ClientTimeStampResponder");
		this.port = port;
		this.key = key;
	}
	@Override
	public void run() {
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean listening = true;
		while(listening)
		{
			try {
				TimeStampResponder resp = new TimeStampResponder(socket.accept(),key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
