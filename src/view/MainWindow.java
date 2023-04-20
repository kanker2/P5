package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import main.Main;

public class MainWindow extends JFrame{
	
	private JPanel mainPanel;
	
	public MainWindow() {
		super("Práctica final");
		mainPanel = createMainPanel();
		this.add(mainPanel);
		
		this.setSize(400, 300);
		this.setLocationRelativeTo(this.getContentPane());
		
		this.setResizable(false);
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.setVisible(true);
	}
	
	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel(new CardLayout());
		
		mainPanel.add(createButtonsPanel(), "Buttons");
		mainPanel.add(createServerCreationPanel(), "Server creation");
		mainPanel.add(createClientCreationPanel(), "Client creation");
		
		CardLayout cardsLayout = (CardLayout) mainPanel.getLayout();
		cardsLayout.show(mainPanel, "Buttons");
		
		return mainPanel;
	}
	
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		
		JButton b1 = new JButton("Crear servidor");
		b1.addActionListener((ActionEvent e) -> changeCard("Server creation"));
		
		JButton b2 = new JButton("Crear cliente");
		b2.addActionListener((ActionEvent e) -> changeCard("Client creation"));
		
		buttonsPanel.add(b1);
		buttonsPanel.add(b2);
		
		return buttonsPanel;
	}
	
	private void changeCard(String cardName) {
		CardLayout cl = (CardLayout) mainPanel.getLayout();
		cl.show(mainPanel, cardName);
	}
	
	private JPanel createServerCreationPanel() {
		
		JPanel receivePortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel portLabel = new JLabel("Puerto: ");
		receivePortPanel.add(portLabel);
		
		JTextField portTextField = new JTextField("5000");
		receivePortPanel.add(portTextField);
		
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton getPortButton = new JButton("Set");
		getPortButton.addActionListener((ActionEvent e) -> {
			portTextField.getText();
			//Creamos hilo que lleve la ventana correspondiente al servidor
			//Añadimos la carta al layout que se encargue de permitirnos crear unicamente clientes, pero en este mismo hilo
		});
		buttonPanel.add(getPortButton);
		
		JPanel serverCreationPanel = new JPanel(new GridLayout(2, 1, 5, 10));
		serverCreationPanel.add(receivePortPanel);
		serverCreationPanel.add(buttonPanel);
		
		return serverCreationPanel; 
	}
	
	private JPanel createClientCreationPanel() {
		return new JPanel();
	}
}
