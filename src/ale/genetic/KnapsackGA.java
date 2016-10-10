package ale.genetic;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import ale.common.View;

public class KnapsackGA {

	private int crossoverCount;
	private int cloneCount;
	private int mutationCount;
	private int nItems;
	private int populationSize = 10;
	private int nGenerations = 20;
	private double knapsackCapacity;
	private double probCrossover = 0.6;
	private double probMutation = 0.05;
	private double totalFitnessOfGeneration = 0;
	private List<Double> valueOfItems;
	private List<Double> weightOfItems;
	private List<Double> fitness = new ArrayList<>();
	private List<Double> bestFitnessOfGeneration = new ArrayList<>();
	private List<Double> meanFitnessOfGeneration = new ArrayList<>();
	private List<String> population = new ArrayList<>();
	private List<String> breedPopulation = new ArrayList<>();
	private List<String> bestSolutionOfGeneration = new ArrayList<>();
	private View view;

	public KnapsackGA(final int nItems, final double knapsackCapacity, final List<Double> weightOfItems, final List<Double> valueOfItems, final View view) {
		this.nItems = nItems;
		this.knapsackCapacity = knapsackCapacity;
		this.weightOfItems = weightOfItems;
		this.valueOfItems = valueOfItems;
		this.view = view;
		this.buildKnapsackProblem();
	}

	public void buildKnapsackProblem() {
		//genera la popolazione iniziale randomicamente
		this.makePopulation();

		this.view.appendText("Generazione iniziale:");
		this.view.appendText("\n===================");
		this.view.appendText("\nPopolazione:");
		for(int i = 0; i < this.populationSize; i++) {
			this.view.appendText("\n" + (i + 1) + " - " + this.population.get(i));
		}

		//valuta la fitness della popolazione iniziale
		this.evalPopulation(this.population);

		this.view.appendText("\n\nFitness:");
		for(int i = 0; i < this.populationSize; i++) {
			this.view.appendText("\n" + (i + 1) + " - " + this.fitness.get(i));
		}

		//Migliore soluzione della generazione
		this.bestSolutionOfGeneration.add(this.population.get(this.getBestSolution()));

		this.view.appendText("\n\nMigliore soluzione della generazione iniziale: " + this.bestSolutionOfGeneration.get(0));

		//Fitness media della generazione
		this.meanFitnessOfGeneration.add(this.getMeanFitness());

		this.view.appendText("\nFitness media della generazione iniziale: " + this.meanFitnessOfGeneration.get(0));
		
		//Migliore fitness della generazione
		this.bestFitnessOfGeneration.add(this.calculateFitness(this.population.get(this.getBestSolution())));

		this.view.appendText("\nFitness della migliore soluzione della generazione iniziale: " + this.bestFitnessOfGeneration.get(0));

		//Genera le generazioni successive
		if(this.nGenerations > 1) {
			makeFurtherGenerations();
		}

		this.showOptimalList();

	}

	/**
	 * Crea una popolazione casuale.
	 */
	private void makePopulation() {

		for(int i = 0; i < this.populationSize; i++) {
			this.population.add(makeChromosome());      
		}
	}

	/**  
	 * Genera una soluzione casuale sotto forma di cromosoma con codifica binaria (stringa di 0 e 1).
	 * @return Il cromosoma prodotto.
	 */
	private String makeChromosome() {

		StringBuilder chromosome = new StringBuilder(this.nItems);

		char gene;

		for(int i = 0; i < this.nItems; i++) {
			gene = '0';
			double rnd = Math.random(); 
			if(rnd > 0.5) {
				gene = '1';
			}
			chromosome.append(gene);
		}
		return chromosome.toString();
	}

	/**
	 * Valuta la fitness della popolazione.
	 * @param population - Popolazione di cui si vuole valutare la fitness.
	 */
	private void evalPopulation(List<String> population) {

		this.totalFitnessOfGeneration = 0;
		for(int i = 0; i < this.populationSize; i++) {
			double tempFitness = calculateFitness(population.get(i));
			this.fitness.add(tempFitness);
			this.totalFitnessOfGeneration += tempFitness;
		}
	}

