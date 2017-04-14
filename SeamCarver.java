import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;

public class SeamCarver {
    private Picture picture; // a mutable copy of the picture
    private double[][] energyMat; // the energy matrix

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture); // make a defensive copy
    }

    // current picture
    public Picture picture() {
        return this.picture;
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
        int xLeft = x == 0 ? width()-1 : x-1;
        int xRight = x == width()-1? 0 : x+1;
        Color leftColor = picture.get(xLeft, y);
        Color rightColor = picture.get(xRight, y);
        return sumSquaredDiff(leftColor, rightColor);
    }

    // helper function for computing the square of the y-gradient
    private double squareOfYGradient(int x, int y) {
        int yUp = y == 0 ? height()-1: y-1;
        int yDown = y == height()-1 ? 0 : y+1;
        Color upColor = picture.get(x, yUp);
        Color downColor = picture.get(x, yDown);
        return sumSquaredDiff(upColor, downColor);
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IndexOutOfBoundsException("Coordinates out of range!");
        // energy squared (avoid square root calculation for efficiency)
        // TODO: OPTIMIZATION
//        return squareOfXGradient(x, y) + squareOfYGradient(x, y);
        // FIXME: DEBUG
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
        // TODO: generalise
        int minX = -1;
        double minEnergy = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width(); x++) {
            if (energyMat[height()-1][x] < minEnergy) {
                minEnergy = energyMat[height()-1][x];
                minX = x;
            }
        }
        return minX;
    }

    // return the x-coordinate of the neighbours above (x, y) with min energy
    private int xMinEnergyAbove(int x, int y) {
        // TODO: generalise
        int minX = -1;
        double minEnergy = Double.POSITIVE_INFINITY;
        int xLeft = x == 0 ? 0 : x-1;
        int xRight = x == width()-1 ? width()-1 : x+1;
        if (energyMat[y-1][xLeft] < minEnergy) {
            minEnergy = energyMat[y-1][xLeft];
            minX = xLeft;
        }
        if (energyMat[y-1][x] < minEnergy) {
            minEnergy = energyMat[y-1][x];
            minX = x;
        }
        if (energyMat[y-1][xRight] < minEnergy) {
            minX = xRight;
        }
        return minX;
    }

    // compute and set the minimum sum of energy at a given pixel
    private void computeMinEnergySum(int x, int y) {
        // TODO: generalise
        int xLeft = x == 0 ? 0 : x-1;
        int xRight = x == width()-1 ? width()-1 : x+1;
        double currentEnergy = energyMat[y][x];
        energyMat[y][x] = Math.min(currentEnergy + energyMat[y-1][xLeft],
                Math.min(currentEnergy + energyMat[y-1][x],
                        currentEnergy + energyMat[y-1][xRight]));
    }

    // transpose a picture
    private Picture transpose(Picture originalPic) {
        // TODO: delete
        Picture newPic = new Picture(originalPic.height(), originalPic.width());
        for (int y = 0; y < originalPic.height(); y++) {
            for (int x = 0; x < originalPic.width(); x++) {
                newPic.set(y, x, originalPic.get(x, y));
            }
        }
        return newPic;
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
        seam[height()-1] = xMinEnergy;
        for (int i = height()-2; i >= 0; i--) {
            xMinEnergy = xMinEnergyAbove(xMinEnergy, i+1);
            seam[i] = xMinEnergy;
        }
        return seam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // TODO: optimise
        int[] seam;
        picture = transpose(picture);
        seam = findVerticalSeam();
        picture = transpose(picture);
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        Picture newPic = new Picture(width(), height()-1);
        // remember iterators for each column
        int[] itrs = new int[seam.length];
        // row-major for cache efficiency
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (seam[x] == y) continue; // jump across the seam
                newPic.set(x, itrs[x]++, picture.get(x, y));
            }
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        Picture newPic = new Picture(width()-1, height());
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

    // do unit testing of this class
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        StdOut.printf("%d-by-%d image\n", picture.width(), picture.height());

        SeamCarver sc = new SeamCarver(picture);

        StdOut.printf("Energy Matrix\n");
        sc.constructEnergyMat();
        for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
                System.out.printf("%.2f  ", sc.energyMat[i][j]);
            }
            StdOut.print("\n");
        }
    }

}