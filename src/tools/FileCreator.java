package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import data_objects.ImageMetadata;


public class FileCreator {

	public static void createImageFile (ImageMetadata[] images,  String dir, String fileName) throws IOException{

		System.out.println("Create File For LDA");

		List<String> exportData = new ArrayList<String>();

		for (int i=0; i<images.length; i++){

			String exportString = images[i].getTags();

			exportData.add(exportString);
		}

		writeInFile(exportData, fileName, dir, true);

	}
	
	
	
	public static void createFileWithKeyword (int N, ImageMetadata[] images,  String dir, String keyword) throws IOException{

		System.out.println("Create File For LDA");

		List<String> exportData = new ArrayList<String>();

		for (int i=0; i<N; i++){

			List<String> tags = new ArrayList<String>(Arrays.asList(images[i].getTags().split(" ")));

			String exportString = "";

			if (tags.contains(keyword)){

				for (int j=0; j<tags.size(); j++){

					if (!tags.get(j).equals(keyword)){

						exportString += (tags.get(j) + " ");

					}
				}

				exportData.add(exportString);
			}
		}

		writeInFile(exportData, keyword, dir, true);

	}
	
	
	
	public void createFileBorders (int N, ImageMetadata[] images,  String dir, String keyword, int latL, int latH, int lonL, int lonH) throws IOException{

		System.out.println("Create File For LDA");

		List<String> exportData = new ArrayList<String>();

		int counter = 0;

		for (int i=0; i<N; i++){

			float[] coord = images[i].getCoord();

			String exportString = "";

			if (coord[0]>latL && coord[0]<latH && coord[1]>lonL && coord[1]<lonH){

				counter++;

				exportString = Integer.toString(counter)+","+images[i].getUserId()+","+Float.toString(coord[0])+","+Float.toString(coord[1])+","+images[i].getTags();

				exportData.add(exportString);


			}
		}

		writeInFile(exportData, keyword, dir, true);

	}

	
	
	public static void writeInFile(List<String> exportData, String fileName, String dir, boolean size) throws IOException{

		(new File(dir)).mkdirs();

		FileWriter fstream = new FileWriter(dir + "/" + fileName + ".txt");
		BufferedWriter out = new BufferedWriter(fstream);

		if (size){
			out.write(Integer.toString(exportData.size()) + "\n");
		}

		for (int i=0; i<exportData.size(); i++){
			out.write(exportData.get(i) + "\n");
		}

		out.close();

	}
}