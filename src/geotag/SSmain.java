package geotag;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tools.DatasetLoader;
import methods.SimilaritySearch;
import compare_objects.CompareDistance;
import compare_objects.DistanceTwoPoints;
import data_objects.ImageMetadata;


class SSmain{

	public SSmain (int N, int T, int k, int a, 
			double threshold, String trainFile, String testFile) throws IOException{
		
		System.out.println("Method : Similarity Search");

		long start = System.currentTimeMillis();

		DatasetLoader data = new DatasetLoader();

		ImageMetadata[] trainSet = data.initializeDataset(trainFile, N);


		long elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Time Elapsed For Loading Dataset: " + elapsedTime/1000F + "s");


		/*****************************************************************************************************/

		double distanceSS = 0, medianError;
		DistanceTwoPoints dist = new DistanceTwoPoints();

		float[] accuracyResults = new float[7];
		
		float[] estCoord = new float[2];
		List<Double> medianDistance = new ArrayList<Double>();

		ImageMetadata[] testSet = data.initializeDataset(testFile, T);

		SimilaritySearch imageSim = new SimilaritySearch(k, a, threshold);


		for (int i=0;i<testSet.length;i++){

			if (!testSet[i].equals("")){

				estCoord = imageSim.computeSimilarity(testSet[i], trainSet);

				if (estCoord!=null){

					distanceSS = dist.computeDistace(testSet[i].getCoord(),estCoord);
					medianDistance.add(distanceSS);

					if(distanceSS <= 1){
						accuracyResults[0]++;
					}
					if(distanceSS <= 10){
						accuracyResults[1]++;
					}
					if(distanceSS <= 100){
						accuracyResults[2]++;
					}
					if(distanceSS <= 500){
						accuracyResults[3]++;
					}
					if(distanceSS <= 1000){
						accuracyResults[4]++;
					}
					if(distanceSS <= 5000){
						accuracyResults[5]++;
					}
					if(distanceSS <= 10000){
						accuracyResults[6]++;
					}
				}
			}
		}

		Collections.sort(medianDistance,new CompareDistance());

		FileWriter fstream = new FileWriter("C:/Users/George/workspace/Location Estimation/results/SimilaritySearch/k_" + k + "_a_" + a);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write("Accuracy: ");
		
		for (int i=0;i<7;i++){
			
			out.write("\n Radius = " + i + " : " + 100*accuracyResults[i]/testSet.length + "%");
			
		}

		if((medianDistance.size() % 2) ==0){
			medianError = (medianDistance.get(Math.round((medianDistance.size()-1)/2))
					+ medianDistance.get(Math.round((medianDistance.size()/2))))/2;
		}
		else{
			medianError = medianDistance.get((medianDistance.size()/2));
		}

		System.out.println("Similarity Search Median Error Distance: " + medianError);

		out.write("\n\nMedian Error: " + medianError);

		elapsedTime = System.currentTimeMillis() - start;

		System.out.println("\nTotal Time Elapsed: "+ elapsedTime/1000F +"s");

		out.close();
	}
}