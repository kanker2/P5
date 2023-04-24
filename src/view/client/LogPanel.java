package view.client;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import client.Client;
import client.StreamProxy;
import common.Message;
import common.Observable;
import common.Observer;

public class LogPanel extends JPanel implements Observer{
	
	private JTextArea log;
	private JLabel titleLabel;
	
	public LogPanel(Client c) {
		super(new BorderLayout());
		
		log = new JTextArea();
		
		JPanel titlePanel = new JPanel(new BorderLayout(10,0));
		
		titlePanel.add(new JLabel("Log panel"), BorderLayout.WEST);
		titleLabel = new JLabel();
		titlePanel.add(titleLabel, BorderLayout.EAST);
		add(titlePanel, BorderLayout.NORTH);
		
		add(log, BorderLayout.CENTER);
		
		c.setLog(this);
		c.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Message)
			log.append(((Message) arg).toString() + "\n");
		else if(arg instanceof String && (String) arg == "id_set")
			titleLabel.setText("ID Client: " + ((Client) o).getId() + " ");
		else if (arg instanceof String && (String) arg == "error")
			log.append("Error tras mensaje: " + ((StreamProxy) o).lastCommunication() + "\n");
	}
}
