package org.example.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTest {

    @Test
    void testAddAndSize() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        list.add("A");
        list.add("B");
        list.add("C");

        assertEquals(3, list.size());
        assertFalse(list.isEmpty());
        assertEquals("A", list.getHead().getData());
    }

    @Test
    void testGetByIndex() {
        SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
    }

    @Test
    void testContains() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("Milk");
        list.add("Bread");

        assertTrue(list.contains("Milk"));
        assertTrue(list.contains("Bread"));
        assertFalse(list.contains("Cheese"));
    }

    @Test
    void testFind() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("X");
        list.add("Y");

        assertEquals("X", list.find("X"));
        assertEquals("Y", list.find("Y"));
        assertNull(list.find("Z"));
    }

    @Test
    void testRemoveHead() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("A");
        list.add("B");
        list.add("C");

        assertTrue(list.remove("A"));
        assertEquals(2, list.size());
        assertEquals("B", list.getHead().getData());
    }

    @Test
    void testRemoveMiddle() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("A");
        list.add("B");
        list.add("C");

        assertTrue(list.remove("B"));
        assertEquals(2, list.size());
        assertFalse(list.contains("B"));
    }

    @Test
    void testRemoveLast() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("A");
        list.add("B");

        assertTrue(list.remove("B"));
        assertEquals(1, list.size());
        assertFalse(list.contains("B"));
    }

    @Test
    void testRemoveNonExisting() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("A");
        list.add("B");

        assertFalse(list.remove("C"));
        assertEquals(2, list.size());
    }

    @Test
    void testClear() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("A");
        list.add("B");
        list.add("C");

        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
        assertNull(list.getHead());
    }

    @Test
    void testToString() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();

        list.add("One");
        list.add("Two");

        assertEquals("[One, Two]", list.toString());
    }

    @Test
    void addAfterRemovingLastElementAppendsAtEnd() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");

        list.remove("Three");
        list.add("Four");

        assertEquals("[One, Two, Four]", list.toString());
        assertEquals(3, list.size());
    }

    @Test
    void addAfterRemovingOnlyElementAndAfterClearWorks() {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        list.add("One");
        list.remove("One");
        list.add("Two");
        assertEquals("[Two]", list.toString());

        list.clear();
        list.add("Three");
        assertEquals("[Three]", list.toString());
    }
}
