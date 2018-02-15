package it.kdde.sequentialpatterns.model;

/**
 * Created by fabiofumarola on 15/11/14.
 */
public class VerticalIdList {

    private ListNode[] elements;

    public VerticalIdList(ListNode[] elements, int absoluteSupport){
        this.elements = elements;
    }

    public ListNode[] getElements() {
        return elements;
    }
}
