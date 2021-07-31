/*
 * SimilarityMatrix.java
 * Created at: July 29, 2021
 * Last updated on: July 31, 2021
 * Author: Thyago M.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Builds a similarity matrix of sentences with scores varying from 0 to 1.
 */
public class SimilarityMatrix {

    private double matrix[][];
    private String sentences[];

    /**
     * Initializes a similarity matrix of a given size, setting the similarity scores to 0 (if nodes are different) and 1 (if nodes are the same).
     * @param size the number of nodes of the matrix
     */
    public SimilarityMatrix(int size) {
        matrix = new double[size][size];
        sentences = new String[size];
        for (int i = 0; i < size; i++) {
            sentences[i] = "" + i;
            for (int j = 0; j < size; j++)
                if (i == j)
                    matrix[i][i] = 1;
                else
                    matrix[i][j] = 0;
        }
    }

    /**
     * Initializes a similarity matrix from sentences obtained from a given file.
     * @param fileName a file containing sentences (one per line)
     * @throws FileNotFoundException
     */
    public SimilarityMatrix(String fileName) throws FileNotFoundException {
        Scanner file = new Scanner(new FileInputStream(fileName));
        List<String> keywords = new LinkedList<>();
        while (file.hasNext()) {
            String sentence = file.nextLine().strip();
            // removes punctuations from sentence
            sentence = sentence.replaceAll("\\p{Punct}", "");
            // removes capitalization
            sentence = sentence.toLowerCase();
            // remove duplicate words
            Set<String> set = new LinkedHashSet<>();
            for (String word: sentence.split(" "))
                set.add(word.strip());
            String keyword = "";
            for (String word: set)
                keyword += word + " ";
            keyword = keyword.strip();
            if (!keywords.contains(keyword))
                keywords.add(keyword);
        }
        int size = keywords.size();
        matrix = new double[size][size];
        sentences = new String[size];
        for (int i = 0; i < size; i++) {
            sentences[i] = keywords.get(i);
            for (int j = 0; j < size; j++)
                if (i == j)
                    matrix[i][i] = 1;
                else if (i > j)
                    matrix[i][j] = compute(i, j);
        }
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (i < j)
                    matrix[i][j] = matrix[j][i];
    }

    /**
     * Returns the number of nodes of the similarity matrix.
     * @return
     */
    public int getSize() {
        return matrix.length;
    }

    /**
     * Sets the similarity score between two given nodes.
     * @param i first node
     * @param j second node
     * @param value similarity score
     */
    public void setSimilarity(int i, int j, double value) {
        matrix[i][j] = matrix[j][i] = value;
    }

    /**
     * Returns the similarity score between two given nodes.
     * @param i first node
     * @param j second node
     * @return similarity score
     */
    public double getSimilarity(int i, int j) {
        return matrix[i][j];
    }

    /**
     * Returns the sentence associated with a given node.
     * @param i a node
     * @return a sentence
     */
    public String getSentence(int i) {
        return sentences[i];
    }

    /**
     * Returns a string representation of the similarity matrix.
     * @return
     */
    @Override
    public String toString() {
        String out = "      ";
        for (int i = 0; i < matrix.length; i++)
            out += String.format("%-5d ", i);
        out += "\n";
        for (int i = 0; i < matrix.length; i++) {
            out += String.format("%-5d ", i);
            for (int j = 0; j < matrix.length; j++)
                if (i == j)
                    out += "  -   ";
                else if (i > j)
                    out += String.format("%1.3f ", matrix[i][j]);
                else
                    break;
            out += "\n";
        }
        return out;
    }

    /**
     * A cluster is a list of nodes. This method returns the average similarity score between a given node and the nodes of the (also) given cluster.
     * @param cluster a list of nodes
     * @param node a node
     * @return a similarity score
     */
    private double compareSimilarityWithCluster(List<Integer> cluster, int node) {
        if (cluster.contains(node))
            return 1;
        double avg = 0;
        for (int clusterNode: cluster)
            avg += matrix[clusterNode][node];
        return avg / cluster.size();
    }

    /**
     * Computes the similarity score of two given nodes.
     * @param i first node
     * @param j second node
     * @return a similarity score
     */
    private double compute(int i, int j) {
        String strA = getSentence(i);
        String strB = getSentence(j);
        int total = strA.split(" ").length;
        int common = 0;
        Stemmer stemmer = new Stemmer();
        for (String wordB: strB.split(" ")) {
            stemmer.add(wordB); stemmer.stem(); wordB = stemmer.toString();
            boolean found = false;
            for (String wordA: strA.split(" ")) {
                stemmer.add(wordA); stemmer.stem(); wordA = stemmer.toString();
                if (wordB.equals(wordA)) {
                    found = true;
                    break;
                }
            }
            if (found)
                common++;
            else
                total++;
        }
        return (double) common / total;
    }

    /**
     * Returns a list of clusters based on a given similarity threshold.
     * @param threshold a similarity threshold score between 0 and 1
     * @return a list of clusters
     */
    public List<List<Integer>> getClusters(double threshold) {
        LinkedList<List<Integer>> clusters = new LinkedList<>();
        Queue<Integer> toProcess = new LinkedList<>();
        for (int i = 0; i < matrix.length; i++)
            toProcess.add(i);
        while (!toProcess.isEmpty()) {
            int node = toProcess.poll();
            // find most similar cluster
            List<Integer> bestCluster = null;
            double bestValue = 0;
            for (List<Integer> cluster: clusters) {
                double value = compareSimilarityWithCluster(cluster, node);
                if (value > threshold && value > bestValue) {
                    bestCluster = cluster;
                    bestValue = value;
                }
            }
            // in case no cluster was found, create a new cluster with the node
            if (bestCluster == null) {
                List<Integer> newCluster = new LinkedList<>();
                newCluster.add(node);
                clusters.add(newCluster);
            }
            // else, add the node to the cluster found
            else
                bestCluster.add(node);
        }
        return clusters;
    }
}