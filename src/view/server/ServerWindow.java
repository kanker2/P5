package view.server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import server.Server;

public class ServerWindow extends JFrame {
	
	private Server server;
	private JPanel mainPanel;
	
	public ServerWindow(JFrame mainWindow, Integer port) {
		super("Servidor");

		
		connectWithMainWindow(mainWindow);
		
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		createMainPanel(port);
		
		(new Thread(server)).start();
	}
	
	private void createMainPanel(Integer port){
		try {
			server = new Server(port);
			mainPanel = new JPanel(new GridLayout(2,2));
			
			mainPanel.add(new FilesInfoPanel(server));			
			mainPanel.add(new ClientsInfoPanel(server));
			mainPanel.add(new JScrollPane(new LogPanel(server)));
			mainPanel.add(new ServerInfoPanel());
			
		} catch(IOException e) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(new JLabel("Error al crear el servidor"), BorderLayout.CENTER);
		}
		add(mainPanel);
	}
	
	private void connectWithMainWindow(JFrame mainWindow) {
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				mainWindow.dispose();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
	}
}
