package pascu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 *
 * @author  Luca Pascucci
 * @version 1.0
 * @since   2016-10-16
 */
public class GeneticAlgorithm extends Thread{

    private static final String NEW_LINE = "\n";

    private View view;
    private List<Double> knapsacksVolume;
    private List<Double> weightOfItems;
    private List<Double> valueOfItems;

    private int numberGenerations = 20;
    private int numberItems;
    private List<String> population;
    private List<Double> populationFitness;
    private int generationCount = 0;
    private double generationFitness;

    public GeneticAlgorithm(final View view, final List<Double> knapsacksVolume, final List<Double> weightOfItems, final List<Double> valueOfItems){
        super("Genetic Algorithm Thread");
        this.view = view;
        this.knapsacksVolume = new ArrayList<>(knapsacksVolume);
        this.weightOfItems = new ArrayList<>(weightOfItems);
        this.valueOfItems = new ArrayList<>(valueOfItems);
        this.numberItems = this.weightOfItems.size();
        this.population = new ArrayList<>();
        this.populationFitness = new ArrayList<>();
    }

    @Override
    public void run() {
        super.run();
        this.view.appendText(this.getName() + NEW_LINE + NEW_LINE);

        //inizializza la popolazione randomicamente
        this.initPopulation();
        this.evalutatePopulation();

        this.printGeneration(this.generationCount);
        this.generationCount++;


        //TODO ciclo per le generazioni future
        for (;this.generationCount < this.numberGenerations; this.generationCount++){
            //TODO ciclo GENETICO
        }

        //riattivo i bottoni nella view
        this.view.changeButtonsState(true);

    }

    /**
     * Inizializzo la popolazione formata composta da 10 cromosomi
     */
    private void initPopulation(){
        int populationSize = 10;
        for (int i = 0; i < populationSize; i++){
            this.population.add(this.generateChromosome());
        }
    }

    /**
     * Genera un cromosoma grande come la lista degli elementi
     * I valori dei singoli geni sono compresi tra 0 e N° knapsack (generati random)
     @return il cromosoma prodotto.
     */
    private String generateChromosome(){

        StringBuilder chromosome = new StringBuilder(this.numberItems);

        new Random().ints(this.numberItems, 0, this.knapsacksVolume.size() + 1).forEach(n -> chromosome.append(n));

        return chromosome.toString();
    }

    /**
     * Valuta la fitness della popolazione
     */
    private void evalutatePopulation(){

        this.generationFitness = 0;
        for (String chromosome: this.population) {
            double chromosomeFitness = this.calculateFitness(chromosome);
            this.populationFitness.add(chromosomeFitness);
            this.generationFitness += chromosomeFitness;
        }
    }

    /**
     * Valuta la fitness di una soluzione (cromosoma) considerando spazio disponibile nel i-esimo zaino e valore dell'oggetto
     * @param chromosome - Cromosoma da valutare
     * @return  il valore di fitness
     */

    private double calculateFitness(String chromosome){

        double fitnessValue = 0;
        List<Double> freeVolume = new ArrayList<>(this.knapsacksVolume);
        int knapsack;

        //Riduce lo spazio disponibile negli zainetti in base agli oggetti inseriti e calcola la fitness totale
        for (int i = 0; i < this.numberItems; i ++){
            knapsack = Character.getNumericValue(chromosome.charAt(i));
            if (knapsack > 0) {
                freeVolume.add(knapsack - 1,(freeVolume.get(knapsack - 1) - this.weightOfItems.get(i)));
                fitnessValue += this.valueOfItems.get(i);
            }
        }

        for (Double volume: freeVolume){
            if (volume < 0) {
                return 0;
            }
        }
        return fitnessValue;

    }

    private double populationFitnessAVG(){

        return 0;
    }

    private int montecarlo(){
        return 0;
    }

    private void crossover(){

    }

    private void mutation(){

    }

    private void printGeneration(int generation){

        if (generation == 0) {
            this.view.appendText("Generazione iniziale:" + NEW_LINE + NEW_LINE);
        } else {
            this.view.appendText("Generazione " + generation + ":" + NEW_LINE + NEW_LINE);
        }

        this.view.appendText("Popolazione:");
        for(int i = 0; i < this.population.size(); i++) {
            this.view.appendText(NEW_LINE + (i + 1) + ") " + this.population.get(i));
        }

        this.view.appendText(NEW_LINE + NEW_LINE + "Fitness:");
        for(int i = 0; i < this.population.size(); i++) {
            this.view.appendText(NEW_LINE + (i + 1) + " - " + this.populationFitness.get(i));
        }
    }

}
