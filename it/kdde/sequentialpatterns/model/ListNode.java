package it.kdde.sequentialpatterns.model;

import it.kdde.sequentialpatterns.model.tree.ClosedSequenceNode;

import java.util.LinkedList;


/**
 * @author eliana
 *         <p/>
 *         Basic node stored in a linked list
 */
public class ListNode {

    private ListNode next;
    //private int row;
    private int column;


    public ListNode(int c) {
        this.column = c;
    }

    public ListNode(int c, ListNode next) {
        this.column = c;
        this.next = next;
    }

    public int getColumn() {
        return column;
    }

    public void setNext(ListNode node) {
        this.next = node;
    }

    public ListNode next() {
        return next;
    }

    public ListNode before(ListNode succ) {
        while (succ != null) {
            if (this.column < succ.column)
                return succ;
            succ = succ.next;
        }
        return null;
    }


    /**
     * @param succsNodes
     * @param i
     * @return
     */
    public ListNode before(LinkedList<ClosedSequenceNode> succsNodes, Integer i) {

        ListNode curr = this;

        for (ClosedSequenceNode node : succsNodes) {
            curr = curr.before(node.getVerticalIdList().getElements()[i]);
            if (curr == null)
                break;
        }

        return curr;
    }

//	public boolean equal(LinkedList<ListNode[]> vilSuccList, int i) {
//		ListNode curr = vilSuccList.getFirst()[i];
//		return column == curr.column;
//	}


    /**
     * check equal on the current node
     *
     * @param succ
     * @return
     */
    public ListNode equal(ListNode succ) {

        while (succ != null) {
            if (this.column == succ.column)
                return succ;
            else
                succ = succ.next;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + " : " + column + "]");
        return buf.toString();
    }

}