	/**
	 * Getter per avere la migliore soluzione della popolazione corrente.
	 * @return La posizione nella lista della migliore soluzione. 
	 */
	private int getBestSolution() {

		int bestPosition = 0;
		double thisFitness = 0;
		double bestFitness = 0;
		for (int i = 0; i < this.populationSize; i++) {
			thisFitness = calculateFitness(this.population.get(i));
			if (thisFitness > bestFitness) {
				bestFitness = thisFitness;
				bestPosition = i;
			}
		}
		return bestPosition;
	}

	/**
	 * Valuta la fitness di una soluzione (cromosoma) in base al valore e al peso.
	 * @param chromosome - Il cromosoma da valutare.
	 * @return il valore di fitness.
	 */
	private double calculateFitness(String chromosome) {

		double totalWeight = 0;
		double totaleValue = 0;
		double fitnessValue = 0;
		double difference = 0;
		char c = '0';

		//Calcola peso e valore degli oggetti selezionati nella soluzione
		for(int j = 0; j < this.nItems; j ++) {
			c = chromosome.charAt(j);
			if(c == '1') {
				totalWeight = totalWeight + this.weightOfItems.get(j);
				totaleValue = totaleValue + this.valueOfItems.get(j);
			}
		}
		//Controlla che il peso totale della soluzione sia inferiore alla capacità del knapsack.
		//se è superiore, viene restituito 0, altrimenti il valore totale degli oggetti nella soluzione.
		difference = this.knapsackCapacity - totalWeight;
		if(difference >= 0) {
			fitnessValue = totaleValue;
		}

		return fitnessValue;
	}

	/**
	 * Produce le generazioni successive alla prima.
	 */
	private void makeFurtherGenerations() {

		for(int i = 1; i < this.nGenerations; i++) {

			this.crossoverCount = 0;
			this.cloneCount = 0;
			this.mutationCount = 0;

			//riproduzione popolazione
			for(int j = 0; j < this.populationSize / 2; j++) {
				this.crossover(this.montecarlo(), this.montecarlo());
			}
			
			//mutazione
			this.mutation();

			//resetto le fitness della generazione precedente
			this.fitness.clear();

			//valuto la fitness della nuova generazione
			this.evalPopulation(this.breedPopulation);

			//sovrascrivo la vecchia popolazione con quella nuova
			for(int k = 0; k < this.populationSize; k++) {
				this.population.set(k, this.breedPopulation.get(k));
			}

			//stampa la popolazione
			this.view.appendText("\n\nGenerazione " + (i + 1) + ":");
			if((i + 1) < 10) {
				this.view.appendText("\n=============");
			}
			if((i + 1) >= 10) {
				this.view.appendText("\n==============");
			}
			if((i + 1) >= 100) {
				this.view.appendText("\n===============");
			}
			this.view.appendText("\nPopolazione:");
			for(int l = 0; l < this.populationSize; l++) {
				this.view.appendText("\n" + (l + 1) + " - " + this.population.get(l));
			}

			//stampa le fitness di ciascun cromosoma
			this.view.appendText("\n\nFitness:");
			for(int m = 0; m < this.populationSize; m++) {
				this.view.appendText("\n" + (m + 1) + " - " + this.fitness.get(m));
			} 

			this.breedPopulation.clear();

			//cerca la migliore soluzione della generazione corrente (quella con fitness maggiore)
			this.bestSolutionOfGeneration.add(this.population.get(this.getBestSolution()));

			this.view.appendText("\n\nMigliore soluzione della generazione " + (i + 1) + ": " + this.bestSolutionOfGeneration.get(i));

			this.meanFitnessOfGeneration.add(this.getMeanFitness());

			this.view.appendText("\nFitness media della generazione: " + this.meanFitnessOfGeneration.get(i));

			this.bestFitnessOfGeneration.add(this.calculateFitness(this.population.get(this.getBestSolution())));

			this.view.appendText("\nFitness della migliore soluzione della generazione " + (i + 1) + ": " + this.bestFitnessOfGeneration.get(i));

			this.view.appendText("\nIl crossover si è verificato " + this.crossoverCount + " volte");
			this.view.appendText("\nLa clonazione si è verificata " + this.cloneCount + " volte");
			this.view.appendText("\nLa mutazione si è verificata " + this.mutationCount + " volte");

		}
	}

