package it.kdde.sequentialpatterns.apriori.tree;

import it.kdde.sequentialpatterns.model.FrequentItemset;
import it.kdde.sequentialpatterns.model.tree.ItemsetNodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiofumarola on 25/11/14.
 */
public class ClosedAprioriNode {

    private int position = -1;
    private final List<ClosedAprioriNode> children = new ArrayList<>();
    private ClosedAprioriNode parent;
    private FrequentItemset itemset;
    private ItemsetNodeType type = ItemsetNodeType.toCheck;

    ClosedAprioriNode(){

    }

    public ClosedAprioriNode(ClosedAprioriNode parent, FrequentItemset itemset, int position) {
        this.parent = parent;
        this.itemset = itemset;
        this.position = position;
    }

    public List<ClosedAprioriNode> getChildren() {
        return children;
    }

    public FrequentItemset getItemset() {
        return itemset;
    }

    public ClosedAprioriNode getParent() {
        return parent;
    }

    public int getPosition() {
        return position;
    }

    public void setType(ItemsetNodeType type) {
        this.type = type;
    }
}
