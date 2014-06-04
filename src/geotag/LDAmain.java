package geotag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.Cluster;

import tools.DatasetCreator;
import tools.FileCreator;
import tools.DatasetLoader;
import tools.StoreLoad;
import tools.TopicHandler;
import methods.Clustering;
import methods.SimilaritySearch;
import data_objects.Area;
import data_objects.AreaCluster;
import data_objects.ImageMetadata;
import data_objects.ImageTopic;
import jgibblda.LDA;



public class LDAmain {

	@SuppressWarnings("unused")
	public LDAmain(int N, int T, int K, String keyword, String dir, String ntopics, String twords, 
			String gtopics, String gwords, double entropyThreshold, String trainFile,
			String testFile, String action) throws IOException, ClassNotFoundException {


		System.out.println("Method : LDA");

		/*
		 	Initialize Data
		 */
		long start = System.currentTimeMillis();

		long elapsedTime;

		DatasetLoader data = new DatasetLoader();

		ImageMetadata[] testSet = data.initializeDataset(testFile, T);

		ImageMetadata[] trainSet = data.initializeDataset(trainFile, N);

		/*
	 		Create a lookup table that contains each image's id in correspondence with its position in the training table.
		 */
		Map<String,Integer> trainSetIdMap = new HashMap<String,Integer>();   

		for(int i=0;i<N;i++){
			trainSetIdMap.put(trainSet[i].getId(), i);
		}


		elapsedTime = System.currentTimeMillis() - start;

		System.out.println("Time Elapsed For Loading Dataset: " + elapsedTime/1000F + "s");



		Area[] areas = new Area[K];

		Integer largestArea = 0;

		System.out.println("\nNumber of Clusters: " + K);

		StoreLoad handler = new StoreLoad(K, "G:/LDA/Areas");





		/*
	 		Cluster the training set images, in order to create areas, using k-means algorithm.
	 		If the areas already exists, they are loaded from the corresponding files.
	 		Else, they are stored in a file with the proper name.
		 */
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





		/*
			Apply LDA for each such area, referred as Local LDAs.
		 */
		for(int i=0;i<areas.length;i++){

			System.out.println("Area " + i);

			AreaCluster ncluster = areas[i].getAreaCluster();
			Cluster cluster = ncluster.getCluster();
			List<Clusterable> items = cluster.getItems();

			int size = cluster.getItems().size();

			ImageMetadata[] areaSet = new ImageMetadata[size];

			for (int j=0; j<size; j++){

				Cluster clusterItem = (Cluster) items.get(j);

				int imageId = clusterItem.getId();

				areaSet[j] = trainSet[imageId];
			}

			FileCreator.createImageFile(areaSet, dir + "/LDA/area_" + Integer.toString(i), "area_" + Integer.toString(i));

			String[] arguments = new String[] {"-est", "-ntopics", ntopics, "-twords", twords, 
					"-savestep", "0", "-dir", dir + "/LDA/area_" + Integer.toString(i), 
					"-dfile", "area_" + Integer.toString(i)+".txt"};

			LDA.main(arguments);
		}





		/*
			Apply LDA in the training dataset, referred as Global LDA.
		 */
		DatasetCreator.createDatasetForLDA(trainFile, testFile, dir + "/dataset/MediaEval");

		String[] arguments = new String[] {"-est", "-ntopics", gtopics, "-twords", gwords, 
				"-savestep", "0", "-dir", dir + "/dataset/MediaEval", 
				"-dfile", "all_training_LDA"};





		/*
			Create the list of the active topics for all images of the training dataset,
			compute each topic's frequency per area, ending up with a topic-area distribution (histogram),
			and calculate each topic's entropy based in its histogram.
		 */
		List<List<ImageTopic>> listActiveTopicsImages = TopicHandler.createListOfActiveTopics(
				N, dir+"/LDA/model-v2-"+gtopics+"-50/model-final.theta");

		Map<Integer,double[]> topicClusterFreqMap = TopicHandler.computeHistogram(
				areas, trainSet, Integer.parseInt(gtopics), listActiveTopicsImages);

		double[] entropy = TopicHandler.computeTopicsEntropy(topicClusterFreqMap, areas, Integer.parseInt(gtopics), dir);





		/*
			As inactive topics are considered those whose entropy exceeds the threshold the are set and 
			the Bag of Excluded Words is created.
		 */
		Set<Integer> inactiveTopics = TopicHandler.excludeTopicsFromFile(
				entropy, entropyThreshold, Integer.parseInt(gtopics));

		Map<Integer,String> wordMap = TopicHandler.loadWordMap(dir + "/model-v2-" + gtopics + "-50/wordmap.txt");

		Set<String> bagOfWords = TopicHandler.createBagOfWords(inactiveTopics, 
				Integer.parseInt(gtopics), wordMap, dir + "/model-v2-" + gtopics + "-50/model-final.phi", dir + "/LDA");





		/*
			Calculate the similarity between every image of the test set and the topics
			from the Local LDAs. The results are saved in files with proper names.
		 */
		TopicHandler.computeTopicSimilarity(testSet, K, dir + "/Areas");

		elapsedTime = System.currentTimeMillis() - start;

		System.out.println("\nTime Elapsed For Assignment: " + elapsedTime/1000F + "s");





		/*
		 	Final estimation of the image in the test set and evaluation of the method.
		 */
		float[] estCoord = new float[2];

		FileInputStream fInStream = new FileInputStream(dir + "/Areas/similarityMax-500.txt");
		DataInputStream in = new DataInputStream(fInStream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		SimilaritySearch imageSim = new SimilaritySearch(2, 25, 0.005);

		Integer maxAreaId;

		FileWriter fstream = new FileWriter(dir + "/submission files/me13pt_certh_tmax.txt");
		BufferedWriter out = new BufferedWriter(fstream);

		String inputLine;
		String[] input = null;


		int onepercent = testSet.length / 100;

		for (int i=0;i<testSet.length;i++){

			if (i%onepercent == 0){
				System.out.println(i/onepercent + "% (" + i + ")" );
			}

			inputLine = temp.readLine();
			input = inputLine.split(" ");

			maxAreaId = (int) Double.parseDouble(input[1]);

			if (Double.parseDouble(input[0])!=0.0){
				estCoord = imageSim.computeSimilarityUsingArea(testSet[i], trainSet, areas[maxAreaId]);
			} 
			else {
				estCoord = imageSim.computeSimilarityUsingArea(testSet[i], trainSet, areas[largestArea]);
				if (estCoord==null){
					estCoord = areas[largestArea].getAreaCluster().getCluster().getClusterMean();
				}
			}		

			out.write(testSet[i].getId()+";"+estCoord[0]+";"+estCoord[1]);
			out.newLine();
		}
		temp.close();
		out.close();
	}
}