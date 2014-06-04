package tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatasetCreator {

	private static Map<String,String[]> latlonMap = new HashMap<String,String[]>();

	public static void createDatasetForLDA(String trainFile, String testFile, String dir) throws IOException{

		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader temp = null;

		Set<String> testSet = createTestSet(testFile);
		List<String> exportDataTrain = new ArrayList<String>();
		List<String> exportDataTest = new ArrayList<String>();

		for(int i=1;i<11;i++){

			fstream = new FileInputStream(trainFile+"/metadata_"+i+".csv");
			in = new DataInputStream(fstream);
			temp = new BufferedReader(new InputStreamReader(in));

			String inputLine = temp.readLine();
			inputLine = temp.readLine();
			while(inputLine != null){

				String [] input = inputLine.split(",");

				if (!testSet.contains(input[0])){

					if(input[4].substring(0,1).matches("\"")){
						input[4] = (input[4].substring(1, input[4].length()-1));
					}

					input[4] = filter(input[4],null);
					
					if(input[4].length()>1){
						exportDataTrain.add(input[4]);
					}
					
				}else{
					if(input[4].substring(0,1).matches("\"")){
						input[4] = (input[4].substring(1, input[4].length()-1));
					}
					exportDataTest.add(input[4]);
				}
				inputLine = temp.readLine();
			}
		}

		System.out.println(exportDataTest.size());

		FileCreator.writeInFile(exportDataTrain, "all_training_Update_LDA", dir, true);

		FileCreator.writeInFile(exportDataTest, "all_test_Update_LDA", dir, true);

		temp.close();

	}

	public static void createDatasetGeneral(String trainFile, String testFile, String dir, Set<String> bagOfWords) throws IOException{

		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader temp = null;

		Set<String> testSet = createTestSet(testFile);
		createLatLonSet(trainFile + "/training_latlng_Update");
		List<String> exportDataTrain = new ArrayList<String>();
		List<String> exportDataTest = new ArrayList<String>();

		FileInputStream fInStream1 = new FileInputStream(dir + "/results/me13pt_certh_run1tmax.txt");
		DataInputStream in1 = new DataInputStream(fInStream1);
		BufferedReader temp1 = new BufferedReader(new InputStreamReader(in1));
		
		Map<String,String> testImageLatLon = new HashMap<String,String>();
		
		temp1.readLine();
		
		String inputLine1 = "";
		String[] input1;
		
		for (int i=0;i<262000;i++){
			inputLine1 = temp1.readLine();
			input1 = inputLine1.split(" ");
			
			testImageLatLon.put(input1[0], ","+input1[3]+","+input1[4]+",");
		}
		temp1.close();
		
		for(int i=1;i<11;i++){

			fstream = new FileInputStream(trainFile+"/metadata_"+i+".csv");
			in = new DataInputStream(fstream);
			temp = new BufferedReader(new InputStreamReader(in));

			String inputLine = temp.readLine();
			inputLine = temp.readLine();
			while(inputLine != null){

				String [] input = inputLine.split(",");
				String output = "";

				if (!testSet.contains(input[0])){
					
					if(input[4].substring(0,1).matches("\"")){
						if((input[4].length()-1)<=0){System.out.println(input[4]);}
						input[4] = (input[4].substring(1, input[4].length()-1));
					}

					input[4] = filter(input[4],bagOfWords);
					
					if(input[4].length()>1){

						String [] coord = latlonMap.get(input[0]);
						output = input[0]+","+input[2]+","+coord[1]+","+coord[2]+","+","+input[1];
						
						exportDataTrain.add(output);

					}
				}else{
					
					if(input[4].substring(0,1).matches("\"")){
						if((input[4].length()-1)<=0){System.out.println(input[4]);}
						input[4] = (input[4].substring(1, input[4].length()-1));
					}
					output = input[0]+","+input[2]+testImageLatLon.get(input[0])+filter(input[4],bagOfWords)+","+input[1];
					
					exportDataTest.add(output);
				}

				inputLine = temp.readLine();
			}
		}

		System.out.println(exportDataTrain.size());

		FileCreator.writeInFile(exportDataTrain, "all_training_Update_Filtered", dir,false);

		FileCreator.writeInFile(exportDataTest, "all_test_Update_Filtered", dir, false);

		temp.close();

	}

	public void createDatasetLinks(String trainFile, String testFile, String dir) throws IOException{

		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader temp = null;

		Set<String> testSet = createTestSet(testFile);
		List<String> exportDataTrain = new ArrayList<String>();
		List<String> exportDataTest = new ArrayList<String>();

		for(int i=10;i<11;i++){

			fstream = new FileInputStream(trainFile+"/metadata_"+i+".csv");
			in = new DataInputStream(fstream);
			temp = new BufferedReader(new InputStreamReader(in));

			String inputLine = temp.readLine();
			inputLine = temp.readLine();
			while(inputLine != null){

				String [] input = inputLine.split(",");
				String output = "";

				if (!testSet.contains(input[0])){
					output = input[0]+" "+input[3];
					exportDataTrain.add(output);
				}else{

					output = input[0]+" "+input[3];
					exportDataTest.add(output);
				}

				inputLine = temp.readLine();
			}
		}

		System.out.println(exportDataTrain.size());

		FileCreator.writeInFile(exportDataTrain, "all_training_links_Update", dir,false);

		FileCreator.writeInFile(exportDataTest, "all_test_links_Update", dir, false);

		temp.close();

	}


	public static String filter(String input,Set<String> bagOfWords){

		String output = "";

		String [] tags = input.split(" ");

		for(int i=0;i<tags.length;i++){
			if((tags[i].contains("geo:") && tags[i].contains("="))
					||(tags[i].contains("geolat") && tags[i].substring(6, tags[i].length()).matches("[0-9]+"))
					||(tags[i].contains("geolon") && tags[i].substring(6, tags[i].length()).matches("[0-9]+"))
					||(tags[i].contains("geolong") && tags[i].substring(7, tags[i].length()).matches("[0-9]+"))
					||(bagOfWords.contains(tags[i]))){
				tags[i] = null;
			}
		}

		for(int i=0;i<tags.length;i++){
			if (!(tags[i] == null)){
				output += (tags[i]+" ");
			}
		}

		return output.substring(0, output.length()>0 ? (output.length()-1) : output.length());
	}

	public static Set<String> createTestSet(String testFile) throws IOException{

		Set<String> testSet = new HashSet<String>();
		
		FileInputStream fstream = new FileInputStream(testFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		String inputLine = temp.readLine();

		while(inputLine != null){
			testSet.add(inputLine);
			inputLine = temp.readLine();
		}

		temp.close();
		
		return testSet;
	}

	public static void createLatLonSet(String latlon) throws IOException{

		FileInputStream fstream = new FileInputStream(latlon);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));

		String inputLine = temp.readLine();
		inputLine = temp.readLine();

		while(inputLine != null){
			String [] input = inputLine.split(" ");
			latlonMap.put(input[0],input);
			inputLine = temp.readLine();
		}

		temp.close();
	}
}