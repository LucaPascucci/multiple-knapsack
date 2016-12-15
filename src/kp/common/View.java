package kp.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class View extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final int EXIT_IDX = 2;	
	private static final int SAVE_IDX = 1;
	private static final int LOAD_IDX = 0;
	private IViewObserver observer;
	private JButton startGABtn = new JButton("Esegui Algoritmo Genetico");
	private JButton startACOBtn = new JButton("Esegui ACO");
	private JTextArea textArea = new JTextArea(45,50);
	private final JMenuBar menuBar;
	private final JMenu optionMenu;
	private final JMenuItem[] optionMenuItems;
	private final JFileChooser fileChooser;

	public View() {
		this.setSize(650, 850);
		this.setResizable(false);
		this.setTitle("Knapsack Problem Solver");
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2, 
				(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);

		this.startGABtn.setEnabled(false);
		this.startGABtn.addActionListener(this);
		this.startACOBtn.setEnabled(false);
		this.startACOBtn.addActionListener(this);

		this.optionMenu = new JMenu("Opzioni");
		this.optionMenuItems = new JMenuItem[3];
		this.optionMenuItems[0] = new JMenuItem("Carica");
		this.optionMenuItems[1] = new JMenuItem("Salva");
		this.optionMenuItems[2] = new JMenuItem("Esci");

		for (int i = 0; i < 3; i++) {
			this.optionMenuItems[i].addActionListener(this);
		}

		for (int i = 0; i < 3; i++) {
			this.optionMenu.add(this.optionMenuItems[i]);
		}

		this.menuBar = new JMenuBar();
		this.menuBar.add(this.optionMenu);
		this.setJMenuBar(this.menuBar);

		LayoutManager layout = new BorderLayout();
		this.setLayout(layout);

		JPanel controlPanel = new JPanel();
		controlPanel.add(this.startGABtn);
		controlPanel.add(this.startACOBtn);

		this.textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(this.textArea);

		JPanel container = new JPanel();       
		container.add(scrollPane);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		this.add(controlPanel,BorderLayout.NORTH);
		this.add(container,BorderLayout.CENTER);

		this.fileChooser = new JFileChooser();
		final FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "txt");
		this.fileChooser.setFileFilter(filter);

		this.textArea.append("Caricare un'istanza dal menu Opzioni."
				+ "\nL'opzione salva permette di salvare su file di testo tutto cio che è presente in questa text area.");
	}

	public void attachObserver(IViewObserver observer) {
		this.observer = observer;
	}

	public void appendText(String text) {
		SwingUtilities.invokeLater(() -> {
			this.textArea.append(text);
		});
	}

	public void resetTxt() {
		SwingUtilities.invokeLater(() -> {
			this.textArea.setText(null);
		});
	}

	public String getText() {
		return this.textArea.getText();
	}

	public void enableButtons(boolean b) {
		this.startGABtn.setEnabled(b);
		this.startACOBtn.setEnabled(b);
		for (int i = 0; i < 2; i++) {
			this.optionMenuItems[i].setEnabled(b);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Object source = e.getSource();
		if (source.equals(this.optionMenuItems[EXIT_IDX])) {
			this.observer.exitCmd(); 
		} else if (source.equals(this.optionMenuItems[SAVE_IDX])) {
			if (this.fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.observer.saveDataCmd(this.fileChooser.getSelectedFile().getPath());
			}
		} else if (source == this.optionMenuItems[LOAD_IDX]) {
			if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.observer.loadDataCmd(this.fileChooser.getSelectedFile().getPath());
			}
		} else if (source.equals(this.startGABtn)) {
			this.observer.startGACmd();
		} else if (source.equals(this.startACOBtn)) {
			this.observer.startACOCmd();
		}

	}

	public interface IViewObserver {

		void startGACmd();
		
		void startACOCmd();

		void exitCmd();

		void saveDataCmd(final String path);

		void loadDataCmd(final String path);
	}

}
