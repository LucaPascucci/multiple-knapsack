package pascu;

import java.util.*;

/**
 * @author  Luca Pascucci
 * @version 1.0
 * @since   2016-10-16
 */

public class GeneticAlgorithm extends Thread{

    private static final String NEW_LINE = "\n";

    private static final int NUMBER_GENERATIONS = 60;
    private static final double PROB_CROSSOVER = 0.6;
    private static final double PROB_MUTATION = 0.05;

    private View view;
    private List<Double> knapsacksVolume;
    private double[][] weightOfItems;
    private List<Double> valueOfItems;
    private double optimumValue;

    private int numberItems;
    private List<String> population;
    private List<Double> populationFitness;
    private int generationCount;
    private double generationFitness;

    private int crossoverCount;
    private int cloneCount;
    private int mutationCount;
    private List<String> supportPopulation;

    private double bestFitness;
    private String bestChromosome;

    public GeneticAlgorithm(final View view, final List<Double> knapsacksVolume, final double[][] weightOfItems, final List<Double> valueOfItems, final double optimumValue){
        super("Genetic Algorithm Thread");
        this.view = view;
        this.knapsacksVolume = new ArrayList<>(knapsacksVolume);
        this.weightOfItems = weightOfItems;
        this.valueOfItems = new ArrayList<>(valueOfItems);
        this.optimumValue = optimumValue;
        this.numberItems = this.valueOfItems.size();
        this.population = new ArrayList<>();
        this.populationFitness = new ArrayList<>();
        this.generationCount = 0;
        this.supportPopulation = new ArrayList<>();
        this.bestFitness = 0;
        this.bestChromosome = "";
    }

