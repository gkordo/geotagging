package methods;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data_objects.Area;
import data_objects.ImageMetadata;


public class LanguageModel {

	private int u;

	public LanguageModel(int u){
		this.u = u;
	}


	public Integer computeArea(ImageMetadata image, Area [] areas,
			Map<String, Map<Integer, Integer>> tagClusterFreqMap){

		Integer Ota, maxAreaId = null;
		int K = areas.length, nTag, nArea;
		double Ota_ = 0.0, Ot_a, Ot_a_, Pta, maxP = 0.0, P;

		String [] tags = image.getTags().split(" ");

		for (int i=0; i < K; i++){
			nArea = areas[i].getAreaCluster().getCluster().getItems().size();

			List<String> vocabulary = Arrays.asList(areas[i].getVocabulary());
			
			Set<String> vocabularySet =  new HashSet<String>(vocabulary);

			Pta = 0.0;
			int counter = 0;

			Ot_a = 0.0;
			Ot_a_ = 0.0;
			
			for(int k=0; k<vocabulary.size(); k++){
				
				Ot_a += tagClusterFreqMap.get(vocabulary.get(k)).get(i+1);// h tags[j] anti gia vocub
				
				Ot_a_ += tagClusterFreqMap.get(vocabulary.get(k)).get(0);
				
			}

			for (int j=0; j<tags.length; j++){
				Map<Integer, Integer> freqMap = tagClusterFreqMap.get(tags[j]);

				if (freqMap != null){

					Ota = freqMap.get(i+1);

					if (vocabularySet.contains(tags[j])){

						nTag = freqMap.get(0);
						Ota_ = nTag;
						counter++;

						if (counter==1){
							Pta = (Ota + u * (Ota_ / Ot_a_)) / (Ot_a + u);
						} 
						else {
							Pta *= (Ota + u * (Ota_ / Ot_a_)) / (Ot_a + u);
						}
					}
				}
			}

			P = nArea * Pta;
			
			if ((P > maxP)) {
				maxP = P;
				maxAreaId = i;
			}
		}
		
		return maxAreaId;
	}
}
