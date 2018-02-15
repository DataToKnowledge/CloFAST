package it.kdde.sequentialpatterns.apriori;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.kdde.sequentialpatterns.model.FrequentItemset;
import it.kdde.sequentialpatterns.model.FrequentSequence;
import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.Sequence;
import util.Statistics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiofumarola on 13/11/14.
 */
public class AprioriAll {

    /**
     * enum to support the discovery of closed patterns
     */
    public enum PatternsType { FREQUENT, CLOSED}

    /**
     *
     */
    private final AprioriDataset aprioriDataset;

    private LitemsetExtractor litemsetExtractor;

    private Statistics statistics = new Statistics();

    /**
     *
     * @param aprioriDataset
     * @param type
     */
    public AprioriAll(AprioriDataset aprioriDataset, PatternsType type) {
        this.aprioriDataset = aprioriDataset;
        switch (type) {
            case FREQUENT:
                litemsetExtractor = new FrequentLitemsetExtractor(aprioriDataset);
                break;
            case CLOSED:
                litemsetExtractor = new ClosedFrequentLitemset(aprioriDataset);
                break;
        }
    }


    public List<FrequentSequence> run() {

        statistics.startMemory();
        statistics.startTimeItemset();
        List<FrequentItemset> litemsets = litemsetExtractor.litemsetPhase();
        //transformation step
        TransformedAprioriDataset transformedDs = tranformationPhase(litemsets);
        statistics.endTimeItemset();

        statistics.startTimeSequence();
        SequenceExtractor sequenceExtractor = new FrequenceSequenceExtractor(transformedDs);
        List<FrequentSequence> frequentSequences = sequenceExtractor.sequencePhase(litemsets);
        statistics.endTimeSequence();
        statistics.endMemory();

        return transformedDs.decodeSequences(frequentSequences);
    }

    /**
     * 1. remove the not frequent items
     * 2. transform the original dataset into the new dataset of Integers
     * @param litemsets
     * @return
     */
    private TransformedAprioriDataset tranformationPhase(List<FrequentItemset> litemsets) {

        final BiMap<FrequentItemset, Integer> biMap = getTransformedMap(litemsets);
        List<Sequence> sequences = new ArrayList<>();

        for (Sequence element : aprioriDataset.getElements()) {

            Sequence converted = new Sequence();
            for (Itemset itemset : element) {
                Itemset transformedItemset = transformItemset(itemset,biMap);
                if (transformedItemset.size() > 0)
                    converted.add(transformedItemset);
            }
            if (converted.length() > 0)
                sequences.add(converted);
        }

        return new TransformedAprioriDataset(sequences, aprioriDataset.getRelativeSupport(), aprioriDataset.getAbsoluteSupport(),biMap);
    }

    /**
     *
     * @param itemset
     * @param biMap
     * @return return a new itemset converted in the biMap space
     */
    private Itemset transformItemset(Itemset itemset, BiMap<FrequentItemset, Integer> biMap) {

        List<String> list = new ArrayList<>();

        for(FrequentItemset litem: biMap.keySet()){
            if (itemset.contains(litem))
                list.add(biMap.get(litem).toString());
        }
        return new Itemset(list);
    }

    /**
     * @param list
     * @return return a bimap that maps the frequent itemesets to numbers
     */
    private BiMap<FrequentItemset, Integer> getTransformedMap(List<FrequentItemset> list) {
        Integer counter = 1;
        BiMap<FrequentItemset, Integer> transformationMap = HashBiMap.create();

        for (FrequentItemset frequentItemset : list) {
            transformationMap.put(frequentItemset, counter++);
        }

        return transformationMap;
    }

    private void writePatterns(Path outputPath, List<FrequentSequence> frequentSequences) throws IOException {
        final BufferedWriter out = Files.newBufferedWriter(outputPath);

        for (FrequentSequence sequence : frequentSequences) {
            out.write(sequence.toString() + "\n");
        }
        out.flush();
        out.close();
        statistics.setNumFrequentSequences(frequentSequences.size());
        statistics.setNumClosedFrequentSequences(frequentSequences.size());
    }

    private void writeStatistic(String datasetName, float minSupp, int absoluteSupport, String statisticsFile, PatternsType type) throws IOException {

        if (type == PatternsType.CLOSED){
            statistics.printClosedSequencesStat("Apriori_All_Closed",datasetName,minSupp,absoluteSupport,statisticsFile);
        }else if (type == PatternsType.FREQUENT){
            statistics.printFrequentSequencesStat("Apriori_All",datasetName,minSupp,absoluteSupport,statisticsFile);
        }
    }


    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("the need parameters are sequence_file, min_supp, statistics_file FREQUENT|CLOSED");
            System.err.println("sequences.txt 0.2 statistics.txt FREQUENT");
        } else {
            String inputFile = args[0];
            float minSupp = Float.parseFloat(args[1]);
            String statisticsFile = args[2];
            int lastPointIndex = inputFile.lastIndexOf(".");
            String outputFile = inputFile + "_" + minSupp + ".txt";
            PatternsType type = PatternsType.valueOf(args[3]);

            System.out.println("Start loading the dataset");
            AprioriDataset ds = AprioriDataset.fromPrefixSpanSource(Paths.get(inputFile), minSupp);
            System.out.println("End loading the dataset");

            AprioriAll aprioriAll = new AprioriAll(ds, type);
            System.out.println("Start sequence extraction");
            List<FrequentSequence>  frequentSequences = aprioriAll.run();
            System.out.println("End sequence extraction");

            //save patterns
            aprioriAll.writePatterns(Paths.get(outputFile), frequentSequences);
            //print statistics
            aprioriAll.writeStatistic(inputFile, minSupp, ds.getAbsoluteSupport(),statisticsFile, type);
        }
    }
}
