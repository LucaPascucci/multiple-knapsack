package kp.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import kp.ACO.KnapsackACO;
import kp.common.View.IViewObserver;
import kp.genetic.KnapsackGA;

public class Controller implements IViewObserver {

	private static final String EXIT_CONFIRM = "Vuoi veramente uscire dall'applicazione?";
	private final View view;
	private double knapsackCapacity;
	private int nItems;
	private List<Double> weightOfItems = new ArrayList<>();
	private List<Double> valueOfItems = new ArrayList<>();

	public Controller(final View view) {
		this.view = view;
	}

	@Override
	public void startGACmd() {
		this.view.resetTxt();
		new Thread(() -> {
			this.view.enableButtons(false);
			new KnapsackGA(this.nItems, this.knapsackCapacity, this.weightOfItems, this.valueOfItems, this.view);
			this.view.enableButtons(true);
		}).start();
	}
	
	@Override
	public void startACOCmd() {
		this.view.resetTxt();
		new Thread(() -> {
			this.view.enableButtons(false);
			new KnapsackACO(this.nItems, this.knapsackCapacity, this.weightOfItems, this.valueOfItems, this.view);
			this.view.enableButtons(true);
		}).start();
	}

	@Override
	public void exitCmd() {
		final int n = JOptionPane.showConfirmDialog(this.view, EXIT_CONFIRM, "Conferma", JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	@Override
	public void saveDataCmd(String path) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(path + ".txt", "UTF-8");
			writer.print(this.view.getText());
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			JOptionPane.showMessageDialog(this.view, "Errore nel salvataggio", "Errore", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void loadDataCmd(String path) {
		String line;
		BufferedReader in;

		try {
			in = new BufferedReader(new FileReader(path));
			line = in.readLine();
			this.weightOfItems.clear();
			this.valueOfItems.clear();
			this.nItems = 0;
			this.knapsackCapacity = Double.parseDouble(line);
			while ((line = in.readLine()) != null) {
				this.nItems++;
				String[] splittedLine = line.split("\\W+");
				this.weightOfItems.add(Double.parseDouble(splittedLine[0]));
				this.valueOfItems.add(Double.parseDouble(splittedLine[1]));
			}
			in.close();
			this.view.enableButtons(true);
			this.view.resetTxt();
			this.view.appendText("Istanza caricata correttamente, premere uno dei due pulsanti per risolverla.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this.view, "Errore nel caricamento dell'istanza", "Errore", JOptionPane.ERROR_MESSAGE);
		}

	}

}
