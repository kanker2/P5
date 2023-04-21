package view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class ServerWindow extends JFrame{
	
	public ServerWindow(JFrame mainWindow, Integer port) {
		super("Servidor");

		
		
		conectarConMainWindow(mainWindow);
		
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void conectarConMainWindow(JFrame mainWindow) {
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
