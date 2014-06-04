package data_objects;
import java.io.Serializable;

public class Area implements Serializable{

	private AreaCluster cluster;
	private String [] vocabulary;

	public Area (){
	}
	
	public Area (AreaCluster cluster){
		this.cluster = cluster;
	}

	public AreaCluster getAreaCluster () {
		return cluster;
	}

	public void setVocabulary (String [] vocabulary) {
		this.vocabulary = vocabulary;
	}

	public String [] getVocabulary () {
		return vocabulary;
	}
}