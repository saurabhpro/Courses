package week1.miniproject_1;

import java.util.Random;

public class RandomSequenceGenerator implements SequenceGenerator {
    private final int seed;
    private final int maxNum;
    private final int sequenceLen;
    private Random rand;

    public RandomSequenceGenerator(final int setSeed,
                                   final int setSequenceLen, final int setMaxNum) {
        this.seed = setSeed;
        this.rand = new Random(this.seed);
        this.maxNum = setMaxNum;
        this.sequenceLen = setSequenceLen;
    }

    @Override
    public int sequenceLength() {
        return sequenceLen;
    }

    @Override
    public int next() {
        return rand.nextInt(maxNum);
    }

    @Override
    public void reset() {
        this.rand = new Random(this.seed);
    }

    @Override
    public String getLabel() {
        return "Random";
    }
}
