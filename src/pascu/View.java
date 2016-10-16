package pascu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Luca on 13/09/16.
 */
public class View extends JFrame implements ActionListener{

    private Controller controller;
    private JTextArea textArea = new JTextArea(42,50);
    private JFileChooser fileChooser;
    private JButton startACObtn = new JButton("Esegui Algoritmo ACO");
    private JButton startGAbtn = new JButton("Esegui Algoritmo Genetico");
    private JButton loadbtn = new JButton("Carica istanza");


    //TODO aggiungere bottone salvataggio
    public View (){

        this.setSize(650,750);
        this.setResizable(false);
        this.setTitle("Multiple Knapsack Problem Solver");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.startACObtn.setEnabled(false);
        this.startGAbtn.setEnabled(false);
        this.startACObtn.addActionListener(this);
        this.startGAbtn.addActionListener(this);
        this.loadbtn.addActionListener(this);

        this.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.add(this.loadbtn);
        controlPanel.add(this.startGAbtn);
        controlPanel.add(this.startACObtn);

        this.textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.textArea);

        JPanel container = new JPanel();
        container.add(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.add(controlPanel,BorderLayout.PAGE_START);
        this.add(container,BorderLayout.CENTER);

        this.fileChooser = new JFileChooser();
        final FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "txt");
        this.fileChooser.setFileFilter(filter);

        this.textArea.append("Caricare un'istanza");

    }

    public void appendText(String text){
        SwingUtilities.invokeLater(()->
            this.textArea.append(text)
        );
    }

    public void resetTextArea(){
        SwingUtilities.invokeLater(()->
            this.textArea.setText(null)
        );
    }

    public String getText(){
        return this.textArea.getText();
    }

    public void changeButtonsState(boolean value){
        SwingUtilities.invokeLater(()-> {
            this.startACObtn.setEnabled(value);
            this.startGAbtn.setEnabled(value);
        });

    }

    public void attachObserver (Controller controller){
        this.controller = controller;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(this.loadbtn)){
            if (this.fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.controller.loadDataCmd(this.fileChooser.getSelectedFile().getPath());
            }

        }

        if (e.getSource().equals(this.startACObtn)){
            this.controller.startAlgorithm(true);
        }

        if (e.getSource().equals(this.startGAbtn)){
            this.controller.startAlgorithm(false);
        }

        //Salvataggio risultato
        /*if (this.fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.controller.saveDataCmd(this.fileChooser.getSelectedFile().getPath());
        }*/

    }

    public void showInfoMessage(String message){
        SwingUtilities.invokeLater(()->
                JOptionPane.showMessageDialog(this,message,"Info",JOptionPane.INFORMATION_MESSAGE)
        );
    }

    public void showErrorMessage(String message){
        SwingUtilities.invokeLater(()->
            JOptionPane.showMessageDialog(this,message,"Errore",JOptionPane.ERROR_MESSAGE)
        );
    }
}
