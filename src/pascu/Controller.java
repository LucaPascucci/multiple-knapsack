package pascu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca on 13/09/16.
 */
public class Controller {

    private static final String REGEX = "\\W+";
    private View view;
    private List<Double> knapsaksCapacity;
    private List<Double> weightOfItems;
    private List<Double> valueOfItems;

    public Controller(final View view){
        this.view = view;
    }

    public void loadDataCmd(final String path) {

        this.knapsaksCapacity = new ArrayList<>();
        this.weightOfItems = new ArrayList<>();
        this.valueOfItems = new ArrayList<>();

        String line;
        String[] splittedLine;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(path));
            line = in.readLine();
            splittedLine = line.split(REGEX);
            for (String s: splittedLine){
                this.knapsaksCapacity.add(Double.parseDouble(s));
            }
            while ((line = in.readLine()) != null) {
                splittedLine = line.split(REGEX);
                this.weightOfItems.add(Double.parseDouble(splittedLine[0]));
                this.valueOfItems.add(Double.parseDouble(splittedLine[0]));
            }
            in.close();
            this.view.resetTextArea();
            this.view.changeButtonsState(true);
            this.view.showInfoMessage("Istanza caricata correttamente, scegliere metodo per risolverla.");
        } catch (IOException e) {
            e.printStackTrace();
            this.view.showErrorMessage("Errore nel caricamento dell'istanza");
        }
        this.view.resetTextArea();
        this.view.changeButtonsState(true);
    }

    public void saveDataCmd(final String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path + ".txt", "UTF-8");
            writer.print(this.view.getText());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            this.view.showErrorMessage("Errore nel salvataggio");

        }
    }

    public void startAlgoritm(final boolean value){
        this.view.resetTextArea();
        this.view.changeButtonsState(false);
        if (value) {
            new ACOAlgorithm(this.view, this.knapsaksCapacity, this.weightOfItems, this.valueOfItems).start();
        } else {
            new GeneticAlgorithm(this.view, this.knapsaksCapacity, this.weightOfItems, this.valueOfItems).start();
        }
    }

}
