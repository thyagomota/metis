import java.io.FileNotFoundException;
import java.util.List;

public class SimilarityMatrixDriver {

    // target similarity
    public static final double TARGET_THRESHOLD = 0.5;

    public static void main(String[] args) throws FileNotFoundException {
        SimilarityMatrix mtx = new SimilarityMatrix("data/dataset2.txt");
//        System.out.println("Sentences:");
//        for (int i = 0; i < mtx.getSize(); i++)
//            System.out.println(i + " " + mtx.getSentence(i));
//        System.out.println();
//        System.out.println("Similarity Matrix:");
//        System.out.println(mtx);
//        System.out.println();
//        double threshold = 0;
//        if (TARGET_SIMILARITY == MEDIUM_SIMILARITY)
//            threshold = MEDIUM_THRESHOLD;
//        else if (TARGET_SIMILARITY == HIGH_SIMILARITY)
//            threshold = HIGH_THRESHOLD;
        System.out.println("Clusters using " + TARGET_THRESHOLD + " similarity threshold");
        List<List<Integer>> clusters = mtx.getClusters(TARGET_THRESHOLD);
        for (List<Integer> cluster : clusters) {
//            if (cluster.size() <= 10)
//                continue;
//            System.out.println(cluster);
            for (int node : cluster)
                // System.out.println(node + " " + mtx.getSentence(node));
                System.out.println(mtx.getSentence(node));

            System.out.println();
        }
    }

}
