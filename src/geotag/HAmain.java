package geotag;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tools.DatasetLoader;
import tools.OrganizeTagsForX;
import tools.StoreLoad;
import methods.Clustering;
import methods.LanguageModel;
import methods.SimilaritySearch;
import methods.Xstat;
import compare_objects.CompareDistance;
import compare_objects.DistanceTwoPoints;
import data_objects.Area;
import data_objects.ImageMetadata;


public class HAmain {

	public HAmain(int N, int T, int K, int m, int u, int k, int a, double threshold, 
			String trainFile, String testFile, String action, String dir) throws IOException, ClassNotFoundException {

		
		System.out.println("Method : Hybrid Approach");
		
		long start = System.currentTimeMillis();

		DatasetLoader data = new DatasetLoader();

		ImageMetadata[] trainSet = data.initializeDataset(trainFile, N);

		Area[] areas = null;
		
		List<float[]> mendoids = new ArrayList<float[]>();
		
		Integer largestArea = 0;
		
		long elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Time Elapsed For Loading Dataset: " + elapsedTime/1000F + "s");
		
		System.out.println("\nNumber of Clusters: " + K);
		
		StoreLoad handler = new StoreLoad(K, dir);
		
		
		if (action.equals("Store")){
			
			start = System.currentTimeMillis();

			Clustering clusterer = new Clustering(trainSet, K);

			areas = clusterer.createAreas(trainSet);

			largestArea = clusterer.getLargestArea();
			
			elapsedTime = System.currentTimeMillis() - start;

			System.out.println("Time Elapsed For Clustering: "+ elapsedTime/1000F +"s");

			handler.store(K, areas, largestArea);
			

		} else if (action.equals("Load")){
			
			
			start = System.currentTimeMillis();

			areas = handler.load(K, largestArea, trainSet);
			
			elapsedTime = System.currentTimeMillis() - start;

			System.out.println("Time Elapsed For Loading Clusters: " + elapsedTime/1000F + "s");

		}

		OrganizeTagsForX organizeTags = new OrganizeTagsForX();

		Map<String, Map<Integer,Integer>> tagMap = organizeTags.getTagMap(trainSet);
		
		
		elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Time Elapsed For Creating Map: " + elapsedTime/1000F + "s");
		

		Xstat x = new Xstat(N, m);

		x.setAreasVocadulary(tagMap, areas);



		elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Time Elapsed For Organizing Tags: "+ elapsedTime/1000F +"s");


		/*****************************************************************************************************/


		double distanceLM, medianError;
		DistanceTwoPoints dist = new DistanceTwoPoints();

		float[] accuracyResults = new float[7];

		float[] estCoord = new float[2];
		List<List<Double>> medianDistance = new ArrayList<List<Double>>();

		for (int i=0;i<7;i++){
			medianDistance.add(new ArrayList<Double>());
		}

		ImageMetadata[] testSet = data.initializeDataset(testFile, T);

		LanguageModel testArea = new LanguageModel(u);
		
		SimilaritySearch imageSim = new SimilaritySearch(k, a, threshold);

		Integer maxAreaId;

		System.out.println();
		
		int onepercent = testSet.length / 100;

		for (int i=0;i<testSet.length;i++){ 
			
			if (i%onepercent == 0){
				System.out.println(i/onepercent + "% (" + i + ")" );
			}
					
			
			if (!testSet[i].equals("")){

				maxAreaId = testArea.computeArea(testSet[i], areas, tagMap);

				if (maxAreaId!=null){
					estCoord = imageSim.computeSimilarityUsingArea(testSet[i], trainSet, areas[maxAreaId]);
				} 
				else {
					estCoord = imageSim.computeSimilarityUsingArea(testSet[i], trainSet, areas[largestArea]);
					if (estCoord==null){
						estCoord = mendoids.get(largestArea);
					}
				}
			} 
			else {
				estCoord = mendoids.get(largestArea);
			}

			distanceLM = dist.computeDistace(testSet[i].getCoord(), estCoord);

			int numTags = testSet[i].getTags().split(" ").length;

			if (numTags < 6){
				medianDistance.get(numTags-1).add(distanceLM);
			}
			else if (numTags < 11){
				medianDistance.get(5).add(distanceLM);
			}
			else if (numTags < 76){
				medianDistance.get(6).add(distanceLM);
			}


			if(distanceLM <= 1){
				accuracyResults[0]++;
			}
			if(distanceLM <= 10){
				accuracyResults[1]++;
			}
			if(distanceLM <= 100){
				accuracyResults[2]++;
			}
			if(distanceLM <= 500){
				accuracyResults[3]++;
			}
			if(distanceLM <= 1000){
				accuracyResults[4]++;
			}
			if(distanceLM <= 5000){
				accuracyResults[5]++;
			}
			if(distanceLM <= 10000){
				accuracyResults[6]++;
			}

		}

		FileWriter fstream = new FileWriter("results/HybridApproach/clust_" + K + "_k_" + k + "_a_" + a + "_2");
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write("K = " + K);

		for (int i=0;i<7;i++){
			if (medianDistance.get(i).size()!=0){

				Collections.sort(medianDistance.get(i),new CompareDistance());

				if((medianDistance.get(i).size() % 2) ==0){

					medianError = (medianDistance.get(i).get(Math.round((medianDistance.get(i).size()-1)/2))
							+ medianDistance.get(i).get(Math.round((medianDistance.get(i).size()/2))))/2;
				}
				else{
					medianError = medianDistance.get(i).get((medianDistance.get(i).size()/2));
				}

				out.write("\nNumber Of Tags: " + (i+1) +"\nMedian Error: " + medianError);

			}
			else{
				out.write("\nNumber Of Tags: " + (i+1) +"\nMedian Error: 0");
			}
		}

		out.write("\n\nAccuracy ");

		for (int i=0;i<7;i++){

			out.write("\n Radius = " + i + " : " + 100*accuracyResults[i]/testSet.length + "%");

		}

		elapsedTime = System.currentTimeMillis() - start;

		System.out.println("\nTotal Time Elapsed: "+ elapsedTime/1000F +"s");

		out.close();
	}
}

