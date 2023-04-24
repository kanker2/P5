package view.server;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import common.Observable;
import common.Observer;
import server.Server;

public class FilesInfoPanel extends JPanel implements Observer{

	private JTable chart;
	private DefaultTableModel model;
	
	public FilesInfoPanel(Server s) {
		super(new BorderLayout());
		
		s.addObserver(this);
		
		model = new DefaultTableModel(new Object[] {"Nombre archivo"}, 0);
		chart = new JTable(model);
		
		this.add(new JLabel("Archivos en el sistema"), BorderLayout.NORTH);
		this.add(new JScrollPane(chart), BorderLayout.CENTER);
	}
	
	private void updateFiles(Server s) {
		Set<String> downloadableFiles = s.getDownloadableFiles();
		model.setRowCount(0);
		for(String fileName : downloadableFiles) 
			model.addRow(new Object[] {fileName});
		model.fireTableDataChanged();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof String && ((String) arg == "add_file" || (String) arg == "removed_connection"))
			updateFiles((Server) o);
	}
}
