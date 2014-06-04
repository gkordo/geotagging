package compare_objects;
import java.util.Comparator;


public class CompareDistance implements Comparator<Double>{

	public int compare(Double distance1, Double distance2) {
		return distance1 > distance2 ? 1 
				: distance1 < distance2 ? -1 
				: 0;
	}
}