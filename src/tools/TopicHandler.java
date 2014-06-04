package tools;

import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.Cluster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
//import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import compare_objects.CompareTopics;
import data_objects.Area;
import data_objects.ImageMetadata;
import data_objects.ImageTopic;


public class TopicHandler {


	public static List<List<ImageTopic>> createListOfActiveTopics(int N, String file) throws IOException, ClassNotFoundException{

		/*
			Topics are activated for an image,when those similarity
			exceeds the activation threshold.
		
			Activation Threshold : 1 / (0.9 * Number of Global Topics)
		 */
		
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		List<List<ImageTopic>> listTopicsImages = new ArrayList<List<ImageTopic>>();

		String inputLine;

		for (int i=0;i<N;i++){

			inputLine = temp.readLine();

			String [] input = inputLine.split(" ");

			List<ImageTopic> listTopics = new ArrayList<ImageTopic>();

			for (int j=0;j<input.length;j++){
				if(Double.parseDouble(input[j])>(1.0/(0.9*input.length))){
					listTopics.add(new ImageTopic(j,0,Double.parseDouble(input[j])));
				}
			}

			listTopicsImages.add(listTopics);
		}

		temp.close();

		return listTopicsImages;
	}



	public static Map<Integer,double[]> computeHistogram (Area areas[], ImageMetadata[] trainSet, 
			int gtopics, List<List<ImageTopic>> listActiveTopicsImages) throws IOException{

		Map<Integer,double[]> topicClusterFreqMap = new HashMap<Integer,double[]>();

		for(int i=0; i<areas.length ;i++){

			Cluster cluster = areas[i].getAreaCluster().getCluster();
			List<Clusterable> items = cluster.getItems();

			int size = cluster.getItems().size();

			double[] topicClusterFreq = new double[gtopics];


			for (int j=0; j<size; j++){

				Cluster clusterItem = (Cluster) items.get(j);

				int imageId = clusterItem.getId();

				for(int k=0;k<listActiveTopicsImages.get(imageId).size();k++){
					topicClusterFreq[listActiveTopicsImages.get(imageId).get(k).getTopicId()]+=(1.0/size);
				}
			}

			topicClusterFreqMap.put(cluster.getId(), topicClusterFreq);

		}
		return topicClusterFreqMap;

	}



	public static double[] computeTopicsEntropy(Map<Integer,
			double[]> topicClusterFreqMap, Area areas[], int gtopics, 
			String dir) throws IOException{

		FileWriter fOutStream = new FileWriter(dir + "LDA/entropy"+String.valueOf(gtopics)+"-at.txt");
		BufferedWriter out = new BufferedWriter(fOutStream);


		double[] entropy = new double[gtopics];

		for(int i=0; i<gtopics ;i++){

			double[] p = new double[areas.length];

			for (int j=0; j<areas.length; j++){

				p[j] = topicClusterFreqMap.get(areas[j].getAreaCluster().getCluster().getId())[i];

			}

			entropy[i] = computeEntropyNaive(p);

			out.write(entropy + "");

			out.write("\n");
		}

		out.close();

		return entropy;
	}



	public static Set<Integer> excludeTopicsFromFile(double[] entropy, 
			double entropyThreshhold, int gtopics) throws NumberFormatException, IOException{

		Set<Integer> inactiveTopics = new HashSet<Integer>();

		for(int i=0;i<gtopics;i++)
			if (entropy[i]>entropyThreshhold){
				inactiveTopics.add(i);
			}

		return inactiveTopics;
	}



