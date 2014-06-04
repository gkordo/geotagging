package compare_objects;
import java.util.Comparator;

import data_objects.TagFreq;


public class CompareTagFreq implements Comparator<TagFreq>{

	public int compare(TagFreq tag1, TagFreq tag2) {
		return tag1.getFreq() < tag2.getFreq() ? 1 
				: tag1.getFreq() > tag2.getFreq() ? -1 
				: 0;
	}
}
