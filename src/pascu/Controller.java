package pascu;

import java.io.*;

/**
 * Created by Luca on 13/09/16.
 */
public class Controller {

    private View view;

    public Controller(View view){
        this.view = view;
    }

    public void loadDataCmd(String path) {

        String line;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(path));
            line = in.readLine();
            while ((line = in.readLine()) != null) {

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

    public void saveDataCmd(String path) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(path + ".txt", "UTF-8");
            writer.print(this.view.getText());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            this.view.showErrorMessage("Errore nel salvataggio");

        }
    }

    public void startAlgoritm(boolean value){
        this.view.resetTextArea();
        new Thread(()->{
            this.view.changeButtonsState(false);
            if (value){
                //ale.ACO
            } else {
                //GA
            }
            this.view.changeButtonsState(true);
        }).start();
    }

}
