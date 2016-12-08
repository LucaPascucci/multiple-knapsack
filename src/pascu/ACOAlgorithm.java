package pascu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luca Pascucci
 * @version 1.0
 * @since 2016-10-17
 */
public class ACOAlgorithm extends Thread {

	private View view;
	private List<Double> knapsacksVolume;
	private double[][] weightOfItems;
	private List<Double> valueOfItems;
	private double optimumValue;

	//per calcolare l'attrattività e la traccia
	private double alpha = 0.5;
	private double beta = 0.5;

	//variabile dell'evaporazione
	private double rho = 0.9;

	//numero formiche
	private int nAnts = 20;

    /*
		 Tau = desiderabilità a posteriori (soluzione migliore precedente)
         Eta = desiderabilità a priori (soluzione migliore )
     */

	public ACOAlgorithm(final View view, final List<Double> knapsaksCapacity, final double[][] weightOfItems, final List<Double> valueOfItems, final double optimumValue) {
		super("ACO Thread");
		this.view = view;

		this.valueOfItems.add(0.0); //aggiungo un oggetto fittizio da cui partire per costruire ogni mia soluzione
		this.valueOfItems.addAll(valueOfItems);

		//this.weightOfItems.add(0.0); //aggiungo un oggetto fittizio da cui partire per costruire ogni mia soluzione
		this.weightOfItems = weightOfItems;

		this.optimumValue = optimumValue;

		this.knapsacksVolume = new ArrayList<>(knapsaksCapacity);
	}

	@Override
	public void run() {
		super.run();
		this.view.appendText("" + this.getName());

		this.initialize();
		this.antSearch();
		this.view.appendText("\nMigliore soluzione trovata: ");


		this.view.appendText("\nMigliore soluzione conosciuta: " + this.optimumValue);
		this.view.setSaveEnabled(true);
		this.view.changeButtonsState(true);
	}


	private void initialize() {

	}

	private void antSearch() {

	}

	/**
	 * Metodo per costruire una singola soluzione, basandosi sulla traccia tau e sulla attrattività eta.
	 */
	private int constructSol(final int[] sol) {
		return 0;
	}

	/**
	 * Selezione probabilistica con Roulette Wheel Selection (montecarlo).
	 */
	private int montecarlo(double[] val) {
		return 0;
	}

	/**
	 * Aggiornamento della traccia tau.
	 */
	private void updateTau(int[] costs, int[][] sol) {

	}
}
