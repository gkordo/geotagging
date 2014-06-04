package methods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import compare_objects.CompareSimilarity;
import data_objects.Area;
import data_objects.ImageMetadata;
import data_objects.ImageSimilarity;
import com.stromberglabs.cluster.Cluster;


public class SimilaritySearch {


	private int k, a;
	private double threshold;


	public SimilaritySearch(int k, int a, double threshold){
		this.k = k;
		this.a = a;
		this.threshold = threshold;
	}


	public float[] computeSimilarity(ImageMetadata image, 
			ImageMetadata[] images){

		List<ImageSimilarity> listSim = new ArrayList<ImageSimilarity>();

		String [] testImageTags = image.getTags().split(" ");

		for (int i=0; i<images.length; i++){

			double counter = 0.0;

			Set<String> trainImageTags = new HashSet<String>(Arrays.asList(images[i].getTags().split(" ")));//String [] trainImageTags = images[i].getTags().split(" ");

			for (int j=0; j<testImageTags.length; j++){

				if (trainImageTags.contains(testImageTags[j])){
					counter += 1.0;
				}
			}

			double sjacc = counter / (testImageTags.length 
					+ trainImageTags.size() - counter);

			listSim.add(new ImageSimilarity(i,sjacc));

		}

		int t = 0, size = k;
		float [] coord = null;

		Collections.sort(listSim, new CompareSimilarity());

		if(listSim.size()<k){
			size = listSim.size();
		}

		for (int i=0; i<size; i++){
			if (listSim.get(i).getSimilarity()>threshold){
				t++;
			}
		}

		if (t!=0){
			coord = computeCoordination(images, listSim, t);
		}
		else{
			if (!listSim.isEmpty()){
				coord = computeCoordination(images, listSim, 1);
			}
		}
		
		return coord;
	}

	

	public float[] computeSimilarityUsingArea(ImageMetadata image, ImageMetadata[] images, Area area){

		ImageMetadata [] areaImages = new ImageMetadata[area.getAreaCluster().getCluster().getItems().size()];

		for (int i=0; i<area.getAreaCluster().getCluster().getItems().size(); i++){
			Cluster clusterItem = (Cluster) area.getAreaCluster().getCluster().getItems().get(i);
			areaImages[i] = images[clusterItem.getId()];
		}

		return computeSimilarity(image, areaImages);
	}


	
	public float[] computeCoordination(ImageMetadata[] images, List<ImageSimilarity> listSim, int t){

		double [] loc = new double[3];
		float [] coord = new float[2];

		for (int l=0; l<t; l++){
			loc[0] += Math.pow(listSim.get(l).getSimilarity(),a)
					* Math.cos(images[listSim.get(l).getImageId()].getCoord()[0] * (Math.PI / 180D))
					* Math.cos(images[listSim.get(l).getImageId()].getCoord()[1] * (Math.PI / 180D)) / k;

			loc[1] += Math.pow(listSim.get(l).getSimilarity(),a)
					* Math.cos(images[listSim.get(l).getImageId()].getCoord()[0] * (Math.PI / 180D))
					* Math.sin(images[listSim.get(l).getImageId()].getCoord()[1] * (Math.PI / 180D)) / k;

			loc[2] += Math.pow(listSim.get(l).getSimilarity(),a)
					* Math.sin(images[listSim.get(l).getImageId()].getCoord()[0] * (Math.PI / 180D)) / k;
		}

		coord[0] = (float) (Math.atan2(loc[2], Math.sqrt(Math.pow(loc[0],2) 
				+ Math.pow(loc[1],2))) * (180D/Math.PI));

		coord[1] = (float) (Math.atan2(loc[1], loc[0]) * (180D/Math.PI));

		return coord;
	}
}
