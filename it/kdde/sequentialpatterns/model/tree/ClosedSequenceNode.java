package it.kdde.sequentialpatterns.model.tree;

import java.util.LinkedList;
import java.util.List;

import it.kdde.sequentialpatterns.model.Sequence;
import it.kdde.sequentialpatterns.model.VerticalIdList;

public class ClosedSequenceNode {

	/**
	 * represents the position of the treenode in the parent children list
	 */
	private VerticalIdList vil;
	private List<ClosedSequenceNode> children = new LinkedList<>();
	private ClosedSequenceNode parent;
	private Sequence sequence;
	private NodeType type = NodeType.toCheck;
	private int absoluteSupport;

	/**
	 * For SequenceNode root
	 * 
	 * @param sizePositionList
	 */
	ClosedSequenceNode(int sizePositionList) {
		sequence = new Sequence();
		this.absoluteSupport = sizePositionList;
	}

	ClosedSequenceNode(ClosedSequenceNode parent, Sequence sequence, VerticalIdList vil, int absoluteSupport) {
		this.vil = vil;
		this.parent = parent;
		this.sequence = sequence;
		this.absoluteSupport = absoluteSupport;
	}

	public List<ClosedSequenceNode> getChildren() {
		return children;
	}

	public ClosedSequenceNode getParent() {
		return parent;
	}

	public VerticalIdList getVerticalIdList(){
		return vil;
	}

	public Sequence getSequence() {
		return sequence;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public int getAbsoluteSupport() {
		return absoluteSupport;
	}

	@Override
	public String toString() {
		return sequence.toString() + " #SUP " + this.absoluteSupport;
	}



    //TODO verificare se è corretto usare equals anzichè contains
	public boolean containsLastItemset(ClosedSequenceNode n) {
		if (sequence.getLastItemset().equals(n.sequence.getLastItemset()))
			return false;
		
		return sequence.getLastItemset().contains(n.getSequence().getLastItemset());
	}
}