import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Class that keeps track of, organizes, and arranges Nodes in a disjoint set forest
 * in order to set up segments for pixels that should be in the same segment.
 * 
 * @author William Clark
 * @author Axel Fries
 * 
 * Time Spent: 6 hours.
 */
public class DisjointSetForest{
    
    public HashMap<Pixel , Node> nodes;     

    /**
     * Constructor for a disjoint set forest.
     * 
     * @param pixelArray - Matrix of pixels. All rows must be of uniform and all cols
     * must be of uniform length.
     */
    public DisjointSetForest(Pixel[][] pixelArray) {
        
        nodes = new HashMap<Pixel, Node>();

        for (int i = 0; i < pixelArray.length; i++){
            for (int j = 0; j < pixelArray[0].length; j++){
                Node node = new Node(i, j);

                nodes.put(pixelArray[i][j], node);
            }
        }
    }
    
    /**
     * Returns a map of a representative pixel for each segment as the key and the 
     * pixels in that segment, including the representative pixel as the value. 
     * 
     * @param pixelArray - Pixel array 
     * @return segments - HashMap<Pixel, HashSet<Pixel>> keys are the root pixel. values are the 
     * pixels contained in the segments with the root pixel 
     */
    public HashMap<Pixel, HashSet<Pixel>> returnSegments(Pixel[][] pixelArray){
        
        HashMap<Pixel, HashSet<Pixel>> segments = new HashMap<Pixel, HashSet<Pixel>>();
       
        System.out.println(nodes.size());

        for (int i = 0; i < pixelArray.length; i++){
            for (int j = 0; j < pixelArray[0].length; j++){
                
                //find the root of the current pixel you are on
                //if segments.containsKey(root) 
                //then segments.get(root).add(pixel[i][j])
                //else:
                //create a new HashSet and add the root and the current pixel to it
                //do segments.put(root, tempSet);

                Pixel root = find(pixelArray[i][j], pixelArray);
                if (segments.containsKey(root)){
                    
                    segments.get(root).add(pixelArray[i][j]);
                    
                } else {
                    HashSet<Pixel> tempSet = new HashSet<Pixel>();
                    tempSet.add(root);
                    tempSet.add(pixelArray[i][j]);
                    segments.put(root, tempSet);
                }
            }
        }     
        System.out.println(segments.size());
        System.out.println(segments.keySet().size());
        return segments;
    }

    /**
     * Gets the size of the segment. 
     * @param pixel - input pixel 
     * @return int representing the size of the segment
     */
    public int getSize(Pixel pixel){
        return nodes.get(pixel).size;
    }

    /**
     * Gets the internal distance of the input pixel from the stored nodes map.
     * @param pixel - input pixel
     * @return int - internal distance of the pixel 
     */
    public double getID(Pixel pixel){
        return nodes.get(pixel).id;
    }

    /**
     * Gets the corresponding the Node for the input pixel.
     * @param pixel - the input Pixel.
     * @return Node for the pixel
     */
    public Node getNode(Pixel pixel){
        return nodes.get(pixel);
    }
    /**
     * Node class to keep track of certain information about each pixel
     * in the disjoint set forest.
     */
    public class Node{
        public Node parent;
        public int size;
        public int row;
        public int col;
        public double id;
        public int rank;

        /**
         * Constructs a Node
         * @param row - the row of the Pixel
         * @param col - the col of the Pixel
         */
        public Node(int row, int col){
            parent = null;
            size = 1;
            this.col = col;
            this.row = row;
            this.id = 0.0;
            this.rank = 0;
        }

        
    }

    /**
     * Finds the root Pixel of a given pixel. Also compresses the pathway taken to find the root
     * node from the input pixel. 
     * @param pixel - input pixel
     * @param pixelArray - pixelArray for the image 
     * @return the root pixel - type Pixel
     */
    public Pixel find(Pixel pixel, Pixel[][] pixelArray){
        Node node = nodes.get(pixel);

        ArrayList<Node> visited = new ArrayList<Node>();
        Node root = findRep(node, visited);

        
        for (Node current: visited){
            current.parent = root;
        }
        return pixelArray[root.row][root.col];
    }

    /**
     * REcursive helper method for the find method. Add to a list of visited nodes and 
     * return the root node.
     * 
     * @param node - the node in the segment that you begin at and intend to traverse up
     * the sugment to find its root.
     * @param visited - ArrayList<Node> that keeps track of visted nodes including the first node, and
     * doesn't include the root node.
     * @return 
     */
    public Node findRep(Node node, ArrayList<Node> visited){
        if(node.parent == null){
            return node;
        }
        visited.add(node);
        return findRep(node.parent, visited);
    }
    
    /**
     * Forms a union between two segments. Input is two pixels. The union is formed between
     * the root of each of the input pixels. The root of the first pixel becomes the parent of 
     * the second pixel.
     * 
     * @param one - Pixel who's segment will be merged with the second pixel's segment
     * @param two - Pixel who's segment will be merged with the first pixel's segment
     */
    public void union (Pixel one, Pixel two, double weight, Pixel[][] pixelArray){

        Pixel t = find(one, pixelArray);  //root of one
        Pixel t2 = find(two, pixelArray); //root of two

        Node temp = getNode(t);
        Node temp2 = getNode(t2);
        
        if (temp.rank == temp2.rank){
            temp2.parent = temp;
            temp.size += temp2.size;
            temp.id = weight;
            temp.rank++;
        } else if (temp.rank > temp2.rank){
            temp2.parent = temp;
            temp.size += temp2.size;
            temp.id = weight;
        } else {
            temp.parent = temp2;
            temp2.size += temp.size;
            temp2.id = weight;
        }
    }
}