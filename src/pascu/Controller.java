package pascu;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  Luca Pascucci
 * @version 1.0
 * @since   2016-10-16
 */

public class Controller {

    private static final String NEW_LINE = "\n";
    private static final String REGEX = "\\W+";
    private View view;
    private List<Double> knapsacksVolume;
    private List<Double> weightOfItems;
    private List<Double> valueOfItems;

    //TODO stampare l'istanza caricata
    public Controller(final View view){
        this.view = view;
    }

    public void loadDataCmd(final String path) {

        this.knapsacksVolume = new ArrayList<>();
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
                this.knapsacksVolume.add(Double.parseDouble(s));
            }
            while ((line = in.readLine()) != null) {
                splittedLine = line.split(REGEX);
                this.weightOfItems.add(Double.parseDouble(splittedLine[0]));
                this.valueOfItems.add(Double.parseDouble(splittedLine[1]));
            }
            in.close();
            this.view.resetTextArea();
            this.view.changeButtonsState(true);
            this.view.showInfoMessage("Istanza caricata correttamente, scegliere metodo per risolverla.");
        } catch (IOException e) {
            e.printStackTrace();
            this.view.showErrorMessage("Errore nel caricamento dell'istanza");
        }
        this.printData();
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

    public void startAlgorithm(final boolean value){
        this.view.resetTextArea();
        this.view.changeButtonsState(false);
        if (value) {
            new ACOAlgorithm(this.view, this.knapsacksVolume, this.weightOfItems, this.valueOfItems).start();
        } else {
            new GeneticAlgorithm(this.view, this.knapsacksVolume, this.weightOfItems, this.valueOfItems).start();
        }
    }

    private void printData(){
        this.view.resetTextArea();
        this.view.appendText("Knapsacks:");
        for (int i = 0; i < this.knapsacksVolume.size(); i++){
            this.view.appendText(NEW_LINE + (i + 1) + ") Volume: " + this.knapsacksVolume.get(i));
        }
        this.view.appendText(NEW_LINE + NEW_LINE + "Items:");
        for (int i = 0; i < this.valueOfItems.size(); i++){
            this.view.appendText(NEW_LINE + (i + 1) + ") Weight: " + this.weightOfItems.get(i) + " - Value: " + this.valueOfItems.get(i));
        }
    }

}
