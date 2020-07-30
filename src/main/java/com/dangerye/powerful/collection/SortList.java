package com.dangerye.powerful.collection;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class SortList<E> {

    private transient final int top;
    private transient final CompareFunction<E> compareFunction;
    private transient int count = 0;
    private transient Node<E> first;

    public SortList(int top, CompareFunction<E> compareFunction) {
        if (top <= 0)
            throw new IllegalArgumentException("top must better than zero");
        if (compareFunction == null)
            throw new IllegalArgumentException("compareFunction must not be null");
        this.top = top;
        this.compareFunction = compareFunction;
    }

    public List<E> toList() {
        List<E> result = new ArrayList<>(top);
        addToList(result, first);
        return result;
    }

    private void addToList(final List<E> list, final Node<E> node) {
        if (node == null) return;
        list.add(node.item);
        addToList(list, node.next);
    }

    public synchronized void add(final E e) {
        if (e == null) throw new NullPointerException();
        final Node<E> newNode = new Node<>(e, null);
        final Node<E> nextNode = first;
        if (nextNode == null) {
            first = newNode;
        } else {
            if (compareFunction.compare(nextNode.item, newNode.item)) {
                first = newNode;
                newNode.next = nextNode;
            } else {
                addNext(nextNode, e);
            }
        }
        count++;
        if (count > top) {
            removeLast(first);
        }
    }

    private void removeLast(final Node<E> node) {
        if (node.next.next == null) {
            node.next = null;
        } else {
            removeLast(node.next);
        }
    }

    private void addNext(final Node<E> node, final E e) {
        final Node<E> newNode = new Node<>(e, null);
        final Node<E> nextNode = node.next;
        if (nextNode == null) {
            node.next = newNode;
        } else {
            if (compareFunction.compare(nextNode.item, newNode.item)) {
                node.next = newNode;
                newNode.next = nextNode;
            } else {
                addNext(nextNode, e);
            }
        }
    }

    @FunctionalInterface
    public interface CompareFunction<E> {
        boolean compare(E nextItem, E newItem);
    }

    @AllArgsConstructor
    private static class Node<E> {
        E item;
        Node<E> next;
    }
}
