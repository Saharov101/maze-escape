import java.util.Arrays;

/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);
        int[] to = s.to();
        int[] from = s.from();

        // Pass 1: Iterative DFS to record finish order
        boolean[] visited = new boolean[n];
        int[] nextEdge = new int[n];
        int[] stack = new int[n];
        int[] order = new int[n];
        int orderIdx = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;

            int top = 0;
            stack[top++] = start;
            visited[start] = true;

            while (top > 0) {
                int u = stack[top - 1];
                if (nextEdge[u] < out[u].length) {
                    int slideIdx = out[u][nextEdge[u]++];
                    int v = to[slideIdx];
                    if (!visited[v]) {
                        visited[v] = true;
                        stack[top++] = v;
                    }
                } else {
                    top--;
                    order[orderIdx++] = u;
                }
            }
        }

        // Pass 2: Iterative DFS on reversed graph in reverse finish order
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int currentZone = 0;

        for (int i = n - 1; i >= 0; i--) {
            int start = order[i];
            if (zoneOf[start] != -1) continue;

            int top = 0;
            stack[top++] = start;
            zoneOf[start] = currentZone;

            while (top > 0) {
                int u = stack[--top];
                for (int slideIdx : in[u]) {
                    int prev = from[slideIdx];
                    if (zoneOf[prev] == -1) {
                        zoneOf[prev] = currentZone;
                        stack[top++] = prev;
                    }
                }
            }
            currentZone++;
        }

        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[] to = s.to();
        int[] time = s.time();

        // Group cells by zone so we can iterate zones in order 0 .. zoneCount - 1
        int[] count = new int[zoneCount];
        for (int u = 0; u < n; u++) {
            count[zoneOf[u]]++;
        }
        int[][] cellsInZone = new int[zoneCount][];
        for (int z = 0; z < zoneCount; z++) {
            cellsInZone[z] = new int[count[z]];
        }
        int[] filled = new int[zoneCount];
        for (int u = 0; u < n; u++) {
            int z = zoneOf[u];
            cellsInZone[z][filled[z]++] = u;
        }

        long[] stages = new long[zoneCount];
        Arrays.fill(stages, 1L);

        long INF = Long.MAX_VALUE / 4;
        long[] minCost = new long[zoneCount];
        Arrays.fill(minCost, INF);

        int startZone = zoneOf[0];
        int exitZone = zoneOf[n - 1];
        minCost[startZone] = 0;

        long maxStages = 0;
        for (int z = 0; z < zoneCount; z++) {
            if (stages[z] > maxStages) {
                maxStages = stages[z];
            }
            for (int u : cellsInZone[z]) {
                for (int slideIdx : out[u]) {
                    int v = to[slideIdx];
                    int zv = zoneOf[v];
                    if (zv > z) {
                        if (stages[z] + 1 > stages[zv]) {
                            stages[zv] = stages[z] + 1;
                        }
                        if (minCost[z] != INF && minCost[z] + time[slideIdx] < minCost[zv]) {
                            minCost[zv] = minCost[z] + time[slideIdx];
                        }
                    }
                }
            }
        }

        return new long[]{maxStages, minCost[exitZone]};
    }
}