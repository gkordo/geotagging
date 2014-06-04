package compare_objects;
import java.util.Comparator;

import data_objects.ImageSimilarity;


public class CompareSimilarity implements Comparator<ImageSimilarity>{

	public int compare(ImageSimilarity image1, ImageSimilarity image2) {
		return image1.getSimilarity() < image2.getSimilarity() ? 1 
				: image1.getSimilarity() > image2.getSimilarity() ? -1 
				: 0;
	}
}
