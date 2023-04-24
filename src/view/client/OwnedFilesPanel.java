package view.client;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import client.Client;
import common.Observable;
import common.Observer;

public class OwnedFilesPanel extends JPanel implements Observer {
	
	private JTable chart;
	private DefaultTableModel model;
	
	public OwnedFilesPanel(Client c) {
		super(new BorderLayout());
		
		c.addObserver(this);
		
		model = new DefaultTableModel(new Object[] {"Nombre archivo", "Ruta"}, 0);
		chart = new JTable(model);
		
		this.add(new JLabel("Archivos para compartir"), BorderLayout.NORTH);
		this.add(new JScrollPane(chart), BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg) {
		String msg = (String) arg;
		if (msg == "owned_files") {
			Client c = (Client) o;
			Map<String, String> shareableFiles = c.getShareableFiles();
			model.setRowCount(0);
			for(Map.Entry<String, String> entry : shareableFiles.entrySet()) 
				model.addRow(new Object[] {entry.getKey(), entry.getValue()});
			model.fireTableDataChanged();
		}
	}
}