    @Override
    public void run() {
        super.run();
        long startTime = System.currentTimeMillis();
        this.view.appendText(this.getName() + NEW_LINE + NEW_LINE);

        //inizializza la popolazione randomicamente
        this.initPopulation();

        //vauta la fitness della prima popolazione
        this.evalutatePopulation();

        //stampa le informazioni della popolazione iniziale
        this.printGeneration(this.generationCount);
        this.generationCount++;

        for (;this.generationCount <= NUMBER_GENERATIONS; this.generationCount++){

            this.crossoverCount = 0;
            this.cloneCount = 0;
            this.mutationCount = 0;

            for (int i = 0; i < this.population.size()/2; i++){
                this.crossover(this.montecarlo(),this.montecarlo());
            }

            this.mutation();

            //Sostituisce la vecchia popolazione con quella nuova
            this.population = new ArrayList<>(this.supportPopulation);
            this.populationFitness.clear();
            this.supportPopulation.clear();

            //Valuta la fitness della nuova popolazione
            this.evalutatePopulation();

            //stampa le informazioni della nuova generazione
            this.printGeneration(this.generationCount);

        }

        this.showBestResult();
        this.view.appendText(NEW_LINE + "Tempo di esecuzione: " + (System.currentTimeMillis()-startTime) + " ms");

        //riabilita l'utilizzo dei bottoni nella view
        this.view.setSaveEnabled(true);
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
     * I valori dei singoli geni sono compresi tra 0 e 1
     @return il cromosoma prodotto.
     */
    private String generateChromosome(){

        StringBuilder chromosome = new StringBuilder(this.numberItems);

        new Random().ints(this.numberItems, 0, 2).forEach(n -> chromosome.append(n));

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

        for (int i = 0; i < this.numberItems; i++){
            if (Character.getNumericValue(chromosome.charAt(i)) == 1){
                fitnessValue += this.valueOfItems.get(i);
                for (int j = 0; j < freeVolume.size(); j++) {
                    freeVolume.set(j,(freeVolume.get(j) - this.weightOfItems[j][i]));
                }
            }
        }

        //Controlla gli zainetti sono stati riempiti correttamente. Al primo zainetto troppo pieno invalida la fitness
        for (Double volume: freeVolume){
            if (volume < 0) {
                fitnessValue = 0;
            }
        }
        return fitnessValue;

    }

    /**
     * Roulette Wheel Selection (Montecarlo) per selezionare i genitori.
     * @return La posizione del genitore selezionato per la riproduzione.
     */
    private int montecarlo(){

        double rand = Math.random() * this.generationFitness;
        double currSum = 0;

        for (int i = 0; i < this.population.size(); i++){
            if (currSum + this.populationFitness.get(i) >= rand){
                return i;
            } else {
                currSum += this.populationFitness.get(i);
            }
        }
        return 0;
    }

    /**
     * Esegue il crossover tra i due cromosomi in base alla probabilità.
     * Se il crossover non avviene, la nuova generazione avrà i cloni dei due genitori.
     * @param indexFirstParent - Indice primo genitore
     * @param indexSecondParent - Indice secondo genitore
     */
    private void crossover(int indexFirstParent, int indexSecondParent){

        if(Math.random() <= PROB_CROSSOVER) {
            this.crossoverCount++;

            int crossPoint = new Random().nextInt(this.numberItems) + 1;
            String firstChild = this.population.get(indexFirstParent).substring(0, crossPoint) + this.population.get(indexSecondParent).substring(crossPoint);
            String secondChild = this.population.get(indexSecondParent).substring(0, crossPoint) + this.population.get(indexFirstParent).substring(crossPoint);

            this.supportPopulation.add(firstChild);
            this.supportPopulation.add(secondChild);

        } else {
            this.cloneCount++;

            this.supportPopulation.add(this.population.get(indexFirstParent));
            this.supportPopulation.add(this.population.get(indexSecondParent));
        }

    }

    /**
     * Metodo per la mutazione dei geni: scorre i geni di tutti i cromosomi e li cambia in base alla probabilità impostata
     */
    private void mutation(){

        ListIterator<String> iterator = this.supportPopulation.listIterator();
        while (iterator.hasNext()) {
            String chromosome = iterator.next();
            for (int i = 0; i < chromosome.length(); i++) {
                if (Math.random() <= PROB_MUTATION) { //se il numero casuale è minore o uguale della probabilità di mutazione, cambio valore al gene
                    int newgene;
                    if (Character.getNumericValue(chromosome.charAt(i)) == 1){
                        newgene = 0;
                    } else {
                        newgene = 1;
                    }
                    chromosome = chromosome.substring(0, i) +  newgene + chromosome.substring(i + 1);
                    iterator.set(chromosome);
                    this.mutationCount++;
                }
            }
        }

    }

    /**
     * Funzione utilizzata per prelevare il miglior cromosoma di una generazione
     * @return La stringa che rappresenta la migliore soluzione di una generazione
     */
    private String getBestSolutionOfGeneration(){
        double maxFitness = Collections.max(this.populationFitness);
        if (maxFitness != 0){
            for (int i = 0; i < this.populationFitness.size(); i++){
                if (maxFitness == this.populationFitness.get(i)){

                    //Se il risultato è migliore del globale aggiorna la migliore soluzione incontrata
                    if (this.bestFitness < this.populationFitness.get(i)){
                        this.bestFitness = this.populationFitness.get(i);
                        this.bestChromosome = this.population.get(i);
                    }
                    return this.population.get(i);
                }
            }
        }
        return "";
    }

    private void printGeneration(int generation){

        if (generation == 0) {
            this.view.appendText("Generazione iniziale:" + NEW_LINE + NEW_LINE);
        } else {
            this.view.appendText("Generazione " + generation + ":" + NEW_LINE + NEW_LINE);
        }

        //Stampa tutti i cromosomi della popolazione
        this.view.appendText("Popolazione:");
        for(int i = 0; i < this.population.size(); i++) {
            this.view.appendText(NEW_LINE + (i + 1) + ") " + this.population.get(i));
        }

        //Stampa la fitness di ciascun cromosoma
        this.view.appendText(NEW_LINE + NEW_LINE + "Fitness:");
        for(int i = 0; i < this.population.size(); i++) {
            this.view.appendText(NEW_LINE + (i + 1) + " - " + this.populationFitness.get(i));
        }

        this.view.appendText(NEW_LINE + NEW_LINE + "Migliore soluzione della generazione: " + this.getBestSolutionOfGeneration());
        this.view.appendText(NEW_LINE + "Fitness della migliore soluzione della generazione: " + Collections.max(this.populationFitness));
        this.view.appendText(NEW_LINE + "Fitness media della generazione: " + (this.generationFitness / this.population.size()));

        if (generation > 0) {
            this.view.appendText(NEW_LINE + "Il crossover si è verificato " + this.crossoverCount + " volte");
            this.view.appendText(NEW_LINE + "La clonazione si è verificata " + this.cloneCount + " volte");
            this.view.appendText(NEW_LINE + "La mutazione si è verificata " + this.mutationCount + " volte");
        }

        this.view.appendText(NEW_LINE + NEW_LINE + "-----------------------------------------" + NEW_LINE + NEW_LINE);
    }

    private void showBestResult(){
        if (!"".equals(this.bestChromosome)){
            this.view.appendText("Soluzione migliore trovata: " + this.bestChromosome);
            this.view.appendText(NEW_LINE + "Valore totale con la soluzione migliore trovata = " + this.bestFitness + "  - Ottima conosciuta: " + this.optimumValue);
            this.checkBestSolution(this.bestChromosome);
        } else {
            this.view.appendText("Non è stata riscontrata nessuna soluzione accettabile.");
        }
    }

    private void checkBestSolution(String chromosome){

        List<Double> freeVolume = new ArrayList<>(this.knapsacksVolume);
        List<Double> volumeOccupato = new ArrayList<>();
        for (int i = 0; i < freeVolume.size(); i++) {
            volumeOccupato.add(0.0);
        }

        for (int i = 0; i < this.numberItems; i++){
            if (Character.getNumericValue(chromosome.charAt(i)) == 1){
                for (int j = 0; j < freeVolume.size(); j++) {
                    freeVolume.set(j,(freeVolume.get(j) - this.weightOfItems[j][i]));
                    volumeOccupato.set(j, (volumeOccupato.get(j) + this.weightOfItems[j][i]));
                }
            }
        }

        this.view.appendText(NEW_LINE + NEW_LINE + "Controllo soluzione: " + NEW_LINE);
        for (int i = 0; i < freeVolume.size(); i ++){
            this.view.appendText("Knapsack " + (i+1) + ") " + volumeOccupato.get(i) + " Volume occupato | " + freeVolume.get(i) + " Volume Libero" + NEW_LINE);
        }
    }

}
