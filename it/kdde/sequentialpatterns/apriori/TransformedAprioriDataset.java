package it.kdde.sequentialpatterns.apriori;

import com.google.common.collect.BiMap;
import it.kdde.sequentialpatterns.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fabiofumarola on 23/11/14.
 * <p/>
 * this class represents a transformed dataset
 */
public class TransformedAprioriDataset extends AprioriDataset {

    private final BiMap<FrequentItemset, Integer> biMap;

    protected TransformedAprioriDataset(List<Sequence> elements, float relativeSupport, int absoluteSupport,
                                        BiMap<FrequentItemset, Integer> biMap) {
        super(elements, relativeSupport, absoluteSupport);
        this.biMap = biMap;
    }

    public BiMap<FrequentItemset, Integer> getBiMap() {
        return biMap;
    }

    public List<FrequentSequence> decodeSequences(List<FrequentSequence> frequentSequences) {
        return frequentSequences.stream().
                map(s -> decodeSequence(s)).collect(Collectors.toList());
    }

    private FrequentSequence decodeSequence(FrequentSequence sequence) {

        List<Itemset> list = sequence.getElements().stream().
                map(itemset -> biMap.inverse().get(Integer.parseInt(itemset.getLast()))).
                collect(Collectors.toList());

        return new FrequentSequence(sequence.getSupport(),list);
    }
}
