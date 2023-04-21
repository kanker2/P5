package view.client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;

public class ClientWindow extends JFrame{

	private JPanel mainPanel;
	private Client client;
	private String username;
	
	public ClientWindow(String ip, Integer port) {
		obtainUserName();
		setTitle("Client " + username);
		
//		mainPanel = createMainPanel();
//		add(mainPanel);
		
		this.setSize(400, 300);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void obtainUserName() {
		JDialog dialog = new JDialog(this, "Credenciales", true);
		JPanel infoPanel = new JPanel(new GridLayout(2, 1));
		
		JPanel userInfoPanel = new JPanel(new GridLayout(1,2));
		JLabel userLabel = new JLabel("Username: ");
		JTextField userTextField = new JTextField();
		userInfoPanel.add(userLabel);
		userInfoPanel.add(userTextField);
		
		JButton userButton = new JButton("Save");
		userButton.addActionListener((ActionEvent e) -> {
			username = userTextField.getText();
			//Cerrar el dialog
		});
		
		infoPanel.add(userInfoPanel);
		infoPanel.add(userButton);
		
		dialog.setContentPane(infoPanel);
	}
	
	private JPanel createMainPanel(Client c) {
		JPanel mainPanel = new JPanel(new GridLayout(2,  2));
		mainPanel.add(new OwnedFilesPanel());
		mainPanel.add(new DownloadableFilesPanel());
		mainPanel.add(new LogPanel());
		mainPanel.add(new MenuPanel());
		return mainPanel;
	}
}