	public static Set<String> createBagOfWords( Set<Integer> inactiveTopics, int gtopics,
			Map<Integer,String> wordMap, String phiFile, String dir) throws IOException{

		/*
			Topics are activated for an word map's word, when those similarity
			exceeds the activation threshold.
	
			Activation Threshold : 1 / (0.9 * Number of Global Topics)
		 */
		
		FileInputStream fInStream = new FileInputStream(phiFile);
		DataInputStream in = new DataInputStream(fInStream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		List<List<ImageTopic>> listActiveTopics = new ArrayList<List<ImageTopic>>();

		for(int i=0;i<wordMap.size();i++){
			listActiveTopics.add(new ArrayList<ImageTopic>());
		}
		
		
		/*
		 	For every word that belongs to word map, a list of the topics that are activated is created.
		 */
		for(int i=0;i<gtopics;i++){

			String inputLine = temp.readLine();

			String[] input = inputLine.split(" ");

			for(int j=0;j<input.length;j++){
				if(Double.parseDouble(input[j])>(1.0/(0.9*gtopics))){
					listActiveTopics.get(j).add(new ImageTopic(i,0,Double.parseDouble(input[j])));
				}
			}	
		}

		Set<String> bagOfWords = new HashSet<String>();

		FileWriter fOutStream = new FileWriter(dir + "/BoEW.txt");
		BufferedWriter out = new BufferedWriter(fOutStream);

		
		/*
		 	The list of the activated topics is ascended sorted and if the topic 
		 	with the greater similarity is included to the list of inactive topics, 
		 	the word is included in the Bag of Excluded Words.
		 */
		for(int i=0;i<listActiveTopics.size();i++){
			if(!listActiveTopics.get(i).isEmpty()){
				List<ImageTopic> listTopics = listActiveTopics.get(i);
				Collections.sort(listTopics, new CompareTopics());

				if(inactiveTopics.contains(listTopics.get(0).getTopicId())){
					System.out.println(wordMap.get(i));
					out.write(wordMap.get(i) + "\n");
					bagOfWords.add(wordMap.get(i));
				}
			}
		}
		out.close();
		temp.close();

		return bagOfWords;
	}



	public static Set<String> loadBagOfWords(String bagOfWordFile) throws NumberFormatException, IOException{

		FileInputStream fInStream = new FileInputStream(bagOfWordFile);
		DataInputStream in = new DataInputStream(fInStream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		Set<String> bagOfWords = new HashSet<String>();

		String input = temp.readLine();

		while(input!=null){				
			bagOfWords.add(input);
			input = temp.readLine();
		}

		temp.close();

		return bagOfWords;
	}



	public static void computeTopicSimilarity(ImageMetadata[] testSet, int K, String dir) throws NumberFormatException, IOException{

		FileInputStream fInStream = null;
		DataInputStream in =null;
		BufferedReader temp = null;

		FileWriter fOutStream = null;
		BufferedWriter out = null;


		String inputLine = "";
		String[] input;

		Set<String> topicWords = new HashSet<String>();
		double[][] similarityTable = new double[testSet.length][100];

		List<String> testImageTags = new ArrayList<String>();
		List<List<String>> testSetTags = new ArrayList<List<String>>();

		for(int k=0;k<testSet.length;k++){
			testImageTags = Arrays.asList(testSet[k].getTags().split(" "));
			testSetTags.add(testImageTags);
		}

		
		double counter;

		double[][] similarityMax = new double[testSet.length][2];


		for(int k=0;k<testSet.length;k++){
			similarityMax[k][0] = 0;
			similarityMax[k][1] = 0;
		}

		double[][] similarityMean = new double[testSet.length][2];


		for(int k=0;k<testSet.length;k++){
			similarityMean[k][0] = 0;
			similarityMean[k][1] = 0;
		}

		int incounter = 0;
		double[][] mean = new double[testSet.length][2];
		System.out.println();


		for(int i=0;i<K;i++){

			if ((i) % 100 == 0){
				System.out.print((incounter+1) + " ");
				incounter++;
			}

			fInStream = new FileInputStream(dir+"/LDA/area_"+i+"/model-final.twords");
			in = new DataInputStream(fInStream);
			temp = new BufferedReader(new InputStreamReader(in));

			inputLine = "";

			for(int k=0;k<testSet.length;k++){
				mean[k][0] = 0.0;
				mean[k][1] = 0.0;
			}

			inputLine = temp.readLine();
			int j=0;

			while(inputLine!=null){

				topicWords = new HashSet<String>();

				inputLine = temp.readLine();

				while( inputLine!=null && !inputLine.equals("Topic "+(j+1)+"th:")){
					input = inputLine.split(" ");
					topicWords.add(input[0].substring(1,input[0].length()));
					inputLine = temp.readLine();

				}


				for(int k=0;k<testSet.length;k++){
					testImageTags = testSetTags.get(k);
					counter = 0.0;

					for(int t=0;t<testImageTags.size();t++){
						if(topicWords.contains(testImageTags.get(t))){
							counter++;
						}
					}

					similarityTable[k][j] = counter/(testImageTags.size()+20.0-counter);

					if(similarityTable[k][j]>0.0){
						if(similarityMax[k][0]<similarityTable[k][j]){
							similarityMax[k][0]=similarityTable[k][j];
							similarityMax[k][1]=i;
						}
						mean[k][0] += similarityTable[k][j]; 
						mean[k][1] += 1.0;
					}
				}
				for(int k=0;k<testSet.length;k++){

					if(similarityMean[k][0]<mean[k][0]/mean[k][1]){
						similarityMean[k][0] = mean[k][0]/mean[k][1];
						similarityMean[k][1] = i;
					}
				}

				j++;

			}

			if((i+1) % 500 == 0){
				fOutStream = new FileWriter(dir+"/similarityMax-"+(i+1)+".txt");
				out = new BufferedWriter(fOutStream);

				for(int k=0;k<testSet.length;k++){
					out.write(similarityMax[k][0]+" "+similarityMax[k][1]+"\n");
				}
				out.close();

				fOutStream = new FileWriter(dir+"/similarityMean-"+(i+1)+".txt");
				out = new BufferedWriter(fOutStream);

				for(int k=0;k<testSet.length;k++){
					out.write(similarityMean[k][0]+" "+similarityMean[k][1]+"\n");
				}
				out.close();
			}
		}

		fOutStream = new FileWriter(dir+"/similarityMax-final.txt");
		out = new BufferedWriter(fOutStream);

		for(int k=0;k<testSet.length;k++){
			out.write(similarityMax[k][0]+" "+similarityMax[k][1]+"\n");
		}
		out.close();

		fOutStream = new FileWriter(dir+"/similarityMean-final.txt");
		out = new BufferedWriter(fOutStream);

		for(int k=0;k<testSet.length;k++){
			out.write(similarityMean[k][0]+" "+similarityMean[k][1]+"\n");
		}
		out.close();
	}



	public static Map<Integer,String> loadWordMap(String wordMapFile) throws IOException{

		
		
		Map<Integer,String> wordMap = new HashMap<Integer,String>();

		FileInputStream fInStream = new FileInputStream(wordMapFile);
		DataInputStream in = new DataInputStream(fInStream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		String inputLine = temp.readLine();
		inputLine = temp.readLine();

		while(inputLine != null){

			String[] input = inputLine.split(" ");

			wordMap.put(Integer.parseInt(input[1]), input[0]);
			inputLine = temp.readLine();

		}

		temp.close();

		return wordMap;		
	}



	private static double computeEntropyNaive(final double[] probabilities) {
		double entropy = 0.0;
		for (int i = 0; i < probabilities.length; i++) {
			final double p = probabilities[i];
			if(p!=0.0){
				entropy -= p * Math.log(p);
			}
		}
		return entropy;
	}
}
