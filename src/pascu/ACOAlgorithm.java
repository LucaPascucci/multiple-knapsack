package pascu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Pascucci
 * @version 1.0
 * @since 2016-10-17
 */
public class ACOAlgorithm extends Thread {

	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";
	//numero iterazioni
	private static final int GENERATIONS = 30;

	private View view;
	private List<Double> knapsacksVolume;
	private double[][] weightOfItems;
	private List<Double> valueOfItems;
	private double optimumValue;
	private int nItems;

	//per calcolare l'attrattività e la traccia
	private double alpha = 0.5;
	private double beta = 0.5;

	//variabile dell'evaporazione
	private double rho = 0.9;

	//numero formiche
	private int nAnts;

	/*
		 Tau = desiderabilità a posteriori (soluzione migliore precedente)
		 Eta = desiderabilità a priori (soluzione migliore )
	 */
	private double[][] eta;
	private double[][] tau;

	private int[][] sol;
	private int[] costs;
	private int maxValue;
	private List<Integer> bestSol;


	public ACOAlgorithm(final View view, final List<Double> knapsaksCapacity, final double[][] weightOfItems, final List<Double> valueOfItems, final double optimumValue) {
		super("ACO Thread");
		this.view = view;

		this.knapsacksVolume = new ArrayList<>(knapsaksCapacity);

		this.valueOfItems = new ArrayList<>();
		this.valueOfItems.add(0.0); //aggiungo un oggetto fittizio da cui partire per costruire ogni mia soluzione
		this.valueOfItems.addAll(valueOfItems);

		this.nItems = this.valueOfItems.size();
		this.nAnts = this.nItems - 1; //numero di formiche uguale al numero di elementi reali

		//aggiungo un oggetto fittizio da cui partire per costruire ogni mia soluzione
		this.weightOfItems = new double[this.knapsacksVolume.size()][this.nItems];
		for (int i = 0; i < this.nItems; i++) {
			for (int j = 0; j < this.knapsacksVolume.size(); j++) {
				if (i == 0) {
					this.weightOfItems[j][i] = 0.0;
				} else {
					this.weightOfItems[j][i] = weightOfItems[j][i - 1];
				}
			}
		}

		this.optimumValue = optimumValue;

		this.eta = new double[this.nItems][this.nItems];
		this.tau = new double[this.nItems][this.nItems];

		this.maxValue = 0;
		this.bestSol = new ArrayList<>();
	}

	@Override
	public void run() {
		super.run();
		this.view.appendText("" + this.getName() + NEW_LINE + NEW_LINE);

		this.initialize();
		this.antSearch();
		this.view.appendText(NEW_LINE + "Migliore soluzione trovata: ");
		for (int object : this.bestSol) {
			this.view.appendText(" " + object);
		}
		this.view.appendText(" -> Best Value : " + this.maxValue);
		this.view.appendText(NEW_LINE + "Migliore soluzione conosciuta: " + this.optimumValue);
		this.view.setSaveEnabled(true);
		this.view.changeButtonsState(true);
	}

	/**
	 * Inizializzazione di tau ed eta.
	 */
	private void initialize() {

		double max = 0.0;

		double ratio1;
		double ratio2;
		for (int i = 0; i < this.nItems; i++) {
			ratio1 = i == 0 ? 0 : (this.valueOfItems.get(i) / getDeltaOfItems(i)) / this.knapsacksVolume.size();
			for (int j = 0; j < this.nItems; j++) {
				ratio2 = i == 0 ? 0 : (this.valueOfItems.get(j) / getDeltaOfItems(j)) / this.knapsacksVolume.size();
				if (i != j) {
					this.eta[i][j] = Math.abs(ratio1 - ratio2);
					if (this.eta[i][j] > max) {
						max = this.eta[i][j];
					}
				} else {
					this.eta[i][j] = 0;
				}
			}
		}

		double tau0 = max * 2;

		//Inizializzo tau
		for (int i = 0; i < this.nItems; i++) {
			for (int j = 0; j < this.nItems; j++) {
				//this.tau[i][j] = (1.0 / this.nItems);
				this.tau[i][j] = tau0;
				this.eta[i][j] = max - this.eta[i][j];
			}
		}
	}

	private double getDeltaOfItems(int item) {
		double delta = 0.0;
		for (int knap = 0; knap < this.knapsacksVolume.size(); knap++) {
			double tmp = this.weightOfItems[knap][item] / this.knapsacksVolume.get(knap);
			delta += tmp;
		}
		return delta;
	}

