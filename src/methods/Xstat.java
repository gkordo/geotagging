package methods;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import compare_objects.CompareTagFreq;
import data_objects.Area;
import data_objects.TagFreq;

public class Xstat{

	
	private int N, m;
	
	
	public Xstat( int N, int m) {
		this.N = N;
		this.m = m;
	}

	public void setAreasVocadulary (Map<String, Map<Integer, 
			Integer>> tagClusterFreqMap, Area [] areas) throws IOException{

		Integer nTag, nArea, Ota, Ota_, Ot_a, Ot_a_;
		int K = areas.length;
		double X;
		
		for (int i=0; i < K; i++){

			nArea = areas[i].getAreaCluster().getCluster().getItems().size();
			List<TagFreq> listXsort = new ArrayList<TagFreq>();

			for (Entry<String, Map<Integer, Integer>> entry 
					: tagClusterFreqMap.entrySet()) {

				String tag = entry.getKey();
				Map<Integer, Integer> freqMap = tagClusterFreqMap.get(tag);
				
				Ota = freqMap.get(i+1);

				if (Ota != null) {
					nTag = freqMap.get(0);
					Ota_ = nTag - Ota;
					Ot_a = nArea - Ota;
					Ot_a_ = N - nArea - nTag + Ota;

					X = (Math.pow(Ota * N - nTag * nArea,2) / (nTag * nArea) / N 
							+ Math.pow(Ota_ * N - nTag * (N - nArea),2) / (nTag * (N - nArea)) / N 
							+ Math.pow(Ot_a * N - (N - nTag) * nArea,2) / ((N - nTag) * nArea) / N 
							+ Math.pow(Ot_a_ * N - (N - nTag) * (N - nArea),2) / ((N - nTag) * (N - nArea)) / N);

					if ( ! tag.equals("")){
						TagFreq tempTag = new TagFreq(tag, X,
								nTag, Ota, Ota_, Ot_a, Ot_a_);
						listXsort.add(tempTag);
					}
				}
			}

			Collections.sort(listXsort, new CompareTagFreq());

			int t = m;

			if (m > listXsort.size()){
				t = listXsort.size();
			}
			
			String [] vocabulary = new String[t];
			
			for (int k=0; k<t; k++){
				vocabulary[k] = listXsort.get(k).getTag();
			}

			areas[i].setVocabulary(vocabulary);
		}
	}
}