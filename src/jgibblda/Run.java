package jgibblda;


public class Run {

	public static void main(String[] args) {
		
		System.out.println("Program Started");
		
		String[] arguments = new String[] {"-est", "-ntopics", "200", "-twords", "50", 
				"-savestep", "500", "-dir", args[0], 
				"-dfile", "all_training.txt"};

		LDA.main(arguments);

		System.out.println("Program Finished");
		
	}
}
