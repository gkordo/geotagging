package data_objects;

public class ImageSimilarity{

	private int id;
	private double similarity;

	public ImageSimilarity (int id, double similarity) {
		this.id = id;
		this.similarity = similarity;
	}

	public int getImageId() {
		return id;
	}
	public void setImageId(int id) {
		this.id = id;
	}

	public double getSimilarity() {
		return similarity;
	}
	public void setFreq(int similarity) {
		this.similarity = similarity;
	}
}
