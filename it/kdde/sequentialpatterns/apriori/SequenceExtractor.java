package it.kdde.sequentialpatterns.apriori;

import it.kdde.sequentialpatterns.model.FrequentItemset;
import it.kdde.sequentialpatterns.model.FrequentSequence;
import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.Sequence;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fabiofumarola on 23/11/14.
 */
public abstract class SequenceExtractor {

    protected final TransformedAprioriDataset ds;

    public SequenceExtractor(TransformedAprioriDataset ds) {
        this.ds = ds;
    }

    public abstract List<FrequentSequence> sequencePhase(List<FrequentItemset> litemset);
}


class FrequenceSequenceExtractor extends SequenceExtractor {

    public FrequenceSequenceExtractor(TransformedAprioriDataset ds) {
        super(ds);
    }

    @Override
    public List<FrequentSequence> sequencePhase(List<FrequentItemset> litemset) {

        List<FrequentSequence> oneSequences = litemset.stream().map(li ->
                new FrequentSequence(li.getSupport(),
                        new Itemset(ds.getBiMap().get(li).toString())))
                .collect(Collectors.toList());

        return sequenceExtraction(oneSequences, new LinkedList<FrequentSequence>(oneSequences));
    }

    private List<FrequentSequence> sequenceExtraction(List<FrequentSequence> levelIMinusOne, LinkedList<FrequentSequence> accumulator) {

        //generate candidates
        List<Sequence> candidates = candidateGeneration(levelIMinusOne);

        //filter out the not frequent itemsets
        List<FrequentSequence> frequentSequences = candidates.stream().
                map(c -> new FrequentSequence(countSupport(c), c.getElements())).
                filter(f -> f.getSupport() >= ds.getAbsoluteSupport()).collect(Collectors.toList());
        accumulator.addAll(frequentSequences);

        if (frequentSequences.isEmpty())
            return accumulator;
        else
            return sequenceExtraction(frequentSequences, accumulator);
    }

    /**
     * @param levelIMinusOne
     * @return generate all the candidates of size +1 from the given Frequent Sequences
     */
    private List<Sequence> candidateGeneration(List<FrequentSequence> levelIMinusOne) {

        List<Sequence> candidates = new LinkedList<>();

        for (int i = 0; i < levelIMinusOne.size(); i++) {
            FrequentSequence cand1 = levelIMinusOne.get(i);
            for (int j = 0; j < levelIMinusOne.size(); j++) {
                FrequentSequence cand2 = levelIMinusOne.get(j);
                Sequence gen = cand1.clone();
                gen.add(cand2.getLastItemset());
                candidates.add(gen);
            }
        }
        return candidates.stream().distinct().
                filter(s -> checkSubSequences(s, levelIMinusOne)).
                collect(Collectors.toList());
    }

    /**
     * @param sequence
     * @param levelIMinusOne
     * @return true if all the sub-sequences of the given sequence are frequent
     */
    private boolean checkSubSequences(Sequence sequence, List<FrequentSequence> levelIMinusOne) {
        return true;
    }

    /**
     * @param sequence
     * @return
     */
    private int countSupport(Sequence sequence) {
        return ds.getElements().stream().map(s -> {
            if (s.contains(sequence))
                return 1;
            else return 0;
        }).reduce(0, Integer::sum);
    }
}

