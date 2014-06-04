package compare_objects;

import java.util.Comparator;

import data_objects.ImageTopic;


public class CompareTopics implements Comparator<ImageTopic>{

	public int compare(ImageTopic topic1, ImageTopic topic2) {
		return topic1.getSimilarity() < topic2.getSimilarity() ? 1 
				: topic1.getSimilarity() > topic2.getSimilarity() ? -1 
				: 0;
	}
}