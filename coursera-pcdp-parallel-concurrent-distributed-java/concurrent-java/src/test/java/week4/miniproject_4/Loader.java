package week4.miniproject_4;

import week4.miniproject_4.boruvka.BoruvkaFactory;
import week4.miniproject_4.boruvka.Component;
import week4.miniproject_4.boruvka.Edge;
import week4.miniproject_4.util.IntPair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.zip.GZIPInputStream;

import static java.util.Collections.shuffle;

/**
 * This class should not be modified.
 *
 * @author <a href="http://shams.web.rice.edu/">Shams Imam</a> (shams@rice.edu)
 */
public final class Loader {

    /**
     * Read edges from the provided input file.
     */
    public static <C extends Component, E extends Edge> void read(String fileName,
                                                                  BoruvkaFactory<C, E> boruvkaFactory,
                                                                  Queue<C> nodesLoaded) {

        Map<Integer, C> nodesMap = new HashMap<>();
        Map<IntPair, E> edgesMap = new HashMap<>();

        double totalWeight = 0;
        var edges = 0;
        try {
            // Open the compressed file
            var in = new GZIPInputStream(new FileInputStream(fileName));
            Reader r = new BufferedReader(new InputStreamReader(in));
            var st = new StreamTokenizer(r);
            final var cstring = "c";
            final var pstring = "p";
            st.commentChar(cstring.charAt(0));
            st.commentChar(pstring.charAt(0));
            // read graph
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                assert (st.sval.equals("a"));
                st.nextToken();
                var from = (int) st.nval;
                st.nextToken();
                var to = (int) st.nval;
                var nodeFrom = getComponent(boruvkaFactory, nodesMap, from);
                var nodeTo = getComponent(boruvkaFactory, nodesMap, to);
                assert (nodeTo != nodeFrom); // Assume no self-loops in the input graph
                st.nextToken();
                var weight = (int) st.nval;
                addEdge(boruvkaFactory, edgesMap, from, to, nodeFrom, nodeTo, weight);
                totalWeight += weight;
                edges++;
            }
            // Close the file and stream
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<C> nodesList = new ArrayList<>(nodesMap.values());
        shuffle(nodesList);
        nodesLoaded.addAll(nodesList);
    }

    private static <C extends Component, E extends Edge> C getComponent(BoruvkaFactory<C, E> factory,
                                                                        Map<Integer, C> nodesMap,
                                                                        int node) {
        if (!nodesMap.containsKey(node)) {
            nodesMap.put(node, factory.newComponent(node));
        }
        return nodesMap.get(node);
    }

    private static <C extends Component, E extends Edge> void addEdge(
        BoruvkaFactory<C, E> factory, Map<IntPair, E> edgesMap,
        int from, int to, C fromC, C toC, double w) {

        IntPair p;
        if (from < to) {
            p = new IntPair(from, to);
        } else {
            p = new IntPair(to, from);
        }
        if (!edgesMap.containsKey(p)) {
            var e = factory.newEdge(fromC, toC, w);
            edgesMap.put(p, e);
            fromC.addEdge(e);
            toC.addEdge(e);
        } else {
            assert (edgesMap.get(p).weight() == w);
        }
    }
}