	/**
	 * Roulette Wheel Selection (Montecarlo) per selezionare i genitori.
	 * @return La posizione del genitore selezionato per la riproduzione.
	 */
	private int montecarlo() {

		double rand = Math.random() * this.totalFitnessOfGeneration;
		double currSum = 0;

		for (int i = 0; i < this.populationSize; i++) {
			if (currSum + calculateFitness(population.get(i)) >= rand) {
				return i;
			} else {
				currSum += calculateFitness(this.population.get(i));
			}
		}

		return 0;
	}

	/**
	 * Esegue il crossover tra i due cromosomi in base alla probabilità. Se il crossover non avviene, la nuova generazione avrà i cloni dei due genitori.
	 * @param firstParent Il primo genitore.
	 * @param secondParent Il secondo genitore.
	 */
	private void crossover(int firstParent, int secondParent) {

		String firstChild;
		String secondChild;

		double random = Math.random();
		if(random <= this.probCrossover) {
			this.crossoverCount++;
			Random generator = new Random(); 
			int crossPoint = generator.nextInt(this.nItems) + 1;

			firstChild = this.population.get(firstParent).substring(0, crossPoint) + this.population.get(secondParent).substring(crossPoint);
			secondChild = this.population.get(secondParent).substring(0, crossPoint) + this.population.get(firstParent).substring(crossPoint);

			this.breedPopulation.add(firstChild);
			this.breedPopulation.add(secondChild);
		} else {
			this.cloneCount++;
			this.breedPopulation.add(this.population.get(firstParent));
			this.breedPopulation.add(this.population.get(secondParent));
		}
	}

	/**
	 * Metodo per la mutazione dei geni: scorre i geni di tutti i cromosomi e li cambia in base alla probabilità impostata
	 */
	private void mutation() {

		ListIterator<String> iterator = this.breedPopulation.listIterator();
		while (iterator.hasNext()) {
			String chromosome = iterator.next();
			for (int i = 0; i < chromosome.length(); i++) {
				double mutationProb = Math.random();
				if (mutationProb <= probMutation) { //se il numero casuale è minore o uguale della probabilità di mutazione, cambio valore al gene
					if (chromosome.charAt(i) == '0') { //gene a 0, da mutare in 1
						chromosome = chromosome.substring(0, i) + "1" + chromosome.substring(i + 1);
					} else { //gene a 1, da mutare a 0
						chromosome = chromosome.substring(0, i) + "0" + chromosome.substring(i + 1);
					}
					iterator.set(chromosome);
					this.mutationCount++;
				}
			}
		}
	}

	/**
	 * Restituisce la fitness media della generazione
	 */
	private double getMeanFitness() {

		double totalFitness = 0;
		double meanFitness = 0;
		for (int i = 0; i < this.populationSize; i++) {
			totalFitness += this.fitness.get(i);
		}
		meanFitness = totalFitness / this.populationSize;
		return meanFitness;
	}

	/**
	 * Stampa la migliore soluzione trovata
	 */
	private void showOptimalList() {

		int counterValue = 0;
		this.view.appendText("\n\nLista migliore di oggetti da includere nel knapsack: ");

		double bestFitness = 0;
		int bestGen = 0;

		//Cerca la migliore soluzione di sempre
		for (int z = 0; z < this.nGenerations; z++) {
			if (this.bestFitnessOfGeneration.get(z) > bestFitness) {
				bestFitness = this.bestFitnessOfGeneration.get(z);
				bestGen = z;
			}
		}

		//Stampa quali oggetti includere grazie al cromosoma migliore
		String optimalList = this.bestSolutionOfGeneration.get(bestGen);
		this.view.appendText("\n");
		for (int y = 0; y < this.nItems; y++) {
			if (optimalList.substring(y, y + 1).equals("1")) {
				this.view.appendText((y + 1) + " ");
				counterValue += this.valueOfItems.get(y);
			}
		}
		this.view.appendText("\n\nValore totale con la soluzione migliore trovata = " + counterValue);
	}
}
