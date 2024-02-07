// Sources used:
// https://algs4.cs.princeton.edu/42digraph/DigraphGenerator.java.html
// https://www.geeksforgeeks.org/strongly-connected-components/
// https://algs4.cs.princeton.edu/42digraph/Digraph.java.html
// http://underpop.online.fr/j/java/help/graph-generators.html.gz
// https://en.wikipedia.org/wiki/Connectivity_(graph_theory)
// https://www.youtube.com/watch?v=7AhHGp7EzZ8
// https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-fall-2011/resources/lecture-14-depth-first-search-dfs-topological-sort/
// https://www.sanfoundry.com/java-program-represent-graph-adjacency-list/
// Eestikeelne ülesandepüstitus Moodle's:
// Moodustada meetod, mis leiab etteantud orienteeritud graafi korral selle tugevalt sidusate komponentide arvu.

import java.util.*;

/** Container class to different classes, that makes the whole
 * set of classes one class formally.
 */
public class GraphTask {

   /** Main method. */
   public static void main (String[] args) {
      GraphTask a = new GraphTask();
      a.run();
   }

   /** Actual main method to run examples and everything. */
   public void run() {
      String nl = System.lineSeparator();
      Graph dg = new Graph("Digraph G1");
      dg.createRandomSimpleDigraph(6, 8);
      System.out.println(dg);
      dg.getSCCs();
      Graph dg2 = new Graph("Digraph G2");
      dg2.createRandomSimpleDigraph(6, 11);
      System.out.println(dg2);
      dg2.createAdjList();
      dg2.printAdjList();
      System.out.println(nl);
      dg2.printReverseAdjList();
      System.out.println(nl);
      dg2.getSCCs();
      Graph dg3 = new Graph("Digraph G3");
      dg3.createRandomSimpleDigraph(2222, 6321);
      dg3.getSCCs();
      Graph dg4 = new Graph("Digraph G4");
      Graph dg5 = new Graph("Digraph G5");
      dg4.createRandomSimpleDigraph(21, 55);
      dg4.getSCCs();
      dg5.createRandomSimpleDigraph(2, 1);
      dg5.getSCCs();
   }

   /** Finding Strongly Connected Components (SCCs) in
    * a random directed graph, using Kosaraju's algorithm.
    */
   class Vertex {

      private String id;
      private Vertex next;
      private Arc first;
      private int info = 0;
      // You can add more fields, if needed

      Vertex (String s, Vertex v, Arc e) {
         id = s;
         next = v;
         first = e;
      }

      Vertex (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }
   }


   /** Arc represents one arrow in the graph. Two-directional edges are
    * represented by two Arc objects (for both directions).
    */
   class Arc {

      private final String id;
      private Vertex startVertex;
      private Vertex target;
      private Arc next;

      Arc (String s, Vertex v, Arc a) {
         id = s;
         target = v;
         next = a;
      }

      Arc (String s) {
         this (s, null, null);
      }

      @Override
      public String toString() {
         return id;
      }
   } 


   class Graph {

      private final String id;
      private Vertex first;
      private int edges;
      private int vertices;
      private final Stack<Arc> set = new Stack<>();
      private final Stack<Vertex> vertSet = new Stack<>();
      private Map<Integer, List<Vertex>> adj;
      private Map<Integer, List<Vertex>> reverseAdj;
      private final Map<Integer, List<Vertex>> listOfSCCs = new HashMap<Integer, List<Vertex>>();
      private final Stack<Vertex> helperStack = new Stack<>();
      boolean[] visited;

      Graph(String s, Vertex v) {
         id = s;
         first = v;
      }

      Graph(String s) {
         this(s, null);
      }

      @Override
      public String toString() {
         Stack<Vertex> copy = new Stack<>();
         copy.addAll(vertSet);
         String nl = System.getProperty("line.separator");
         StringBuilder sb = new StringBuilder(nl);
         sb.append(id).append(":");
         sb.append(nl);
         first = copy.get(0);
         Vertex v = first;
         while (v != null) {
            copy.remove(v);
            sb.append(v);
            sb.append(" -->");
            Arc a = v.first;
            while (a != null) {
               sb.append(" ");
               sb.append(a);
               sb.append(" (");
               sb.append(a.startVertex);
               sb.append("->");
               sb.append(a.target);
               sb.append(");");
               a = a.next;
            }
            sb.append(nl);
            if (copy.size() != 0) {
               v = copy.get(0);
            } else {
               return sb.toString();
            }
         }
         return sb.toString();
      }

      public Vertex createVertex(String vid) {
         Vertex res = new Vertex(vid);
         res.info = Integer.parseInt(vid.replace("v_", ""));
         vertices++;
         for (Vertex vertex : vertSet) {
            if (vertex.info == res.info) {
               vertices--;
               return vertex;
            }
         }
         res.next = first;
         first = res;
         vertSet.add(res);
         return res;
      }

      public Arc createArc(String aid, Vertex from, Vertex to) {
         Arc res = new Arc(aid);
         res.target = to;
         res.startVertex = from;
         for (Arc arc : set) {
            if (arc.startVertex == res.startVertex && arc.target == res.target) {
               return null;
            }
         }
         res.next = from.first;
         from.first = res;
         return res;
      }

