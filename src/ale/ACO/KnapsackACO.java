package ale.ACO;

import java.util.ArrayList;
import java.util.List;
import ale.common.View;

public class KnapsackACO {

	private double alpha = 0.5;
	private double beta = 0.5;
	private double rho = 0.9;
	private int nAnts;
	private int nIters = 20;
	private int maxValue;
	private List<Integer> bestSol;
	private double[][] eta;
	private double[][] tau;
	private int[][] sol;
	private int[] costs;
	private double max;
	private double knapsackCapacity;
	private List<Double> valueOfItems;
	private List<Double> weightOfItems;
	private int nItems;
	private View view;

	public KnapsackACO(final int nItems, final double knapsackCapacity, final List<Double> weightOfItems, final List<Double> valueOfItems, View view) {
		this.valueOfItems = new ArrayList<>();
		this.weightOfItems = new ArrayList<>();
		this.valueOfItems.add(0.0); //aggiungo un oggetto fittizio da cui partire per costruire ogni mia soluzione
		this.weightOfItems.add(0.0);
		this.nItems = nItems + 1;
		this.nAnts = nItems;
		this.knapsackCapacity = knapsackCapacity;
		this.weightOfItems.addAll(weightOfItems);
		this.valueOfItems.addAll(valueOfItems);
		this.view = view;
		this.bestSol = new ArrayList<>();
		this.eta = new double[this.nItems][this.nItems];
		this.tau = new double[this.nItems][this.nItems];
		this.max = 0;

		this.initialize();
		this.antSearch();
		this.view.appendText("\nMigliore soluzione trovata: ");
		for (int object : this.bestSol) {
			this.view.appendText(" " + object);
		}
		this.view.appendText("\nValore della migliore soluzione trovata: " + Integer.toString(this.maxValue));
	}
	
	/**
	 * Inizializzazione di tau ed eta.
	 */
	private void initialize() {

		double ratio1;
		double ratio2;
		for (int i = 0; i < this.nItems; i++) {
			ratio1 = i == 0 ? 0 : this.valueOfItems.get(i) / this.weightOfItems.get(i) / this.knapsackCapacity;
			for (int j = 0; j < this.nItems; j++) {
				if (i != j) {
					ratio2 = j == 0 ? 0 : this.valueOfItems.get(j) / this.weightOfItems.get(j) / this.knapsackCapacity;
					this.eta[i][j] = Math.abs(ratio1 - ratio2);
					if (this.eta[i][j] > this.max) {
						this.max = this.eta[i][j];
					}
				} else {
					this.eta[i][j] = 0;
				}
			}
		}

		double tau0 = this.max * 2;

		for (int i = 0; i < this.nItems; i++) {
			for (int j = 0; j < this.nItems; j++) {
				this.tau[i][j] = tau0;
				this.eta[i][j] = this.max - this.eta[i][j];
			}
		}

	}

	private void antSearch() {
		this.sol = new int[this.nItems][];
		this.costs = new int[this.nItems];
		for (int i = 0; i < this.nItems; i++) {
			this.sol[i] = new int[this.nItems];
		}
		for (int iter = 1; iter <= this.nIters; iter++) {
			if (iter != 1) {
				this.view.appendText("\n");
			}
			this.view.appendText("Iterazione: " + iter + "\tValore\tPeso" );
			for (int k = 0; k < this.nAnts; k++) {
				int counter = k + 1;
				this.view.appendText("\nFormica " + counter + ": " + "\t");
				this.costs[k] = constructSol(this.sol[k]);
				if (counter == this.nAnts) {
					this.view.appendText("\n");
				}
			}
			this.updateTau(this.costs, this.sol);
		}
	}
	
	/**
	 * Metodo per costruire una singola soluzione, basandosi sulla traccia tau e sulla attrattività eta.
	 */
	private int constructSol(final int[] sol) {
		int z = 0;
		int k;
		double currentWeight = 0;
		boolean[] added = new boolean[this.nItems]; //array che indica quali oggetti ho aggiunto
		boolean[] check = new boolean[this.nItems]; //array che indica quali oggetti ho provato a aggiungere
		double[] val = new double[this.nItems];

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
			
			k = this.montecarlo(val);
			check[k] = true;
			
			//se il peso totale col nuovo oggetto non sfora la capacità dello zaino, lo aggiungo, altrimenti no
			if (currentWeight + this.weightOfItems.get(k) <= this.knapsackCapacity) {
				sol[i] = k;
				added[k] = true;
				z += this.valueOfItems.get(k);
				currentWeight += this.weightOfItems.get(k);
			}
		}
		
		//se ho trovato una soluzione globale migliore, la aggiorno con quella trovata
		if (z > this.maxValue) {
			this.maxValue = z;
			this.bestSol.clear();
			for (int i = 1; i < added.length; i++) {
				if(added[i]) {
					this.bestSol.add(i);
				}
			}
		}
		this.view.appendText(z + "\t" + currentWeight);

		return z;
	}
	
	/**
	 * Selezione probabilistica con Roulette Wheel Selection (montecarlo).
	 */
	private int montecarlo(double[] val){

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
		for (int i = 0; i < this.nItems; i++) {
			for (int j = 0; j < this.nItems; j++) {
				this.tau[i][j] *= this.rho;
			}
		}

		for (int i = 0; i < this.nAnts; i++) {
			for (int j = 0; j < this.nItems - 1; j++) {
				this.tau[sol[i][j]][sol[i][j + 1]] += (double) 1 / costs[i];
			}
		}
	}

}
