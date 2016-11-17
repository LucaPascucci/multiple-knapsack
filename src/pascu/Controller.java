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
    private List<Double> valueOfItems;
    private double[][] weightOfItems;
    private Double optimumValue;

    public Controller(final View view){
        this.view = view;
    }

    public void loadDataCmd(final String path) {

        this.knapsacksVolume = new ArrayList<>();
        this.valueOfItems = new ArrayList<>();

        String line;
        String[] splittedLine;
        BufferedReader in;
        int knapsacks;
        int items;
        int temp_item;
        int temp_knap;

        try {
            in = new BufferedReader(new FileReader(path));
            line = in.readLine();
            splittedLine = line.split(REGEX);
            knapsacks = Integer.parseInt(splittedLine[0]);
            items = Integer.parseInt(splittedLine[1]);
            this.weightOfItems = new double[knapsacks][items];

            //Lettura valori degli oggetti
            temp_item = 0;
            while (temp_item < items){
                line = in.readLine();
                splittedLine = line.split(REGEX);
                for (String s: splittedLine){
                    this.valueOfItems.add(Double.parseDouble(s));
                    temp_item++;
                }
            }

            //Lettura pesi degli zainetti
            temp_knap = 0;
            while (temp_knap < knapsacks){
                line = in.readLine();
                splittedLine = line.split(REGEX);
                for (String s: splittedLine){
                    this.knapsacksVolume.add(Double.parseDouble(s));
                    temp_knap++;
                }
            }

            //Lettura pesi degli oggetti
            temp_knap = 0;
            temp_item = 0;
            while (temp_knap < knapsacks){
                line = in.readLine();
                splittedLine = line.split(REGEX);
                for (String s: splittedLine){
                    this.weightOfItems[temp_knap][temp_item] = Double.parseDouble(s);
                    temp_item++;
                }
                if (temp_item == items){
                    temp_item = 0;
                    temp_knap++;
                }
            }

            //Lettura valore ottimo
            while ((line = in.readLine()) != null) {
                if (!line.isEmpty()){
                    this.optimumValue = Double.parseDouble(line);
                }
            }

            in.close();
            this.view.resetTextArea();
            this.view.changeButtonsState(true);
            this.view.showInfoMessage("Istanza caricata correttamente, scegliere un algoritmo per risolverla.");
        } catch (IOException e) {
            e.printStackTrace();
            this.view.showErrorMessage("Errore nel caricamento dell'istanza");
        }
        this.printData();
        this.view.setSaveEnabled(false);
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
            //new ACOAlgorithm(this.view, this.knapsacksVolume, this.weightOfItems, this.valueOfItems).start();
        } else {
            new GeneticAlgorithm(this.view, this.knapsacksVolume, this.weightOfItems, this.valueOfItems, this.optimumValue).start();
        }
    }

    private void printData(){
        this.view.resetTextArea();
        this.view.appendText("Optimum Value: " + this.optimumValue + NEW_LINE + NEW_LINE);

        this.view.appendText("Knapsacks:");
        for (int i = 0; i < this.knapsacksVolume.size(); i++){
            this.view.appendText(NEW_LINE + (i + 1) + ") Volume: " + this.knapsacksVolume.get(i));
        }

        this.view.appendText(NEW_LINE + NEW_LINE + "Items:");
        for (int i = 0; i < this.valueOfItems.size(); i++){
            String temp_weights = "";
            for (int j = 0; j < this.knapsacksVolume.size(); j++) {
                temp_weights += "" + this.weightOfItems[j][i] + " | ";
            }
            this.view.appendText(NEW_LINE + (i + 1) + ") Value: " + this.valueOfItems.get(i) + " - Weights: " + temp_weights);
        }
    }

    public void updateDataWeka(final String path) {

        BufferedReader in;
        PrintWriter writer;
        String line;

        try {
            writer = new PrintWriter(path + ".txt", "UTF-8");
            in = new BufferedReader(new FileReader(path));

            line = in.readLine();
            line += NEW_LINE;
            writer.print(line);
            while ((line = in.readLine()) != null) {
                String value = "";
                if (line.endsWith("2")){
                    value = "b";
                }else if (line.endsWith("4")){
                    value = "m";
                }
                String temp = line.substring(0,line.length()-1);
                temp += value;
                //String temp = line.substring(line.indexOf(",") + 1);
                //String temp = line.substring(0,line.indexOf(";"));

                temp += NEW_LINE;
                writer.print(temp);
            }

            in.close();
            writer.close();


        } catch (IOException e) {
            this.view.showErrorMessage("Errore nel salvataggio");

        }
    }
}
