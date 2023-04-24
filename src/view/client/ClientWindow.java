package view.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import client.Client;
import common.Observable;
import common.Observer;
import common.ProtocolError;

public class ClientWindow extends JFrame implements Observer{

	private JPanel mainPanel;
	private Client client;
	private String username;
	
	public ClientWindow(String ip, Integer port) {
		obtainUserName();
		setTitle("Client " + username);
		
		createMainPanel(ip, port);
		setCloseAction();
		
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void obtainUserName() {
		JDialog dialog = new JDialog(this, "Credenciales", true);
		JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		JPanel userInfoPanel = new JPanel(new GridLayout(1,2));
		JLabel userLabel = new JLabel("Username: ");
		JTextField userTextField = new JTextField();
		userInfoPanel.add(userLabel);
		userInfoPanel.add(userTextField);
		
		JButton userButton = new JButton("Save");
		userButton.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					userButton.doClick();
			}
		});
		userButton.addActionListener((ActionEvent e) -> {
			username = userTextField.getText();
			dialog.dispose();
			//Cerrar el dialog
		});
		
		infoPanel.add(userInfoPanel);
		infoPanel.add(userButton);
		
		dialog.setContentPane(infoPanel);
		
		dialog.setLocationRelativeTo(null);
		dialog.setSize(400,300);
		
		dialog.setVisible(true);
	}
	
	private void createMainPanel(String ip, Integer port){
		
		try {
			client = new Client(username, ip, port);
			client.addObserver(this);
			mainPanel = new JPanel(new GridLayout(2,  2));
			
			mainPanel.add(new OwnedFilesPanel(client));
			mainPanel.add(new DownloadableFilesPanel(client));
			mainPanel.add(new JScrollPane(new LogPanel(client)));
			mainPanel.add(new MenuPanel(this, client));

			client.connectToServer();
		}catch (ClassNotFoundException | IOException | ProtocolError e) {
			
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(new JLabel("Error al establecer conexion con el servidor"), BorderLayout.CENTER);
			
			e.printStackTrace();
		}
		add(mainPanel);
	}

	private void setCloseAction() {
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				client.closeConnection();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String && (String) arg == "close_connection")
			dispose();
	}
}
