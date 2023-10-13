package week4.miniproject_4.boruvka;

/**
 * An abstract definition of a component in a graph, which may be a singleton
 * node or may be the result of merging multiple nodes.
 *
 * @param <C> Type of this component.
 */
public interface Component<C extends Component> {
    /**
     * Fetch the unique node ID for this node in the graph.
     *
     * @return Node ID
     */
    int nodeId();

    /**
     * Add an edge to this component.
     *
     * @param e Edge to add, which is anchored on this component.
     */
    void addEdge(Edge<C> e);

    /**
     * Compute the total weight of this component.
     *
     * @return The weight of this component, based on the number of edges that
     * have been collapsed to form it.
     */
    double totalWeight();

    /**
     * Compute the number of edges that have been collapsed to form this
     * component.
     *
     * @return # edges collapsed into this component.
     */
    long totalEdges();
}
