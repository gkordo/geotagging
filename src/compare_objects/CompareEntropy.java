package compare_objects;

import java.util.Comparator;

import data_objects.TopicEntropy;


public class CompareEntropy implements Comparator<TopicEntropy>{

	public int compare(TopicEntropy topic1, TopicEntropy topic2) {
		return topic1.getEntropy() < topic2.getEntropy() ? 1 
				: topic1.getEntropy() > topic2.getEntropy() ? -1 
				: 0;
	}
}