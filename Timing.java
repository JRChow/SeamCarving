import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by jrzhou on 15/4/2017.
 */
public class Timing {
    public static void main(String[] args) {
        int W = 10000;
        int H = 5000;
        int numOfTest = 30;
        double timeSum = 0;

        // remove vertical
        for (int i = 1; i <= numOfTest; i++) {
            Picture pic = SCUtility.randomPicture(W, H);
            SeamCarver sc = new SeamCarver(pic);
            Stopwatch sw = new Stopwatch();
            sc.removeVerticalSeam(sc.findVerticalSeam());
            timeSum += sw.elapsedTime();
            StdOut.println(i + ": Sum of time = " + timeSum);
        }
        StdOut.println("Average Time = " + timeSum/numOfTest);
    }
}
