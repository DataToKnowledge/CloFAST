package it.kdde.sequentialpatterns.apriori;

import it.kdde.sequentialpatterns.apriori.tree.ClosedAprioriNode;
import it.kdde.sequentialpatterns.apriori.tree.ClosedAprioriTree;
import it.kdde.sequentialpatterns.model.FrequentItemset;
import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.tree.ItemsetNodeType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fabiofumarola on 20/11/14.
 */
public abstract class LitemsetExtractor {

    protected final AprioriDataset aprioriDataset;

    public LitemsetExtractor(AprioriDataset aprioriDataset) {
        this.aprioriDataset = aprioriDataset;
    }

    public abstract List<FrequentItemset> litemsetPhase();

    /**
     * @return an hash map with the all the items in the
     */
    protected List<FrequentItemset> getFrequent1Items() {

        final Map<String, Integer> items = new HashMap<>();
        for (Sequence seq : aprioriDataset) {
            Set<String> distinctItems = seq.getElements().stream().
                    flatMap(i -> i.getElements().stream()).
                    collect(Collectors.toSet());

            distinctItems.forEach(item -> {
                items.putIfAbsent(item, 0);
                Integer value = items.get(item) + 1;
                items.replace(item, value);
            });

        }

        List<FrequentItemset> frequent1Items = items.entrySet().stream().
                filter(e -> e.getValue() >= aprioriDataset.getAbsoluteSupport()).
                map(e -> new FrequentItemset(e.getValue(), e.getKey())).
                collect(Collectors.toList());
        //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return frequent1Items;

    }

    /**
     * @param candidates
     * @return list of the frequent itemsets from the given candidates
     */
    protected List<FrequentItemset> countSupport(List<Itemset> candidates) {

        final List<FrequentItemset> result = new ArrayList<>();

        candidates.forEach(cand -> {

            int count = 0;

            for (Sequence seq : aprioriDataset) {
                if (seq.containsItemset(cand))
                    count++;
            }

            if (count >= aprioriDataset.getAbsoluteSupport()) {
                result.add(new FrequentItemset(count, cand.getElements()));
            }
        });

        return result.stream().distinct().collect(Collectors.toList());
    }

    /**
     * @param candidate
     * @return the support for a given candidate
     */
    protected int countSupport(Itemset candidate) {

        int count = 0;

        for (Sequence seq : aprioriDataset) {
            if (seq.containsItemset(candidate))
                count++;
        }

        return count;
    }

}

/**
 * Created by fabiofumarola on 20/11/14.
 */
class FrequentLitemsetExtractor extends LitemsetExtractor {

    public FrequentLitemsetExtractor(AprioriDataset aprioriDataset) {
        super(aprioriDataset);
    }

    @Override
    public List<FrequentItemset> litemsetPhase() {

        //get the frequent 1 items
        List<FrequentItemset> frequent1Items = getFrequent1Items();

        //do itemset extraction
        return itemsetExtraction(frequent1Items, new LinkedList<>(frequent1Items), 1);
    }

    private List<FrequentItemset> itemsetExtraction(List<FrequentItemset> levelMinus1, List<FrequentItemset> accumulator, int level) {

        //generate large itemset by combining the frequent itemset of level -1
        List<Itemset> candidates = candidateGenerations(levelMinus1);

        List<FrequentItemset> levelFrequentItemsets = countSupport(candidates);
        accumulator.addAll(levelFrequentItemsets);

        if (levelFrequentItemsets.isEmpty())
            return accumulator;
        else
            return itemsetExtraction(levelFrequentItemsets, accumulator, level + 1);
    }


    /**
     * @param levelMinus1
     * @return the set of generated candidates
     */
    private List<Itemset> candidateGenerations(List<FrequentItemset> levelMinus1) {

        List<Itemset> candidates = new ArrayList<>();

        for (int i = 0; i < levelMinus1.size(); i++) {
            FrequentItemset c1 = levelMinus1.get(i);
            for (int j = i + 1; j < levelMinus1.size(); j++) {
                FrequentItemset c2 = levelMinus1.get(j);

                Set<String> elems = new HashSet<String>();
                elems.addAll(c1.getElements());
                elems.addAll(c2.getElements());
                if (elems.size() == c1.size() + 1)
                    candidates.add(new Itemset(elems));
            }
        }
        return candidates;
    }

}

/**
 * Created by fabiofumarola on 20/11/14.
 */
class ClosedFrequentLitemset extends LitemsetExtractor {

    final Map<Integer, List<ClosedAprioriNode>> closedTable = new HashMap<>();
    final ClosedAprioriTree tree = new ClosedAprioriTree();

    public ClosedFrequentLitemset(AprioriDataset aprioriDataset) {
        super(aprioriDataset);
    }

    @Override
    public List<FrequentItemset> litemsetPhase() {

        List<FrequentItemset> frequent1Items = getFrequent1Items();

        final Queue<ClosedAprioriNode> queue = new LinkedList<>();
        int pos = 0;
        ClosedAprioriNode node;

        for (FrequentItemset item : frequent1Items) {
            node = tree.addChild(tree.getRoot(), item, pos++);
            queue.add(node);
        }

        while (!queue.isEmpty()) {
            node = queue.remove();
            closedItemsetExtension(node);
            queue.addAll(node.getChildren());
        }

        List<FrequentItemset> result = closedTable.values().stream().
                flatMap(l -> l.stream()).map(n -> n.getItemset()).
                sorted().
                collect(Collectors.toList());


        return result;
    }

    private void closedItemsetExtension(ClosedAprioriNode node) {

        boolean sentinel = false;
        int pos = 0;

        List<ClosedAprioriNode> children = node.getParent().getChildren();

        for (int i = node.getPosition() + 1; i < children.size(); i++) {
            ClosedAprioriNode rightBrother = children.get(i);
            Itemset candidate = node.getItemset().clone();
            candidate.addItem(rightBrother.getItemset().getLast());

            int count = countSupport(candidate);

            if (count >= aprioriDataset.getAbsoluteSupport()) {

                if (count == node.getItemset().getSupport()) {
                    //node is an intermediate node
                    node.setType(ItemsetNodeType.intermediate);
                    sentinel = true;
                }
                FrequentItemset frequentItemset = new FrequentItemset(count);
                frequentItemset.getElements().addAll(candidate.getElements());
                tree.addChild(node, frequentItemset, pos++);
            }
        }

        if (!sentinel){
            if (!leftCheck(node)){
                node.setType(ItemsetNodeType.closed);
                closedTable.putIfAbsent(node.getItemset().getSupport(), new ArrayList<>());
                closedTable.get(node.getItemset().getSupport()).add(node);
            }
        }

    }

    private boolean leftCheck(ClosedAprioriNode nodeToCheck) {
        Integer nodeSupp = nodeToCheck.getItemset().getSupport();
        final List<ClosedAprioriNode> toRemove = new ArrayList<>();

        List<ClosedAprioriNode> list = closedTable.getOrDefault(nodeSupp, new ArrayList<>());

        if (closedTable.containsKey(nodeSupp)){
            for (ClosedAprioriNode candidateClosed : list){

                if (candidateClosed.getItemset().contains(nodeToCheck.getItemset()))
                    return true;

                if (nodeToCheck.getItemset().contains(candidateClosed.getItemset())){
                    toRemove.add(candidateClosed);
                    candidateClosed.setType(ItemsetNodeType.notClosed);
                }
            }
        }

        list.removeAll(toRemove);
        return false;
    }
}
