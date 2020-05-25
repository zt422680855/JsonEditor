package com.demo.trans.specifications;

/**
 * @Description:
 * @Author: zhengt
 * @CreateDate: 2020/5/25 22:50
 */
public class Node<E> {
    public Node<E> prev;
    public Node<E> next;
    public E data;

    public Node(E data) {
        this.data = data;
        this.prev = null;
        this.next = null;
    }

    public void linkNext(Node<E> node) {
        if (node == null) {
            // unlink next, return
            this.unlinkNext();
            return;
        }
        if (node.prev != null) {
            // need to unlink previous node from node
            node.unlinkPrev();
        }
        if (this.next != null) {
            // need to unlink next node from this
            this.unlinkNext();
        }
        this.next = node;
        node.prev = this;
    }

    public void linkPrev(Node<E> node) {
        if (node == null) {
            this.unlinkPrev();
            return;
        }
        if (node.next != null) {
            // need to unlink next node from node
            node.unlinkNext();
        }
        if (this.prev != null) {
            // need to unlink prev from this
            this.unlinkPrev();
        }
        this.prev = node;
        node.next = this;
    }

    public void unlinkNext() {
        this.next.prev = null;
        this.next = null;
    }

    public void unlinkPrev() {
        this.prev.next = null;
        this.prev = null;
    }

}
