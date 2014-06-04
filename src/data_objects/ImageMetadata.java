package data_objects;

public class ImageMetadata{

	private int area_id;
	private float [] coord;
	private String image_id, user_id, tags;

	public ImageMetadata (String id, String user_id, String tags) {
		this.image_id = id;
		this.user_id = user_id;
		this.tags = tags;
	}

	public String getId () {
		return image_id;
	}

	public String getUserId () {
		return user_id;
	}

	public String getTags () {
		return tags;
	}

	public void setCoord (float lat, float lng){
		coord = new float [2]; 
		coord[0] = lat;
		coord[1] = lng;
	}

	public float [] getCoord () {
		return coord;
	}

	public void setArea (int area_id){
		this.area_id = area_id;
	}

	public int getArea () {
		return area_id;
	}

}