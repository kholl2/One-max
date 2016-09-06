import java.util.*;

public class Project3 {

	public static void main(String[] args) {
		int N = 100;  //pop size
		int t_max = 100; //max generations
		int bestOfRun = 0; //best of run
		Data bestOfRunVector = new Data();		
		int[] fitnessDistr = new int[N]; //fitness holder
		
		ArrayList<Data> data = new ArrayList<Data>(N);
		int t = 0;
		
		//Initialize Pop(0)
		for(int i = 0; i < N; i++){
			data.add(new Data());
		}
		
		int stringLength = data.get(0).getIndividualLength();
		
		//Get fitness
		fitnessDistr = getFitnessDistr(data, N);
		
		int bestOfGeneration = fitnessDistr[0];
		int index = 0;
		int avgOfGen = fitnessDistr[0];
		//System.out.println("Fitness at gen 0 "+ fitnessDistr[0]+" corresponding to vector: "+data.get(index).getIndividual());
		
		for (int i = 1; i < N; i++){
			//System.out.println("Fitness at gen 0 "+ fitnessDistr[i]+" corresponding to vector: "+data.get(i).getIndividual());
			
			avgOfGen += fitnessDistr[i];
			if (fitnessDistr[i] > bestOfGeneration){
				bestOfGeneration = fitnessDistr[i];
				index = i;
			}
		}
		
		int[] bestOfGen = new int[(t_max/10) + 1];
		int[] AvOfGen = new int[(t_max/10) + 1];
		int j =0;
		
		System.out.println("Generation 0");
		System.out.println("Best of generation: "+ bestOfGeneration +" corresponding to vector: "+data.get(index));
		bestOfGen[j] = bestOfGeneration;
		
		System.out.println("Average of generation: "+ avgOfGen/N);
		AvOfGen[j] = avgOfGen/N;
		System.out.println("--------------------------------------------------------");
		//System.out.println("First in main: "+data.toString());
		
		while(t < t_max){
			ArrayList<Data> afterSelection = new ArrayList<Data>(selection(data, N, fitnessDistr));
			ArrayList<Data> afterCrossover = new ArrayList<Data>(N);
			ArrayList<Data> afterMutation = new ArrayList<Data>(1);
			//System.out.println("After selection in main: "+afterSelection.toString());
			
			int afterSelectionSize = afterSelection.size();
			for (int i = 0; i < N; i++){
				Data tempSave = new Data();
				for(int k = 0; k < stringLength; k++){
					int rnd = new Random().nextInt(afterSelectionSize);
					tempSave.replaceIdividualAtIndex(k, afterSelection.get(rnd).getComponentAtIndex(k));
				}
				//System.out.println("After replacement :"+tempSave.getIndividual());
				afterCrossover.add(tempSave);
				int fit = fitness(tempSave);
				//best of Run
				if (fit > bestOfRun){
					bestOfRun = fit;
					bestOfRunVector = tempSave;
				}
				//mutation
				Data mutationVector = mutationApplied(tempSave);
				afterMutation.add(mutationVector);
				fit = fitness(mutationVector);
				//best of Run
				if (fit > bestOfRun){
					bestOfRun = fit;
					bestOfRunVector = mutationVector;
				}
			}
			
			data = afterMutation; //update data vector
			fitnessDistr = getFitnessDistr(data, N); //update fitness distribution
			
			//Get best and average at every 10th generation
			if ((t+1) % 10 == 0){
			    bestOfGeneration = fitnessDistr[0];
				avgOfGen = fitnessDistr[0];
				index = 0;
				
				for (int i = 1; i < N; i++){
					avgOfGen += fitnessDistr[i];
					if (fitnessDistr[i] > bestOfGeneration){
						bestOfGeneration = fitnessDistr[i];
						index = i;
					}
				}
				j++;
				avgOfGen = avgOfGen / N;
				System.out.println("Generation "+ (t+1));
				System.out.println("Best of generation is: "+ bestOfGeneration+" corresponding to vector: "+data.get(index) );
				System.out.println("Average of generation is: "+ avgOfGen);
				bestOfGen[j] = bestOfGeneration;
				AvOfGen[j] = avgOfGen;
				System.out.println("Best so far: "+ bestOfRun + " corresponding to vector: "+bestOfRunVector);
				System.out.println("--------------------------------------------------------");
			}
			t++;
		}
		
		
	}
	
	//function to get fitness distribution
		public static int[] getFitnessDistr(ArrayList<Data> data, int popSize){
			int[] fitnessDistr = new int[popSize];
			for (int i = 0; i< popSize; i++){
				int fit = fitness(data.get(i));
				fitnessDistr[i] = fit;
			}
			
			return fitnessDistr;
		}
	
	//calculate fitness
		public static int fitness(Data data){
			int f = 0;
			int n = data.getIndividualLength();
			for (int i = 0; i< n; i++){
				if(data.getComponentAtIndex(i) == '1')
				f ++;
			}
			
			return f;
		}
		
		//function to select the top 30% individuals
		public static ArrayList<Data> selection(ArrayList<Data> data, int popSize, int[] fitnessDistr){
			ArrayList<Data> selectedData = new ArrayList<Data>(data);
			int[] fD = new int[popSize];
			//Make a copy of fitness distr
			System.arraycopy(fitnessDistr, 0, fD, 0, popSize);
			
			
			for (int i = 0; i < popSize - 1; i++){
				int index = i;
				for (int j = i + 1; j < popSize; j++){
					if(fD[j] < fD[index]){
						index = j;
					}
				}
				int temp = fD[ index ];   //swap 
				Data tempdata = selectedData.get(index);
		        fD[ index ] = fD[ i ];
		        selectedData.set(index, selectedData.get(i));
		        fD[ i ] = temp;  
		        selectedData.set(i, tempdata);
			}
			
			
			int selectedSize = (30*popSize)/100;
			ArrayList<Data> finalSelectedData = new ArrayList<Data> (selectedData.subList(popSize - selectedSize, popSize));
			return finalSelectedData;
			
		}
		
		//Mutation
		public static Data mutationApplied(Data data){
			double p_m = 0.01; //mutation rate
			String temp = data.getIndividual();
			Data mutData = new Data(temp);
			String zero = "0";
			String one = "1";
			char z = zero.charAt(0);
			char o = one.charAt(0);
			
			//for each allele
			for (int i = 0; i < data.getIndividualLength(); i++){
				Random rand = new Random();
				double randNum = rand.nextDouble();
				char x = data.getComponentAtIndex(i); //get the allele
				if(randNum < p_m){
					
					if (x == z)
						mutData.replaceIdividualAtIndex(i, o);
					else
						mutData.replaceIdividualAtIndex(i, z);
				}
			}
			return mutData;
		}

}
