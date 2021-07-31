/*
 * SimilarityMatrix.java
 * Created at: July 29, 2021
 * Last updated on: July 31, 2021
 * Author: Thyago M.
 */

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Groups sentences (obtained from a file) into groups based on a similarity threshold value.
 */
public class SimilarityMatrixDriver {

    // target similarity (change it to set the desired similarity)
    public static final double TARGET_THRESHOLD = 0.5;

    public static void main(String[] args) throws FileNotFoundException {
        // change the name of the file to set different group of sentences
        SimilarityMatrix mtx = new SimilarityMatrix("data/dataset2.txt");
        System.out.println("Clusters using " + TARGET_THRESHOLD + " similarity threshold");
        List<List<Integer>> clusters = mtx.getClusters(TARGET_THRESHOLD);
        for (List<Integer> cluster : clusters) {
            for (int node : cluster)
                System.out.println(mtx.getSentence(node));
            System.out.println();
        }
    }
}
