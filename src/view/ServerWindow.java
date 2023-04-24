package view;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import server.Server;

public class ServerWindow extends JFrame{
	
	private Server server;
	private JPanel mainPanel;
	
	public ServerWindow(JFrame mainWindow, Integer port) {
		super("Servidor");

		
		connectWithMainWindow(mainWindow);
		
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		createMainPanel(port);
		
		server.start();
	}
	
	private void createMainPanel(Integer port){
		try {
			server = new Server(port);
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(new JLabel("Creado"), BorderLayout.CENTER);
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
