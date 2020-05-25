package com.demo.trans.specifications;

import java.awt.*;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:50
 */
public class TreeEvent extends AWTEvent {
    boolean isAdd;
    /**
     * The node to add/remove from
     */
    protected DnDNode destination;
    /**
     * The node to be added/removed, or the parent of the node that was moved
     * from
     */
    protected DnDNode node;
    /**
     * The index to add/remove node to
     */
    protected int index;

    /**
     * Creates an event that adds/removes items from the tree at the specified
     * node and index.
     *
     * @param source
     * @param add
     * @param destination
     * @param node
     * @param index
     */
    public TreeEvent(Object source, boolean isAdd, DnDNode destination, DnDNode node, int index) {
        super(source, AWTEvent.RESERVED_ID_MAX + 1);
        this.destination = destination;
        this.node = node;
        this.index = index;
        this.isAdd = isAdd;
    }

    public TreeEvent invert() {
        return new TreeEvent(this.source, !this.isAdd, this.destination, this.node, this.index);

    }

    /**
     * @return the destination
     */
    public DnDNode getDestination() {
        return this.destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(DnDNode destination) {
        this.destination = destination;
    }

    /**
     * @return the node
     */
    public DnDNode getNode() {
        return this.node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(DnDNode node) {
        this.node = node;
    }

    public boolean isAdd() {
        return this.isAdd;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Add remove " + this.destination + " " + this.node + " " + this.index + " " + this.isAdd;
    }

}
