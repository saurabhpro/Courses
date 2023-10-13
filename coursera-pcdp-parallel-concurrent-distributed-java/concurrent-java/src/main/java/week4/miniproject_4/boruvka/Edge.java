package week4.miniproject_4.boruvka;

/**
 * An abstract representation of a weighted, directed edge in a graph connecting
 * two components.
 *
 * @param <C> Component type that this edge connects.
 */
public interface Edge<C extends Component> {
    /**
     * Fetch the weight of this edge.
     *
     * @return Weight of this edge.
     */
    double weight();

    /**
     * Given one member of this edge, swap it out for a different component.
     *
     * @param from The component already in this edge
     * @param to   The component to replace with
     *
     * @return this
     */
    Edge<C> replaceComponent(C from, C to);

    /**
     * Source component for this edge.
     *
     * @return Source component for this edge.
     */
    C fromComponent();

    /**
     * Destination component for this edge.
     *
     * @return Destination component for this edge.
     */
    C toComponent();

    /**
     * Given one member of this edge, return the other.
     *
     * @param component One component of this edge.
     *
     * @return The other component of this edge.
     */
    C getOther(C component);
}
