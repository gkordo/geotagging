package data_objects;

public class ImageTopic{

	private int topicId, clusterId;
	private double similarity;

	public ImageTopic (int topicId, int clusterId, double similarity) {
		this.topicId = topicId;
		this.clusterId = clusterId;
		this.similarity = similarity;
	}

	
	public int getTopicId() {
		return topicId;
	}
	public void setTopcId(int topicId) {
		this.topicId = topicId;
	}
	
	public int getClusterId() {
		return clusterId;
	}
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	public double getSimilarity() {
		return similarity;
	}
	public void setSim(int similarity) {
		this.similarity = similarity;
	}
}
