package tools;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import data_objects.ImageMetadata;


public class DatasetLoader{

	public DatasetLoader () {}

	public ImageMetadata[] initializeDataset (String fileName, int N) 
			throws IOException{
		
		float lat, lng;
		FileInputStream fstream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader temp = new BufferedReader(new InputStreamReader(in));
		String inputLine;

		ImageMetadata [] images = new ImageMetadata [N]; 
		
		for(int i=0; i<N ;i++){
			
			inputLine = temp.readLine();
			String [] input = inputLine.split(",");
			
			String id = input[0];
			
			if (input.length > 4){
				images[i] = new ImageMetadata(id, input[1], input[4]);
			} 
			else {
				images[i] = new ImageMetadata(id, input[1], "");
			}
			
			lat = Float.parseFloat(input[2]);
			lng = Float.parseFloat(input[3]);
			images[i].setCoord(lat, lng);

		}

		temp.close();
		in.close();
		return images;
	}
}