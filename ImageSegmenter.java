import java.awt.Color;
import java.util.HashSet;
import java.util.Collections;
import java.util.HashMap;
import java.lang.Math;
import java.util.ArrayList;

/** Partitions a given image into similar segments and assigns a random color to each 
 * segment. 
 *
 * @author Axel Fries
 * @author William Clark
 *
 * Time Spent: 6 hours
 */
public class ImageSegmenter {

    /** Creates a grid of pixels given the colors of each pixel.
     *
     * This method takes in a grid of colors and assigns each color to a 
     * pixel which is in turn put into a grid.
     *
     * @param rgbArray - a grid with colors in each position.
     * 
     * @return pixelGraph - a grid with pixels in each position.
     */
    public static Pixel[][] pixelGraph(Color[][] rgbArray){
        Pixel[][] pixelGraph = new Pixel[rgbArray.length][rgbArray[0].length];
        for (int i = 0; i < rgbArray.length; i++){
            for(int j = 0; j < rgbArray[0].length; j++){ 
                pixelGraph[i][j] = new Pixel(i, j, rgbArray[i][j]); 
            }
        }
        return pixelGraph;
    }

    /** Assigns random colors to the segmented pixels and places 
     * them into a new color grid.
     *
     * This method takes the orignial color grid, creates segments with 
     * all of the pixels, and assigns a random color to each segment.
     *
     * @param rgbArray - a grid with colors in each position
     * @param granularity - controls the size of each segment
     *
     * @return rgbArray - a new color grid with random color assignments to 
     *                    each pixel within every segment
     */
    public static Color[][] segment(Color[][] rgbArray, double granularity) {
        
        System.out.println("Size of rgbArray: row: " + rgbArray.length + " col: " + rgbArray[0].length);

        Pixel[][] pixelGraph = pixelGraph(rgbArray);

        System.out.println("pixelGraph");

        HashSet<Edge> edgeTemp = new HashSet<Edge>();
        edgeTemp = edge(pixelGraph);
        ArrayList<Edge> E = new ArrayList<Edge>();
        
        for (Edge current: edgeTemp){
            E.add(current);
        }

        Collections.sort(E);

        DisjointSetForest forest = new DisjointSetForest(pixelGraph);
        merge(forest, E, pixelGraph, granularity);
        rgbArray = assignColor(pixelGraph, forest);

        System.out.println("AssignColor: ");
        return rgbArray;
    }

    /** Builds the list of edges between each pixel. 
     * 
     * This method builds a list of edges that connect each pixel
     * in the pixel grid.
     *
     * @param pixelGraph - a grid with pixels as each element
     * @return edges - a list of edges
     */
    public static HashSet<Edge> edge(Pixel[][] pixelGraph){

        HashSet<Edge> edges = new HashSet<Edge>();
        
        for (int row = 0; row < pixelGraph.length; row++){
            for (int col = 0; col < pixelGraph[row].length; col++){
                for (int i = -1; i < 2; i++){
                    for (int j = -1; j < 2; j++){
                        if (row + i >= 0 && col + j >= 0
                        && row + i < pixelGraph.length && 
                        col + j < pixelGraph[row].length &&
                        (row != row+i || col != col+j)
                        ){
                            Edge temp = new Edge(pixelGraph[row][col], 
                                        pixelGraph[row+i][col+j]);
                            Edge tempFlipped = new Edge(pixelGraph[row+i][col+j],
                                                pixelGraph[row][col]);
                            if(!edges.contains(temp) && !edges.contains(tempFlipped)){
                                edges.add(temp);
                            }
                        }
                    }
                }

            }
        }
        System.out.println("edge");
        return edges;
    }

    /** Creates the segments of pixels.
     *
     * This method takes all similar pixels and places them into 
     * segments. 
     * 
     * @param forest - the disjoint set forest object.
     * @param edges - the list of edges
     * @param pixelGraph - a grid with pixels in each position
     * @param g - the granularity for the image
     */
    public static void merge(DisjointSetForest forest, ArrayList<Edge> edges, 
                            Pixel[][] pixelGraph, double g){

        for (Edge e: edges){
            Pixel vn = e.getFirstPixel();
            Pixel vm = e.getSecondPixel();

            Pixel sn = forest.find(vn, pixelGraph);
            Pixel sm = forest.find(vm, pixelGraph);

            if (sn != sm) {
                double sizeN = (double)forest.getSize(sn);
                double sizeM = (double)forest.getSize(sm);
                double idN =   (double)forest.getID(sn); 
                double idM =   (double)forest.getID(sm);

                if(e.getWeight() < Math.min((idN + g/sizeN), (idM + g/sizeM))){
                    forest.union(sn, sm, e.getWeight(), pixelGraph); 
                }
            }
        }
        System.out.println("merge");
    }

    /** Assigns the random color to each pixel within similar segments.
     * 
     * This method takes a random color and assigns every pixel within a segment
     * that color. Then takes another random color and assigns every pixel in
     * another segment that color. The method does so until all segments have 
     * their own random color.
     * 
     * @param pixelGraph - a grid with pixels in each element.
     * @param forest - a disjoint set forest.
     * @return colorArray - a grid with colors in each element.
     */
    public static Color[][] assignColor (Pixel[][] pixelGraph, DisjointSetForest forest){

        HashMap<Pixel, HashSet<Pixel>> pixels = new HashMap <Pixel, HashSet<Pixel>>();
        pixels = forest.returnSegments(pixelGraph);

        Color[][] colorArray = new Color[pixelGraph.length][pixelGraph[0].length];
        ColorPicker colorPicker = new ColorPicker();

        for (Pixel current: pixels.keySet()){
            Color color = colorPicker.nextColor();
            HashSet<Pixel> set = pixels.get(current);
            for(Pixel pixel: set){
                colorArray[pixel.getRow()][pixel.getCol()] = color;
            }
        }

        System.out.println("assignColor");

        return colorArray;
    }
    
    public static void main (String[] args){ 

        Color[][]  rgbArray = new Color[3][3];
        rgbArray[0][0] = new Color(255, 255, 255);
        rgbArray[0][1] = new Color(0, 0, 0);
        rgbArray[0][2] = new Color(230, 89, 67);
        rgbArray[1][0] = new Color(100, 89, 67);
        rgbArray[1][1] = new Color(245, 89, 67);
        rgbArray[1][2] = new Color(234, 89, 67);
        rgbArray[2][0] = new Color(120, 89, 67);
        rgbArray[2][1] = new Color(230, 89, 67);
        rgbArray[2][2] = new Color(220, 89, 67);    
        
        double g = 500.0;
    
        segment(rgbArray, g); 
    }
}

