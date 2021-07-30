import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class SimilarityMatrix {

    private double matrix[][];
    private String map[];

    public SimilarityMatrix(int size) {
        matrix = new double[size][size];
        map = new String[size];
        for (int i = 0; i < size; i++) {
            map[i] = "" + i;
            for (int j = 0; j < size; j++)
                if (i == j)
                    matrix[i][i] = 1;
                else
                    matrix[i][j] = 0;
        }
    }

    public SimilarityMatrix(String fileName) throws FileNotFoundException {
        Scanner file = new Scanner(new FileInputStream(fileName));
        List<String> keywords = new LinkedList<>();
        while (file.hasNext()) {
            String sentence = file.nextLine().strip();
            // removes punctuations from sentence
            sentence = sentence.replaceAll("\\p{Punct}", "");
            // removes capitalization
            sentence = sentence.toLowerCase();
            // sorts words in sentence alphabetically, also removing duplicates
            Set<String> treeSet = new TreeSet<>();
            for (String word: sentence.split(" "))
                treeSet.add(word.strip());
            String keyword = "";
            for (String word: treeSet)
                keyword += word + " ";
            keyword = keyword.strip();
            if (!keywords.contains(keyword))
                keywords.add(keyword);
        }
        int size = keywords.size();
        matrix = new double[size][size];
        map = new String[size];
        for (int i = 0; i < size; i++) {
            map[i] = keywords.get(i);
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

    public int getSize() {
        return matrix.length;
    }

    public void setSimilarity(int i, int j, double value) {
        matrix[i][j] = matrix[j][i] = value;
    }

    public double getSimilarity(int i, int j) {
        return matrix[i][j];
    }

    public String getSentence(int i) {
        return map[i];
    }

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

    private double compareSimilarityWithCluster(List<Integer> cluster, int node) {
        if (cluster.contains(node))
            return 1;
        double avg = 0;
        for (int clusterNode: cluster)
            avg += matrix[clusterNode][node];
        return avg / cluster.size();
    }

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
