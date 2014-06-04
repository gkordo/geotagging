package data_objects;

public class TopicEntropy {
	
	private String tags;
	private double entropy;

	public TopicEntropy (String tags, double similarity) {
		this.tags = tags;
		this.entropy = similarity;
	}

	public String getTags() {
		return tags;
	}
	public void setTags(String id) {
		this.tags = id;
	}

	public double getEntropy() {
		return entropy;
	}
	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}
}
