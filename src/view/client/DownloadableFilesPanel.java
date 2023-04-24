package view.client;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import client.Client;
import common.Observable;
import common.Observer;

public class DownloadableFilesPanel extends JPanel implements Observer {
	
	private JTable chart;
	private DefaultTableModel model;
	
	public DownloadableFilesPanel(Client c) {
		super(new BorderLayout());
		
		c.addObserver(this);
		
		model = new DefaultTableModel(new Object[] {"Nombre archivo"}, 0);
		chart = new JTable(model);
		
		this.add(new JLabel("Archivos descargables"), BorderLayout.NORTH);
		this.add(new JScrollPane(chart), BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, Object arg) {
		String msg = (String) arg;
		if (msg == "downloadable_files") {
			Client c = (Client) o;
			Set<String> downloadableFiles = c.getDownloadableFiles();
			model.setRowCount(0);
			for(String file : downloadableFiles)
				model.addRow(new Object[] {file});
			model.fireTableDataChanged();
		}
	}
}
