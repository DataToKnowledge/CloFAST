package it.kdde.sequentialpatterns.apriori.tree;

import it.kdde.sequentialpatterns.model.FrequentItemset;
import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.tree.ItemsetNodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fabiofumarola on 25/11/14.
 */
public class ClosedAprioriTree {

    private ClosedAprioriNode root = new ClosedAprioriNode();

    private HashMap<Integer,List<FrequentItemset>> closedTable = new HashMap<>();

    public ClosedAprioriNode addChild(ClosedAprioriNode parent, FrequentItemset itemset, int position){
        ClosedAprioriNode newNode = new ClosedAprioriNode(parent, itemset,position);
        parent.getChildren().add(newNode);
        return newNode;
    }

    public ClosedAprioriNode getRoot() {
        return root;
    }
}