      /**
       * Create an adjacency list and a reverse adjacency list of this graph.
       * Changes the adj and reverseAdj fields for a graph.
       */
      public void createAdjList() {
         Stack<Vertex> copy = new Stack<>();
         copy.addAll(vertSet);
         Vertex v = copy.get(0);
         adj = new HashMap<Integer, List<Vertex>>();
         reverseAdj = new HashMap<Integer, List<Vertex>>();
         for (int i = 0; i < vertSet.size(); i++) {
            adj.put(i, new LinkedList<>());
            reverseAdj.put(i, new LinkedList<>());
         }
         while (v != null) {
            copy.remove(v);
            Arc a = v.first;
            while (a != null) {
               adj.get(v.info).add(a.target);
               reverseAdj.get(a.target.info).add(v);
               a = a.next;
            }
            if (copy.size() != 0) {
               v = copy.get(0);
            } else {
               break;
            }
         }
      }

      /**
       * Prints out an adjacency list in a readable form.
       */
      private void printAdjList() {
         System.out.println("Adjacency list:");
         for (int i = 0; i < adj.size(); i++) {
            System.out.println("\nVertex " + i + ":");
            for (int j = 0; j < adj.get(i).size(); j++) {
               System.out.print(vertSet.get(i) + " -> " + adj.get(i).get(j) + "; ");
            }
         }
      }

      /**
       * Prints out a reverse adjacency list in a readable form.
       */
      private void printReverseAdjList() {
         System.out.println("Reverse adjacency list:");
         for (int i = 0; i < reverseAdj.size(); i++) {
            System.out.println("\nVertex " + i + ":");
            for (int j = 0; j < reverseAdj.get(i).size(); j++) {
               System.out.print(vertSet.get(i) + " -> " + reverseAdj.get(i).get(j) + "; ");
            }
         }
      }

      /**
       * Creates a random simple directed (no loops, no multiple
       * arcs) graph with n vertices and m (Max: n(n-1)) edges.
       * Graph may be disconnected.
       *
       * @param n number of vertices
       * @param m number of arcs
       */
      private void createRandomSimpleDigraph(int n, int m) {
         if (m > (long) n * (n - 1)) throw new IllegalArgumentException("Too many edges");
         if (m < 0) throw new IllegalArgumentException("Too few edges");
         Graph G = this;
         Random random = new Random();
         int counter = 0;
         while (G.edges < m) {
            Arc e = null;
            int v = random.nextInt(n);
            Vertex vertex1 = createVertex("v_" + v);
            int w = random.nextInt(n);
            Vertex vertex2 = createVertex("v_" + w);
            if (v != w) {
               e = createArc("arc", vertex1, vertex2);
            }
            if ((v != w) && e != null) {
               set.add(e);
               G.edges++;
            }
         }
         for (Vertex vertex : vertSet) {
            vertex.info = counter;
            vertex.id = "v_" + counter;
            counter++;
         }
         if (vertSet.size() != n) {
            for (int i = vertSet.size(); i < n + 1; i++) {
               createVertex("v_" + i);
            }
         }
      }

      /**
       * Implementation of the Kosaraju's algorithm to find all SCCs in a directed graph.
       * This implements Depth-First Searches.
       * This empties the helperStack field for a graph.
       */
      private void getSCCs() {
         createAdjList();
         visited = new boolean[vertSet.size()];

         for (int i = 0; i < vertSet.size(); i++) {
            visited[i] = false;
         }

         for (int i = 0; i < vertSet.size(); i++) {
            if (!visited[i]) {
               adjDFSUtil(i);
            }
         }

         for (int i = 0; i < vertSet.size(); i++) {
            visited[i] = false;
         }

         int counter = 1;
         while (!helperStack.isEmpty()) {
            int x = helperStack.pop().info;
            if (!visited[x]) {
               listOfSCCs.put(counter, new LinkedList<>());
               reverseAdjDFSUtil(x, counter);
               counter++;
            }
         }
         System.out.println("Randomly generated digraph " + id +
                 "(V: " + vertSet.size() + "; E: " + edges + ")" + " contains " +
                 listOfSCCs.size() + " SCC(s).");
         System.out.println(listOfSCCs);
      }

      /**
       * Implementation of the Depth-First Search for the graph's adjacency list.
       * This fills the helperStack field for a graph.
       */
      private void adjDFSUtil(int n) {
         visited[n] = true;
         Vertex x = vertSet.get(n);
         for (int i = 0; i < adj.get(n).size(); i++) {
            int next = adj.get(n).get(i).info;
            if (!visited[next]) {
               adjDFSUtil(next);
            }
         }
         helperStack.push(x);
      }

      /**
       * Implementation of the Depth-First Search for the graph's reverse adjacency list.
       * This populates the listOfSCCs for a graph.
       */
      private void reverseAdjDFSUtil(int n, int count) {
         visited[n] = true;
         Vertex x = vertSet.get(n);
         listOfSCCs.get(count).add(x);
         for (int i = 0; i < reverseAdj.get(n).size(); i++) {
            int next = reverseAdj.get(n).get(i).info;
            if (!visited[next]) {
               reverseAdjDFSUtil(next, count);
            }
         }
      }
   }
}