	/**
	 * Avvia ricerca
	 */
	private void antSearch() {

		this.sol = new int[this.nItems][this.nItems];
		this.costs = new int[this.nItems];

		for (int i = 0; i < GENERATIONS; i++) {
			if (i != 0) {
				this.view.appendText(NEW_LINE);
			}
			this.view.appendText("Iterazione: " + (i + 1) + TAB + "Valore" + TAB + "Peso");

			for (int ant = 0; ant < this.nAnts; ant++) {
				this.view.appendText(NEW_LINE + "Formica " + (ant + 1) + ": " + TAB);
				this.costs[ant] = constructSol(this.sol[ant]);
			}
			this.view.appendText(NEW_LINE);

			this.updateTau(this.costs, this.sol);
		}

	}

	/**
	 * Metodo per costruire una singola soluzione, basandosi sulla traccia tau e sulla attrattività eta.
	 */
	private int constructSol(final int[] sol) {

		int z = 0;
		double[] currentWeight = new double[this.knapsacksVolume.size()];
		boolean[] added = new boolean[this.nItems]; //array che indica quali oggetti ho aggiunto
		boolean[] check = new boolean[this.nItems]; //array che indica quali oggetti ho provato a aggiungere
		double[] val = new double[this.nItems];

		//Inizializzo peso della soluzione corrente
		for (int knap = 0; knap < this.knapsacksVolume.size(); knap++) {
			currentWeight[knap] = 0.0;
		}

		//inizializzo elementi da inserire ed inseriti
		for (int i = 0; i < this.nItems; i++) {
			added[i] = false;
			check[i] = false;
		}

		sol[0] = 0;
		added[0] = true;
		check[0] = true;

		for (int i = 1; i < this.nItems; i++) {
			for (int j = 0; j < this.nItems; j++) {
				if (check[j]) {
					val[j] = 0;
				} else {
					val[j] = Math.pow(this.eta[sol[i]][j], this.alpha) * Math.pow(this.tau[sol[i]][j], this.beta);
				}
			}

			int k = this.montecarlo(val);
			check[k] = true; //imposto l'elemento k come provato ad aggiungere

			//se il peso totale col nuovo oggetto non sfora la capacità degli zaini, lo aggiungo, altrimenti no
			if (this.checkAdmissibility(k, currentWeight)) {
				sol[i] = k;
				added[k] = true;
				z += this.valueOfItems.get(k);
				//aggiorno il peso occupato dagli oggetti presi
				for (int knap = 0; knap < this.knapsacksVolume.size(); knap++) {
					currentWeight[knap] += this.weightOfItems[knap][k];
				}
			}
		}

		//se ho trovato una soluzione globale migliore, la aggiorno con quella trovata
		if (z > this.maxValue) {
			this.maxValue = z;
			this.bestSol.clear();
			for (int i = 1; i < added.length; i++) {
				if (added[i]) {
					this.bestSol.add(i);
				}
			}
		}

		this.view.appendText("" + z);
		for (int i = 0; i < this.knapsacksVolume.size(); i++) {
			this.view.appendText(TAB + currentWeight[i]);
		}
		/*this.view.appendText(NEW_LINE + "SOL: ");
		for (int v : sol) {
			this.view.appendText(" " + v);
		}
		this.view.appendText(NEW_LINE + "ADDED: ");
		for (int i = 1; i < added.length; i++) {
			if (added[i]) {
				this.view.appendText(" " + i);
			}
		}*/
		return z;
	}

	private boolean checkAdmissibility(int object, double[] currentWeight) {

		for (int knap = 0; knap < this.knapsacksVolume.size(); knap++) {
			if ((currentWeight[knap] + this.weightOfItems[knap][object]) > this.knapsacksVolume.get(knap)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Selezione probabilistica con Roulette Wheel Selection (montecarlo).
	 */
	private int montecarlo(double[] val) {

		double sum = 0;
		for (double tmp : val) {
			sum += tmp;
		}

		double rand = Math.random() * sum;
		sum = 0;
		for (int i = 1; i < val.length; i++) {
			sum += val[i];
			if (sum >= rand) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Aggiornamento della traccia tau.
	 */
	private void updateTau(int[] costs, int[][] sol) {

		//Aggiorno tau considerando l'evaporazione
		for (int i = 0; i < this.nItems; i++) {
			for (int j = 0; j < this.nItems; j++) {
				this.tau[i][j] *= this.rho;
			}
		}

		//Aggiorno tau
		for (int i = 0; i < this.nAnts; i++) {
			for (int j = 0; j < this.nItems - 1; j++) {
				this.tau[sol[i][j]][sol[i][j + 1]] += 1.0 / costs[i];
			}
		}
	}
}
