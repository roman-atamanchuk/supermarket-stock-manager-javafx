package org.example.util;

public class SinglyLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;   // last node, so add() doesn't walk the whole list
    private int size;

    public SinglyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // --- Add new element (O(1) using the tail pointer) ---
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.data;
    }
    // --- Remove by data ---
    public boolean remove(T data) {
        if (head == null) return false;

        if (head.data.equals(data)) {
            head = head.next;
            if (head == null) tail = null;
            size--;
            return true;
        }

        Node<T> current = head;
        while (current.next != null && !current.next.data.equals(data)) {
            current = current.next;
        }

        if (current.next != null) {
            if (current.next == tail) tail = current;
            current.next = current.next.next;
            size--;
            return true;
        }

        return false;
    }

    // --- Check if data exists ---
    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data)) return true;
            current = current.next;
        }
        return false;
    }

    // --- Find and return data ---
    public T find(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data)) return current.data;
            current = current.next;
        }
        return null;
    }

    // --- Clear entire list ---
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    // --- Print all data ---
    public void printAll() {
        Node<T> current = head;
        int index = 1;
        while (current != null) {
            System.out.println(index + ". " + current.data);
            current = current.next;
            index++;
        }
    }
    public Node<T> getHead() {
        return head;
    }
    // --- Getters ---
    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    // --- Optional: Debug display ---
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}