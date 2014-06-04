package methods;

import com.stromberglabs.cluster.Cluster;
import com.stromberglabs.cluster.Clusterable;
import com.stromberglabs.cluster.KClusterer;
import com.stromberglabs.cluster.KMeansClusterer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data_objects.Area;
import data_objects.AreaCluster;
import data_objects.ImageMetadata;


public class Clustering{


	private Cluster[] clusters;
	private int largestArea, largestAreaSize;
	
	
	public Clustering (ImageMetadata[] images, int K)
	{	
		List<Clusterable> clusterList = new ArrayList<Clusterable>(images.length);
		
		for (int i=0; i<images.length; i++){
			clusterList.add(new Cluster(images[i].getCoord(), i));
		}
		
		KClusterer clusterer = new KMeansClusterer();
		clusters = clusterer.cluster(clusterList, K);
	}
	
	
	
	public Area [] createAreas(ImageMetadata[] images) 
			throws IOException{
		
		int image_Id;
		int  i, counter = 0;

		largestAreaSize = 0;

		List<Area> areas = new ArrayList<Area>();
		
		for (i=0; i<clusters.length; i++){

			Cluster cluster = clusters[i];
			List<Clusterable> items = cluster.getItems();
			int size = cluster.getItems().size();

			if (size != 0){
				
				AreaCluster ncluster = new AreaCluster(cluster);
				
				areas.add(new Area(ncluster));
				
				for (int j=0; j<size; j++){

					Cluster clusterItem = (Cluster) items.get(j);
					
					image_Id = clusterItem.getId();
					images[image_Id].setArea(counter);
									
				}
				
				if (size > largestAreaSize){
					largestArea = counter;
					largestAreaSize = size;
				}
				
				counter++;				
			}
		}

		return areas.toArray(new Area[areas.size()]);
	}
	
	
	public int getLargestArea(){
		return largestArea;
	}
}