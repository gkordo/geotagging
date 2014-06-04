package tools;

import com.stromberglabs.cluster.Cluster;
import com.stromberglabs.cluster.Clusterable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import data_objects.Area;
import data_objects.ImageMetadata;


public class StoreLoad {
	
	private String file;
	
	public StoreLoad(int K, String dir){
		
		(new File(dir)).mkdirs();
		file = dir + "/areas_" + K;
		
	}
	
	public void store (int K, Area[] areas, int largestArea) throws IOException{

		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);

		oos.writeObject(areas.length);
		
		for(int i=0; i<areas.length ;i++){
			oos.writeObject(areas[i]);
		}
		
		oos.writeObject(largestArea);
		
		oos.close();
	}
	
	
	public Area[] load (int K, int largestArea, ImageMetadata[] trainSet) throws IOException, ClassNotFoundException{

		FileWriter fstream = new FileWriter("clustering/out");
		BufferedWriter out = new BufferedWriter(fstream);
		
		Area[] areas = null;
		
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		Integer areaSize = (Integer) ois.readObject();
		
		areas = new Area[areaSize];
		
		for(int i=0; i<areaSize ;i++){
			areas[i] = (Area) ois.readObject();
			
			Cluster cluster = areas[i].getAreaCluster().getCluster();
			List<Clusterable> items = cluster.getItems();
			
			int size = cluster.getItems().size();
			
			
			FileWriter fstream_x = new FileWriter("clustering/out_" + i );
			BufferedWriter out_t = new BufferedWriter(fstream_x);
			
			for (int j=0; j<size; j++){
				
				Cluster clusterItem = (Cluster) items.get(j);
				
				int image_Id = clusterItem.getId();
				
				trainSet[image_Id].setArea(i);
			}
			
			out_t.close();
			
		}

		out.close();
		
		largestArea = (Integer) ois.readObject();
		
		ois.close();
		
		return areas;
		
	}
}