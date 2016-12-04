package pascu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author  Luca Pascucci
 * @version 1.0
 * @since   2016-10-16
 */

public class View extends JFrame implements ActionListener{

    private Controller controller;
    private JTextArea textArea = new JTextArea(40,50);
    private JButton startACObtn = new JButton("Esegui Algoritmo ACO");
    private JButton startGAbtn = new JButton("Esegui Algoritmo Genetico");
    private final JMenuItem loadDataset = new JMenuItem("Carica");
    private final JMenuItem saveResult = new JMenuItem("Salva");

    public View (){

        this.setSize(650,750);
        this.setResizable(false);
        this.setTitle("Multiple Knapsack Problem Solver");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        this.loadDataset.addActionListener(this);
        this.saveResult.addActionListener(this);
        this.saveResult.setVisible(false);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(this.loadDataset);
        menuBar.add(this.saveResult);

        this.startACObtn.setEnabled(false);
        this.startGAbtn.setEnabled(false);
        this.startACObtn.addActionListener(this);
        this.startGAbtn.addActionListener(this);

        this.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.add(this.startGAbtn);
        controlPanel.add(this.startACObtn);

        this.textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(this.textArea);

        JPanel container = new JPanel();
        container.add(scrollPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.add(controlPanel,BorderLayout.PAGE_START);
        this.add(container,BorderLayout.CENTER);
        this.setJMenuBar(menuBar);

        this.textArea.append("Caricare un dataset");

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

        if (e.getSource().equals(this.startACObtn)){
            this.controller.startAlgorithm(true);
        }

        if (e.getSource().equals(this.startGAbtn)){
            this.controller.startAlgorithm(false);
        }

        if (e.getSource().equals(this.loadDataset)){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Scegli il dataset da caricare");
            fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.controller.loadDataCmd(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }

        if (e.getSource().equals(this.saveResult)){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Scegli dove salvare il risultato");
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.controller.saveDataCmd(fileChooser.getSelectedFile().getAbsolutePath());
            }
        }

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

    public void setSaveEnabled(boolean value){
        SwingUtilities.invokeLater(()->this.saveResult.setVisible(value));
    }
}
