package geotag;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.ejml.alg.block.GeneratorBlockInnerMultiplication;

import compare_objects.CompareDistance;
import jgibblda.LDA;
@SuppressWarnings("unused")

class LocationEstimation{

	public static void main (String args[]) throws IOException, ClassNotFoundException {

		Properties properties = new Properties();

		System.out.println("Program Started");

		properties.load(new FileInputStream("config.properties"));

		int N = Integer.parseInt(properties.getProperty("N"));
		int T = Integer.parseInt(properties.getProperty("T"));

		String method = properties.getProperty("method");

		String trainFile = properties.getProperty("trainFile");

		String testFile = properties.getProperty("testFile");

		String dir = properties.getProperty("dir");

		if (method.equals("lm")){



			int K = Integer.parseInt(properties.getProperty("K"));
			int m = Integer.parseInt(properties.getProperty("m"));
			int u = Integer.parseInt(properties.getProperty("u"));

			String action = properties.getProperty("action");

			LMmain mainProgram = new LMmain(N, T, K, m, u,trainFile, testFile, action, dir);

		}else if (method.equals("ss")){

			int k = Integer.parseInt(properties.getProperty("k"));
			int a = Integer.parseInt(properties.getProperty("a"));
			double threshold = Double.parseDouble(properties.getProperty("threshold"));

			SSmain mainProgram = new SSmain(N, T, k, a, threshold, trainFile, testFile);

		}else if (method.equals("ha")){

			int K = Integer.parseInt(properties.getProperty("K"));
			int m = Integer.parseInt(properties.getProperty("m"));
			int u = Integer.parseInt(properties.getProperty("u"));

			String action = properties.getProperty("action");

			int k = Integer.parseInt(properties.getProperty("k"));
			int a = Integer.parseInt(properties.getProperty("a"));
			double threshold = Double.parseDouble(properties.getProperty("threshold"));

			HAmain mainProgram = new HAmain(N, T, K, m, u, k, a, threshold, trainFile, testFile, action, dir);

		}else if (method.equals("LDA")){

			String keyword = properties.getProperty("keyword");


			String ntopics = properties.getProperty("ntopics");
			String twords = properties.getProperty("twords");
			String gtopics = properties.getProperty("gtopics");
			String gwords = properties.getProperty("gwords");

			double entropyThreshold = Double.parseDouble(properties.getProperty("entropyThreshold"));

			int K = Integer.parseInt(properties.getProperty("K"));
			String action = properties.getProperty("action");

			LDAmain mainProgram = new LDAmain(N, T, K, keyword, dir, ntopics, twords, gtopics, gwords, entropyThreshold, trainFile, testFile, action);
		}
		System.out.println("Program Finished");

	}
}