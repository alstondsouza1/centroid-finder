import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) {
        List<Group> groupList = new ArrayList<>();

        // check if the image is null
        if (image == null) {
            throw new NullPointerException("Image array cannot be null");
        }

        // check if the image is empty
        if (image.length == 0 || image[0].length == 0) {
            throw new IllegalArgumentException("Image array cannot be empty");
        }

        // check if the image is rectangular
        for (int[] row : image) {
            if (row == null) {
                throw new IllegalArgumentException("Image array cannot contain null subarrays");
            }
        }
        boolean[][] visited = new boolean[image.length][image[0].length];

        // iterate through the image to find connected groups
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (image[i][j] == 1 && !visited[i][j]) {
                    List<int[]> pixels = findConnectedGroupsHelper(image, new int[]{i, j}, visited);
                    groupList.add(newGroup(pixels));
                }
            }
        }

        groupList.sort(Collections.reverseOrder());
        return groupList;
    }

    // findConnectedGroupsHelper to find all connected 1s staring from current 
    public List<int[]> findConnectedGroupsHelper(int[][] image, int[] current, boolean[][] visited) {
        List<int[]> pixels = new ArrayList<>();
        Stack<int[]> stack = new Stack<>();
        stack.push(current);

        // DFS to find all connected pixels
        while (!stack.isEmpty()) {
            int[] pixel = stack.pop();
            int row = pixel[0];
            int col = pixel[1];

            // Check if the pixel is out of bounds
            if (row < 0 || row >= image.length || col < 0 || col >= image[0].length) {
                continue; 
            }

            // Check if the pixel is already visited or not a 1
            if (visited[row][col] || image[row][col] == 0) {
                continue; 
            }

            // Mark the pixel as visited
            visited[row][col] = true;
            pixels.add(new int[]{row, col});

            // Add adjacent pixels to the stack
            stack.push(new int[]{row - 1, col}); // up
            stack.push(new int[]{row + 1, col}); // down
            stack.push(new int[]{row, col - 1}); // left
            stack.push(new int[]{row, col + 1}); // right
        }

        return pixels; 
    }

    // possibleDirections 
    public static List<int[]> possibleDirections(int[][] image, int[] current) {
        List<int[]> validMoves = new ArrayList<>();

        int row = current[0];
        int col = current[1];
        
        int[][] directions = new int[][] {
            {-1, 0}, // up
            {1, 0},  // down
            {0, -1}, // left
            {0, 1}   // right
        };

        // Check all four possible directions
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

             // Check if the new position is within bounds and is a 1
            if (newRow >= 0 && newRow < image.length && newCol >= 0 && newCol < image[0].length && image[newRow][newCol] == 1) {
                validMoves.add(new int[]{newRow, newCol});
            }
        }

        return validMoves;
    }

    
    private Group newGroup(List<int[]> pixels) {
        int size = pixels.size();
        int sumX = 0;
        int sumY = 0;

        for (int[] pixel : pixels) {
            sumX += pixel[1];
            sumY += pixel[0];
        }

        int centroidX = sumX / size;
        int centroidY = sumY / size;

        return new Group(size, new Coordinate(centroidX, centroidY));
    }

}
