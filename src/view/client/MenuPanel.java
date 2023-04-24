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

public class MenuPanel extends JPanel{
	private Client c;
	private JFrame parent;
	
	public MenuPanel(JFrame parent, Client c) {
		super(new BorderLayout());
		
		this.parent = parent;
		this.c = c;
		
		add(new JLabel("Menu"), BorderLayout.NORTH);
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
		
		JPanel actionsPanel = new JPanel(new GridLayout(4,1));
		
		actionsPanel.add(downloadFilePanel);
		actionsPanel.add(uploadFile);
		actionsPanel.add(closeConnection);
		
		return actionsPanel;
	}
}
