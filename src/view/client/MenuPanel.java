package view.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;
import common.Observable;
import common.Observer;

public class MenuPanel extends JPanel implements Observer{
	private Client c;
	private JFrame parent;
	private JLabel ip, port, username, clientId;
	private JLabel downloadStatus;
	
	public MenuPanel(JFrame parent, Client c) {
		super(new BorderLayout());
		
		this.parent = parent;
		this.c = c;
		c.addObserver(this);
		
		JLabel title = new JLabel("Menu");
		downloadStatus = new JLabel();
		JPanel titlePanel = new JPanel(new GridLayout(1,2));
		titlePanel.add(title);
		titlePanel.add(downloadStatus);
		add(titlePanel, BorderLayout.NORTH);
		add(createActionsPanel(), BorderLayout.CENTER);
	}
	
	private JPanel createActionsPanel() {
		//Descarga de archivo
		JPanel downloadFilePanel = new JPanel(new GridLayout(1,2));
		
		JTextField fileName = new JTextField();
		JButton fileButton = new JButton("Descargar");
		fileButton.addActionListener((ActionEvent e) -> {
			c.downloadFile(fileName.getText());
		});
		
		downloadFilePanel.add(fileName);
		downloadFilePanel.add(fileButton);

		//Subir archivo
		JButton uploadFile = new JButton("Subir archivo");
		uploadFile.addActionListener((ActionEvent e) -> {
			FileChooserDialog dialog = new FileChooserDialog(parent);
			File fileChoosed = dialog.getSelectedFile();
			if (fileChoosed != null)
				c.newShareableFile(fileChoosed.getName(), fileChoosed.getAbsolutePath());
		});
		
		//CerrarConexion
		JButton closeConnection = new JButton("Cerrar conexion");
		closeConnection.addActionListener((ActionEvent e) -> {
			c.closeConnection();
		});
		
		//Información del cliente
		JPanel clientInfoPanel = new JPanel(new GridLayout(2,2));
		ip = new JLabel("Ip: " + c.getIp());
		port = new JLabel("Port: " + c.getPort());
		username = new JLabel("Username: " + c.getUsername());
		clientId = new JLabel("Client ID: Not set yet");
		clientInfoPanel.add(ip);
		clientInfoPanel.add(port);
		clientInfoPanel.add(username);
		clientInfoPanel.add(clientId);
		
		JPanel actionsPanel = new JPanel(new GridLayout(4,1));
		
		actionsPanel.add(clientInfoPanel);
		actionsPanel.add(downloadFilePanel);
		actionsPanel.add(uploadFile);
		actionsPanel.add(closeConnection);
		
		return actionsPanel;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String && (String) arg == "id_set")
			clientId.setText("ClientId: " + c.getId());
		else if (arg instanceof String && "failed_download".equals((String) arg))
			downloadStatus.setText("Download failed");
		else if (arg instanceof String && ((String) arg).split(":")[0].equals("success_download"))
			downloadStatus.setText("Succesfully downloaded " + " " + ((String) arg).split(":")[1]);
	}
}
