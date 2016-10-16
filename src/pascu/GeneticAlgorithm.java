package pascu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 13/09/16.
 */
public class GeneticAlgorithm extends Thread{

    private View view;
    private List<Double> knapsaksCapacity;
    private List<Double> weightOfItems;
    private List<Double> valueOfItems;

    public GeneticAlgorithm(final View view, final List<Double> knapsaksCapacity, final List<Double> weightOfItems, final List<Double> valueOfItems){
        super("GA Thread");
        this.view = view;
        this.knapsaksCapacity = new ArrayList<>(knapsaksCapacity);
        this.weightOfItems = new ArrayList<>(weightOfItems);
        this.valueOfItems = new ArrayList<>(valueOfItems);
    }

    @Override
    public void run() {
        super.run();
        this.view.appendText("" + this.getName());
        this.view.changeButtonsState(true);
    }


}
