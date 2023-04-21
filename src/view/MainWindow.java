package view;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import view.client.ClientWindow;

public class MainWindow extends JFrame{

	private final String INIT_SERVER_CARD_NAME = "Server Creation";
	private final String INIT_CLIENT_CARD_NAME = "Client Creation";
	private final String ADD_CLIENT_CARD_NAME = "Add Client Creation";
	
	private JPanel mainPanel;
	private String ip;
	private Integer port;
	
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
		mainPanel.add(createServerCreationPanel(), INIT_SERVER_CARD_NAME);
		mainPanel.add(createClientCreationPanel(), INIT_CLIENT_CARD_NAME);
		mainPanel.add(createAddClientPanel(), ADD_CLIENT_CARD_NAME);
		
		CardLayout cardsLayout = (CardLayout) mainPanel.getLayout();
		cardsLayout.show(mainPanel, "Buttons");
		
		return mainPanel;
	}
	
	private JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		
		JButton b1 = new JButton("Crear servidor");
		b1.addActionListener((ActionEvent e) -> changeCard(INIT_SERVER_CARD_NAME));
		
		JButton b2 = new JButton("Crear cliente");
		b2.addActionListener((ActionEvent e) -> changeCard(INIT_CLIENT_CARD_NAME));
		
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
			ip = "localhost";
			port = Integer.parseInt(portTextField.getText());
			
			SwingUtilities.invokeLater(()-> {
				new ServerWindow(MainWindow.this, port);
			});
			
			changeCard(ADD_CLIENT_CARD_NAME);
		});
		buttonPanel.add(getPortButton);
		
		JPanel serverCreationPanel = new JPanel(new GridLayout(2, 1, 5, 10));
		serverCreationPanel.add(receivePortPanel);
		serverCreationPanel.add(buttonPanel);
		
		return serverCreationPanel; 
	}
	
	private JPanel createClientCreationPanel() {
		
		JPanel receiveIpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel ipLabel = new JLabel("IP: ");
		receiveIpPanel.add(ipLabel);
		
		JTextField ipTextField = new JTextField("localhost");
		receiveIpPanel.add(ipTextField);
		
		JPanel receivePortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel portLabel = new JLabel("Puerto: ");
		receivePortPanel.add(portLabel);
		
		JTextField portTextField = new JTextField("5000");
		receivePortPanel.add(portTextField);
		

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton getPortButton = new JButton("Set");
		
		getPortButton.addActionListener((ActionEvent e) -> {
			ip = ipTextField.getText();
			port = Integer.parseInt(portTextField.getText());
			
			SwingUtilities.invokeLater(()-> {
				new ClientWindow(ip, port);
			});
			
			changeCard(ADD_CLIENT_CARD_NAME);
		});
		buttonPanel.add(getPortButton);
		
		JPanel clientCreationPanel = new JPanel(new GridLayout(3, 1, 5, 10));
		clientCreationPanel.add(receiveIpPanel);
		clientCreationPanel.add(receivePortPanel);
		clientCreationPanel.add(buttonPanel);
		
		return clientCreationPanel; 
	}

	private JPanel createAddClientPanel() {
		JPanel createClientPanel = new JPanel(new GridLayout(1,1));
		
		JButton createClientWindow = new JButton("+");
		createClientWindow.addActionListener((ActionEvent e) -> {
			SwingUtilities.invokeLater(() -> {
				new ClientWindow(ip, port);
			});
		});
		createClientPanel.add(createClientWindow);
		
		return createClientPanel;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		System.exit(0);
	}
}
