package tools;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import data_objects.ImageMetadata;


public class OrganizeTagsForX {

	
	private Map<String, Map<Integer,Integer>> tagClusterFreqMap;

	
	public OrganizeTagsForX(){
		tagClusterFreqMap = new HashMap<String,Map<Integer,Integer>>();
	}
	
	public Map<String, Map<Integer,Integer>> getTagMap(ImageMetadata [] images) 
			throws IOException{

		for (int i=0; i<images.length; i++){
			
			String [] tags = images[i].getTags().split(" ");
			
			for (int j=0; j<tags.length; j++){
				
				Map<Integer,Integer> clusterFreqMap = tagClusterFreqMap.get(tags[j]);
				
				if (clusterFreqMap == null) {
					Map<Integer,Integer> newClusterFreqMap = new HashMap<Integer,Integer>();
					newClusterFreqMap.put(0, 1);
					newClusterFreqMap.put(images[i].getArea()+1, 1);
					tagClusterFreqMap.put(tags[j], newClusterFreqMap);
				} 
				else {
					Integer clusterFreq = clusterFreqMap.get(images[i].getArea()+1);
					clusterFreqMap.put(0, clusterFreqMap.get(0)+1);
					
					if (clusterFreq == null) {
						clusterFreqMap.put(images[i].getArea()+1, 1);
						tagClusterFreqMap.put(tags[j], clusterFreqMap);
					} 
					else {
						clusterFreqMap.put(images[i].getArea()+1, clusterFreq+1);
						tagClusterFreqMap.put(tags[j], clusterFreqMap);
					}
				}
			}
		}
		
		return tagClusterFreqMap;
	}
}
