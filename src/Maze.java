import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        int n = w.cells();
        DisjointSet dsu = new DisjointSet(n);
        int[] temp = new int[n - 1];
        int count = 0;

        for (int k = 0; k < w.count(); k++) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                temp[count++] = k;
                if (count == n - 1) break;
            }
        }
        return count == temp.length ? temp : Arrays.copyOf(temp, count);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        int m = w.count();
        int n = w.cells();
        Integer[] order = new Integer[m];
        for (int i = 0; i < m; i++) {
            order[i] = i;
        }
        int[] cost = w.cost();
        Arrays.sort(order, Comparator.comparingInt(i -> cost[i]));

        DisjointSet dsu = new DisjointSet(n);
        int[] temp = new int[n - 1];
        int count = 0;

        for (int k : order) {
            if (dsu.union(w.a()[k], w.b()[k])) {
                temp[count++] = k;
                if (count == n - 1) break;
            }
        }
        return count == temp.length ? temp : Arrays.copyOf(temp, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int m = w.count();
        int[] a = w.a(), b = w.b(), cost = w.cost();

        // Build adjacency list of walls for each cell
        int[] deg = new int[n];
        for (int k = 0; k < m; k++) {
            deg[a[k]]++;
            deg[b[k]]++;
        }
        int[][] adjWall = new int[n][];
        for (int i = 0; i < n; i++) {
            adjWall[i] = new int[deg[i]];
        }
        int[] filled = new int[n];
        for (int k = 0; k < m; k++) {
            adjWall[a[k]][filled[a[k]]++] = k;
            adjWall[b[k]][filled[b[k]]++] = k;
        }

        boolean[] visited = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e[0]));

        visited[0] = true;
        int visitedCount = 1;
        for (int k : adjWall[0]) {
            int next = a[k] == 0 ? b[k] : a[k];
            pq.add(new int[]{cost[k], next});
        }

        long totalCost = 0;
        while (!pq.isEmpty() && visitedCount < n) {
            int[] curr = pq.poll();
            int c = curr[0];
            int u = curr[1];
            if (visited[u]) continue;

            visited[u] = true;
            visitedCount++;
            totalCost += c;

            for (int k : adjWall[u]) {
                int next = a[k] == u ? b[k] : a[k];
                if (!visited[next]) {
                    pq.add(new int[]{cost[k], next});
                }
            }
        }
        return totalCost;
    }
}