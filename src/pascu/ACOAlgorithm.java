package pascu;

import java.util.ArrayList;
import java.util.List;

/**
 * @author  Luca Pascucci
 * @version 1.0
 * @since   2016-10-17
 */
public class ACOAlgorithm extends Thread {

    private View view;
    private List<Double> knapsaksCapacity;
    private List<Double> weightOfItems;
    private List<Double> valueOfItems;

    //per calcolare l'attrattività e la traccia
    private double alpha = 0.5;
    private double beta = 0.5;

    //variabile dell'evaporazione
    private double rho = 0.9;

    //numero formiche
    private int nAnts = 20;

    /*
         Tau è la desiderabilità a posteriori (soluzione migliore precedente)
         Eta è la desiderabilità ad anteriori (soluzione migliore )
     */

    public ACOAlgorithm (final View view, final List<Double> knapsaksCapacity, final List<Double> weightOfItems, final List<Double> valueOfItems) {
        super("ACO Thread");
        this.view = view;

        this.valueOfItems.add(0.0); //aggiungo un oggetto fittizio da cui partire per costruire ogni mia soluzione
        this.valueOfItems.addAll(valueOfItems);

        this.weightOfItems.add(0.0);
        this.weightOfItems.addAll(weightOfItems);

        this.knapsaksCapacity = new ArrayList<>(knapsaksCapacity);
    }

    @Override
    public void run() {
        super.run();
        this.view.appendText("" + this.getName());



        this.view.setSaveEnabled(true);
        this.view.changeButtonsState(true);
    }
}
