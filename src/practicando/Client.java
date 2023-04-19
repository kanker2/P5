package practicando;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client{
	
	private int id;
	private String ip;
	private int port;
	private String fn;
	private File f;
	ClientSocketListener csl;
	
	public Client(int id, String ip, int port, String fn)
	{
		this.ip = ip;
		this.port = port;
		this.fn = fn;
		csl = new ClientSocketListener(this, 0);
		this.id = id;
		csl.start();
	}
	
	public void updateFile(File f) {
		this.f = f;
		System.out.println("Client " + id + " got " + f.getFileName() + " file succesfully");
		System.out.println(this.f);
	}
	
	public String getIP() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getFileName()
	{
		return fn;
	}

	public String getId() {
		return Integer.toString(id);
	}
	
	public static void f1()
	{
		try
		{
			Socket s = new Socket("localhost", 99);
			
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(3);
			
			BufferedReader finC = new BufferedReader(new InputStreamReader(s.getInputStream()));
			System.out.println("Cliente recibe: " + finC.readLine());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Cliente");
		}
	}
}
