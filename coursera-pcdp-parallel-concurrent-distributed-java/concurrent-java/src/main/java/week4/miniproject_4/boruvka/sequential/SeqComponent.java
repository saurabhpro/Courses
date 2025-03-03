package week4.miniproject_4.boruvka.sequential;

import week4.miniproject_4.boruvka.Component;
import week4.miniproject_4.boruvka.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * Component implementation used for the sequential Boruvka implementation.
 */
public final class SeqComponent implements Component<SeqComponent> {

    /**
     * A unique identifier for this component in the graph that contains
     * it.
     */
    public final int nodeId;
    /**
     * List of edges attached to this component, sorted by weight from least
     * to greatest.
     */
    public List<Edge<SeqComponent>> edges = new ArrayList<>();
    /**
     * The weight this component accounts for. A component gains weight when
     * it is merged with another component across an edge with a certain
     * weight.
     */
    public double totalWeight = 0;
    /**
     * Number of edges that have been collapsed to create this component.
     */
    public long totalEdges = 0;
    /**
     * Whether this component has already been collapsed into another
     * component.
     */
    public boolean isDead = false;

    /**
     * Constructor.
     *
     * @param setNodeId ID for this node.
     */
    SeqComponent(final int setNodeId) {
        super();
        this.nodeId = setNodeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeId() {
        return nodeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double totalWeight() {
        return totalWeight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long totalEdges() {
        return totalEdges;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Edge is inserted in weight order, from least to greatest.
     */
    public void addEdge(final Edge<SeqComponent> e) {
        var i = 0;
        while (i < edges.size()) {
            if (e.weight() < edges.get(i).weight()) {
                break;
            }
            i++;
        }
        edges.add(i, e);
    }

    /**
     * Get the edge with minimum weight from the sorted edge list.
     *
     * @return Edge with the smallest weight attached to this component.
     */
    public Edge<SeqComponent> getMinEdge() {
        if (edges.isEmpty()) {
            return null;
        }
        return edges.get(0);
    }

    /**
     * Merge two components together, connected by an edge with weight
     * edgeWeight.
     *
     * @param other      The other component to merge into this component.
     * @param edgeWeight Weight of the edge connecting these components.
     */
    public void merge(final SeqComponent other, final double edgeWeight) {
        totalWeight += other.totalWeight + edgeWeight;
        totalEdges += other.totalEdges + 1;
        final List<Edge<SeqComponent>> newEdges = new ArrayList<>();
        var i = 0;
        var j = 0;
        while (i + j < edges.size() + other.edges.size()) {
            // get rid of inter-component edges
            while (i < edges.size()) {
                final var e = edges.get(i);
                if ((e.fromComponent() != this && e.fromComponent() != other)
                        || (e.toComponent() != this && e.toComponent() != other)
                ) {
                    break;
                }
                i++;
            }
            while (j < other.edges.size()) {
                final var e = other.edges.get(j);
                if ((e.fromComponent() != this && e.fromComponent() != other)
                        || (e.toComponent() != this && e.toComponent() != other)
                ) {
                    break;
                }
                j++;
            }
            if (j < other.edges.size() && (i >= edges.size()
                    || edges.get(i).weight() > other.edges.get(j).weight())
            ) {
                newEdges.add(other.edges.get(j++).replaceComponent(other,
                        this));
            } else if (i < edges.size()) {
                newEdges.add(edges.get(i++).replaceComponent(other, this));
            }
        }
        other.edges.clear();
        edges.clear();
        edges = newEdges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Component component)) {
            return false;
        }

        return nodeId == component.nodeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return nodeId;
    }
}
