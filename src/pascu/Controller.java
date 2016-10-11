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
    private GeneticAlgorithm geneticAlgorithm;
    private ACOAlgorithm acoAlgorithm;
    //TODO mettere le liste qua

    public Controller(final View view){
        this.view = view;
    }

    public void loadDataCmd(final String path) {

        List<Double> knapsaksCapacity = new ArrayList<>();
        List<Double> weightOfItems = new ArrayList<>();
        List<Double> valueOfItems = new ArrayList<>();

        String line;
        String[] splittedLine;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(path));
            line = in.readLine();
            splittedLine = line.split(REGEX);
            for (String s: splittedLine){
                knapsaksCapacity.add(Double.parseDouble(s));
            }
            while ((line = in.readLine()) != null) {
                splittedLine = line.split(REGEX);
                weightOfItems.add(Double.parseDouble(splittedLine[0]));
                valueOfItems.add(Double.parseDouble(splittedLine[0]));
            }
            in.close();
            this.view.resetTextArea();
            this.view.changeButtonsState(true);
            this.view.showInfoMessage("Istanza caricata correttamente, scegliere metodo per risolverla.");
        } catch (IOException e) {
            e.printStackTrace();
            this.view.showErrorMessage("Errore nel caricamento dell'istanza");
        }

        this.geneticAlgorithm = new GeneticAlgorithm(this.view, knapsaksCapacity, weightOfItems, valueOfItems);
        this.acoAlgorithm = new ACOAlgorithm(this.view, knapsaksCapacity, weightOfItems, valueOfItems);
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
        //TODO sistemare il riavvio degli algoritmi
        if (value) {
            //TODO istanziare ACO ed avviarlo
            if (this.acoAlgorithm != null && !this.acoAlgorithm.isAlive()){
                this.view.changeButtonsState(false);
                this.acoAlgorithm.start();
            }
        } else {
            //TODO istanziare GA ed avviarlo
            if (this.geneticAlgorithm != null && !this.geneticAlgorithm.isAlive()){
                this.geneticAlgorithm.start();
            }
        }
    }

}
