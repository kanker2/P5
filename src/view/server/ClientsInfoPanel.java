package view.server;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import common.Observable;
import common.Observer;
import server.Server;

public class ClientsInfoPanel extends JPanel implements Observer{

	private JTable chart;
	private DefaultTableModel model;
	
	public ClientsInfoPanel(Server s) {
		super(new BorderLayout());
		
		s.addObserver(this);
		
		model = new DefaultTableModel(new Object[] {"ID Cliente", "Nombre usuario"}, 0);
		chart = new JTable(model);
		
		this.add(new JLabel("Usuarios en el sistema"), BorderLayout.NORTH);
		this.add(new JScrollPane(chart), BorderLayout.CENTER);
	}
	
	private void updateUsers(Server s) {
		Map<String, String> users = s.getUsers();
		model.setRowCount(0);
		for(Map.Entry<String, String> user : users.entrySet()) 
			model.addRow(new Object[] {user.getKey(), user.getValue()});
		model.fireTableDataChanged();
	}
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String && ((String) arg == "new_connection" || (String) arg == "removed_connection"))
			updateUsers((Server) o);
	}

}
