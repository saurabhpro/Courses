package week4.miniproject_4.boruvka.parallel;


import week4.miniproject_4.ParBoruvka.ParComponent;
import week4.miniproject_4.ParBoruvka.ParEdge;
import week4.miniproject_4.boruvka.BoruvkaFactory;

/**
 * A factory for generating components and edges when performing a parallel
 * traversal.
 */
public final class ParBoruvkaFactory
        implements BoruvkaFactory<ParComponent, ParEdge> {
    /**
     * {@inheritDoc}
     */
    @Override
    public ParComponent newComponent(final int nodeId) {
        return new ParComponent(nodeId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParEdge newEdge(final ParComponent from, final ParComponent to,
                           final double weight) {
        return new ParEdge(from, to, weight);
    }
}
