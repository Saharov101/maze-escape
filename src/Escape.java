import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells();
        int cols = w.cols();
        int[][] g = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int i = 0; i < n; i++) {
            if (g[i].length == 1) {
                deadEnds++;
            }
        }

        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        // Max 4 pushes per cell
        int[] stack = new int[4 * n + 1];
        int[] fromStack = new int[4 * n + 1];
        int top = 0;

        stack[top] = 0;
        fromStack[top] = -1;
        top++;

        int visitedCount = 0;
        int exit = n - 1;

        while (top > 0) {
            top--;
            int u = stack[top];
            int p = fromStack[top];

            if (visited[u]) continue;
            visited[u] = true;
            parent[u] = p;
            visitedCount++;

            if (u == exit) {
                break;
            }

            // Desired exploration order: up, right, down, left.
            // Push onto LIFO stack in reverse order: left, down, right, up.
            int[] dirs = {u - 1, u + cols, u + 1, u - cols};
            for (int target : dirs) {
                if (isOpenNeighbour(g[u], target) && !visited[target]) {
                    stack[top] = target;
                    fromStack[top] = u;
                    top++;
                }
            }
        }

        if (!visited[exit]) {
            return new Result(new int[0], visitedCount, deadEnds);
        }

        int len = 0;
        for (int curr = exit; curr != -1; curr = parent[curr]) {
            len++;
        }
        int[] path = new int[len];
        int idx = len - 1;
        for (int curr = exit; curr != -1; curr = parent[curr]) {
            path[idx--] = curr;
        }

        return new Result(path, visitedCount, deadEnds);
    }

    private static boolean isOpenNeighbour(int[] openList, int target) {
        for (int x : openList) {
            if (x == target) return true;
        }
        return false;
    }
}