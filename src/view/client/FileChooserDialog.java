package view.client;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class FileChooserDialog extends JDialog {

    private JFileChooser fileChooser;
    private File selectedFile;

    public FileChooserDialog(JFrame parent) {
        super(parent, "Seleccionar archivo", true);
        setLocationRelativeTo(null);

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(FileChooserDialog.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileChooser.setVisible(false);
            dispose();
        }
    }
    
    public File getSelectedFile() {
    	return selectedFile;
    }
}
