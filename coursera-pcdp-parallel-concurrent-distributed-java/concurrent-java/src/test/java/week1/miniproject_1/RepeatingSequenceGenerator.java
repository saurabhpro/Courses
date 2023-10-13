package week1.miniproject_1;

public class RepeatingSequenceGenerator implements SequenceGenerator {

    private final int[] subsequence;
    private final int sequenceLen;
    private int iter;

    public RepeatingSequenceGenerator(int setSequenceLen,
                                      int periodicity) {
        this.subsequence = new int[periodicity];
        this.iter = 0;
        this.sequenceLen = setSequenceLen;

        for (var i = 0; i < this.subsequence.length; i++) {
            this.subsequence[i] = i;
        }
    }

    @Override
    public int sequenceLength() {
        return sequenceLen;
    }

    @Override
    public int next() {
        return subsequence[(iter++) % subsequence.length];
    }

    @Override
    public void reset() {
        this.iter = 0;
    }

    @Override
    public String getLabel() {
        return "Repeating";
    }
}
