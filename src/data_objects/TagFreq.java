package data_objects;

public class TagFreq{

	private String tag;
	private double freq;
	int nTag,Ota,Ota_, Ot_a, Ot_a_;

	public TagFreq (String tag, double freq, 
			int nt, int Ota, int Ota_, int Ot_a, int Ot_a_) {
		this.tag = tag;
		this.freq = freq;
		this.nTag = nt;
		this.Ota = Ota;
		this.Ota_ = Ota_;
		this.Ot_a = Ot_a;
		this.Ot_a_ = Ot_a_;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}

	public double getFreq() {
		return freq;
	}
	
	public void setnTag(int nTag) {
		this.nTag = nTag;
	}

	public int getnTag() {
		return nTag;
	}

	public void setOta(int Ota) {
		this.Ota = Ota;
	}

	public int getOta() {
		return Ota;
	}

	public void setOta_(int Ota_) {
		this.Ota_ = Ota_;
	}

	public int getOta_() {
		return Ota_;
	}
	public void setOt_a(int Ot_a) {
		this.Ot_a = Ot_a;
	}

	public int getOt_a() {
		return Ot_a;
	}

	public void setOt_a_(int Ot_a_) {
		this.Ot_a_ = Ot_a_;
	}

	public int getOt_a_() {
		return Ot_a_;
	}	
}
