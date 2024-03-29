package week4.miniproject_4.boruvka.sequential;


import week4.miniproject_4.AbstractBoruvka;
import week4.miniproject_4.SolutionToBoruvka;

import java.util.Queue;

/**
 * Sequential implementatin of Boruvka's minimum spanning tree algorithm.
 */
public final class SeqBoruvka implements AbstractBoruvka<SeqComponent> {

    /**
     * Constructor.
     */
    public SeqBoruvka() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeBoruvka(final Queue<SeqComponent> nodesLoaded,
                               final SolutionToBoruvka<SeqComponent> solution) {
        SeqComponent loopNode = null;

        // START OF EDGE CONTRACTION ALGORITHM
        while (!nodesLoaded.isEmpty()) {

            /*
             * poll() removes first element (node loopNode) from the nodesLoaded
             * work-list.
             */
            loopNode = nodesLoaded.poll();

            if (loopNode.isDead) {
                continue; // node loopNode has already been merged
            }

            // retrieve loopNode's edge with minimum cost
            final var e = loopNode.getMinEdge();
            if (e == null) {
                break; // done - we've contracted the graph to a single node
            }

            final var other = e.getOther(loopNode);
            other.isDead = true;
            // merge node other into node loopNode
            loopNode.merge(other, e.weight());
            // add newly merged loopNode back in the work-list
            nodesLoaded.add(loopNode);

        }
        // END OF EDGE CONTRACTION ALGORITHM

        solution.setSolution(loopNode);
    }
}
