package data_objects;

import java.io.Serializable;
import com.stromberglabs.cluster.Cluster;

public class AreaCluster
   implements Serializable
 {
	Cluster cluster;
	
	public AreaCluster(Cluster cluster){
		this.cluster = cluster;
	}
	
	public Cluster getCluster (){
		return cluster;
	}
 }