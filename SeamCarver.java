/******************************************************************************
 *  Name:    Jingran Zhou
 *  NetID:   jingranz
 *  Precept: P03
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description:  Seam Carver.
 ******************************************************************************/

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class SeamCarver {
    private Picture picture; // a mutable copy of the picture
    private double[][] energyMat; // the energy matrix

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new NullPointerException("Picture cannot be null!");
        this.picture = new Picture(picture); // make a defensive copy
    }

    // current picture
    public Picture picture() {
        return new Picture(this.picture); // mutable copy
    }

    // width of current picture
    public int width() {
        return this.picture.width();
    }

    // height of current picture
    public int height() {
        return this.picture.height();
    }

    // helper function for calculating the sum of squared differences in the
    // red, green and blue components of two colors
    private double sumSquaredDiff(Color color1, Color color2) {
        return Math.pow(color1.getRed() - color2.getRed(), 2)
                + Math.pow(color1.getGreen() - color2.getGreen(), 2)
                + Math.pow(color1.getBlue() - color2.getBlue(), 2);
    }

    // helper function for computing the square of the x-gradient
    private double squareOfXGradient(int x, int y) {
        int xLeft = x - 1;
        int xRight = x + 1;
        if (x == 0) xLeft = width() - 1;
        if (x == width() - 1) xRight = 0;
        Color leftColor = picture.get(xLeft, y);
        Color rightColor = picture.get(xRight, y);
        return sumSquaredDiff(leftColor, rightColor);
    }

    // helper function for computing the square of the y-gradient
    private double squareOfYGradient(int x, int y) {
        int yUp = y - 1;
        int yDown = y + 1;
        if (y == 0) yUp = height() - 1;
        if (y == height() - 1) yDown = 0;
//        StdOut.println("Calculating " + x + ", " + y);
        Color upColor = picture.get(x, yUp);
//        StdOut.println(upColor);
//        StdOut.println("(x, yDown) = (" + x + ", " + yDown + ")");
        Color downColor = picture.get(x, yDown);
        return sumSquaredDiff(upColor, downColor);
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IndexOutOfBoundsException("Coordinates out of range!");
        // energy squared (avoid square root calculation for efficiency)
//        return squareOfXGradient(x, y) + squareOfYGradient(x, y);
        // STANDARD
        return Math.sqrt(squareOfXGradient(x, y) + squareOfYGradient(x, y));
    }

    // helper function to construct the energy matrix based on the graph
    private void constructEnergyMat() {
        energyMat = new double[height()][width()];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                energyMat[y][x] = energy(x, y);
            }
        }
    }

    // return the x-coordinate of the pixel in the last row with min energy
    private int xMinEnergy() {
        int minX = -1;
        double minEnergy = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width(); x++) {
            if (energyMat[height() - 1][x] < minEnergy) {
                minEnergy = energyMat[height() - 1][x];
                minX = x;
            }
        }
        return minX;
    }

    // return the x-coordinate of the neighbours above (x, y) with min energy
    private int xMinEnergyAbove(int x, int y) {
        int minX = -1;
        double minEnergy = Double.POSITIVE_INFINITY;
        int xLeft = x - 1;
        int xRight = x + 1;
        if (x == 0) xLeft = 0;
        if (x == width() - 1) xRight = width() - 1;
        if (energyMat[y - 1][xLeft] < minEnergy) {
            minEnergy = energyMat[y - 1][xLeft];
            minX = xLeft;
        }
        if (energyMat[y - 1][x] < minEnergy) {
            minEnergy = energyMat[y - 1][x];
            minX = x;
        }
        if (energyMat[y - 1][xRight] < minEnergy) {
            minX = xRight;
        }
        return minX;
    }

    // compute and set the minimum sum of energy at a given pixel
    private void computeMinEnergySum(int x, int y) {
        int xLeft = x - 1;
        int xRight = x + 1;
        if (x == 0) xLeft = 0;
        if (x == width() - 1) xRight = width() - 1;
        energyMat[y][x] += Math.min(energyMat[y - 1][xLeft],
                Math.min(energyMat[y - 1][x], energyMat[y - 1][xRight]));
    }

    // transpose a picture
    private void transpose() {
        Picture newPic = new Picture(picture.height(), picture.width());
        for (int y = 0; y < picture.height(); y++) {
            for (int x = 0; x < picture.width(); x++) {
                newPic.set(y, x, picture.get(x, y));
            }
        }
        this.picture = newPic;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] seam = new int[height()];
        constructEnergyMat();
        // compute best min energies
        for (int y = 1; y < height(); y++)
            for (int x = 0; x < width(); x++)
                computeMinEnergySum(x, y);
        // reconstruct the path
        int xMinEnergy = xMinEnergy();
        seam[height() - 1] = xMinEnergy;
        for (int i = height() - 2; i >= 0; i--) {
            xMinEnergy = xMinEnergyAbove(xMinEnergy, i + 1);
            seam[i] = xMinEnergy;
        }
        return seam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        int[] seam;
        transpose();
        seam = findVerticalSeam();
        transpose();
        return seam;
    }

    // helper function to validate a vertical seam
    private boolean isValidVerticalSeam(int[] seam) {
        if (seam.length != height()) return false;
        if (seam[0] < 0 || seam[0] >= width()) return false;
        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= width()) return false;
            if (Math.abs(seam[i] - seam[i - 1]) > 1) return false;
        }
        return true;
    }

    // helper function to validate a horizontal seam
    private boolean isValidHorizontalSeam(int[] seam) {
        if (seam.length != width()) return false;
        if (seam[0] < 0 || seam[0] >= height()) return false;
        for (int i = 1; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= height()) return false;
            if (Math.abs(seam[i] - seam[i - 1]) > 1) return false;
        }
        return true;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new NullPointerException("Seam cannot be null!");
        if (!isValidHorizontalSeam(seam))
            throw new IllegalArgumentException("Seam not valid!");
        if (height() == 1)
            throw new IllegalArgumentException("Height is 1. Cannot remove!");

        Picture newPic = new Picture(width(), height() - 1);
        // remember iterators for each column
        int[] itrs = new int[seam.length];
        // row-major for cache efficiency
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (seam[x] == y) continue; // jump across the seam
                newPic.set(x, itrs[x]++, picture.get(x, y));
            }
        }
        this.picture = newPic;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new NullPointerException("Seam cannot be null!");
        if (!isValidVerticalSeam(seam))
            throw new IllegalArgumentException("Seam not valid!");
        if (width() == 1)
            throw new IllegalArgumentException("Width is 1. Cannot remove!");

        Picture newPic = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            // iterator for filling in the rows of the new picture
            int itr = 0;
            for (int x = 0; x < width(); x++) {
                if (x == seam[y]) continue; // jump across the seam
                newPic.set(itr++, y, picture.get(x, y));
            }
        }
        this.picture = newPic;
    }

    // helper function to print the energy matrix
    private void printEnergyMat() {
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                String element = String.format("%.2f", energyMat[i][j]);
                System.out.format("%-10s", element);
            }
            StdOut.print("\n");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        StdOut.println("=================== 1x1 Test ===================");
        Picture tiny = new Picture(1, 1);
        tiny.set(0, 0, Color.RED);
        SeamCarver scTiny = new SeamCarver(tiny);
        scTiny.constructEnergyMat();
        scTiny.printEnergyMat();
        StdOut.println("=================== 8x1 Test ===================");
        Picture eightByOne = new Picture("1x8.png");
        SeamCarver sc8x1 = new SeamCarver(eightByOne);
        sc8x1.transpose();
        StdOut.println("width = " + sc8x1.width());
        StdOut.println("height = " + sc8x1.height());
        sc8x1.constructEnergyMat();
        sc8x1.printEnergyMat();
        // file name here ;)
        Picture picture = new Picture(args[0]);
        StdOut.printf("===================%d-by-%d image===================\n",
                picture.width(), picture.height());
        StdOut.println("=================== Constructor ===================");
        SeamCarver sc = new SeamCarver(picture);
        StdOut.println("=================== picture() ===================");
        StdOut.println("pass => " + (sc.picture().equals(picture)));
        StdOut.println("================ width() & height() ================");
        StdOut.println("width: pass => " + (sc.width() == picture.width()));
        StdOut.println("height: pass => " + (sc.height() == picture.height()));
        StdOut.println("=================== energy() ===================");
        StdOut.printf("[Energy Matrix]\n");
        sc.constructEnergyMat(); // energy() is called inside this function
        for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
                System.out.printf("%.2f  ", sc.energyMat[i][j]);
            }
            StdOut.print("\n");
        }
        StdOut.println("================ findVerticalSeam() ================");
        int[] vSeam = sc.findVerticalSeam();
        for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
                String element = String.format("%.2f", sc.energyMat[i][j]);
                if (vSeam[i] == j) element += "*";
                System.out.format("%-10s", element);
            }
            StdOut.print("\n");
        }
        StdOut.println("================ removeVerticalSeam() ===============");
        sc.removeVerticalSeam(vSeam);
        sc.constructEnergyMat();
        sc.printEnergyMat();
        StdOut.println("=============== findHorizontalSeam() ===============");
        int[] hSeam = sc.findHorizontalSeam();
        for (int i = 0; i < sc.width(); i++) {
            for (int j = 0; j < sc.height(); j++) {
                String element = String.format("%.2f", sc.energyMat[i][j]);
                if (hSeam[i] == j) element += "#";
                System.out.format("%-10s", element);
            }
            StdOut.print("\n");
        }
        StdOut.println("=============== removeHorizontalSeam() ==============");
        sc.removeVerticalSeam(vSeam);
        sc.constructEnergyMat();
        sc.printEnergyMat();
    }
}