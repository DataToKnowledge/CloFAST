package it.kdde.sequentialpatterns.apriori;

import it.kdde.sequentialpatterns.model.Itemset;
import it.kdde.sequentialpatterns.model.Sequence;
import util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by fabiofumarola on 20/11/14.
 */
public class AprioriDataset implements Iterable<Sequence>{

    public static final String ITEMSET_SEPARATOR = "-1";
    public static final String SEQUENCE_SEPARATOR = "-2";

    protected final List<Sequence> elements;
    protected final float relativeSupport;
    protected final int absoluteSupport;

    protected AprioriDataset(List<Sequence> elements, float relativeSupport, int absoluteSupport){
        this.elements = elements;
        this.relativeSupport = relativeSupport;
        this.absoluteSupport = absoluteSupport;
    }

    public int getAbsoluteSupport() {
        return absoluteSupport;
    }

    public float getRelativeSupport() {
        return relativeSupport;
    }

    public int size(){
        return elements.size();
    }

    public List<Sequence> getElements() {
        return elements;
    }


    public static AprioriDataset fromPrefixSpanSource(Path path, float relativeSupport) throws IOException {

        long numRows = Files.lines(path).count();
        int absoluteSupport = Utils.absoluteSupport(relativeSupport,numRows);
        List<Sequence> elements = new LinkedList<>();

        Files.lines(path).filter(l -> l.length() > 0).
                map(l -> l.split("\\s")).forEach( array -> {

            final Sequence sequence = new Sequence();
            Itemset itemset = new Itemset();

            for (String item : array){

                if (item.equals(ITEMSET_SEPARATOR)){
                    sequence.add(itemset);
                    itemset = new Itemset();
                }else if (item.equals(SEQUENCE_SEPARATOR)) {
                    break;
                } else {
                    itemset.addItem(item);
                }
            }
            elements.add(sequence);
        });

        return new AprioriDataset(elements,relativeSupport,absoluteSupport);
    }

    @Override
    public Iterator<Sequence> iterator() {
        return elements.iterator();
    }


}
