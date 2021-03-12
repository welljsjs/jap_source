/*
 * Decompiled with CFR 0.150.
 */
package HTTPClient;

class LinkedList {
    private LinkElement head = null;
    private LinkElement tail = null;
    private LinkElement next_enum = null;

    LinkedList() {
    }

    public synchronized void addToHead(Object object) {
        this.head = new LinkElement(object, this.head);
        if (this.head.next == null) {
            this.tail = this.head;
        }
    }

    public synchronized void addToEnd(Object object) {
        if (this.head == null) {
            this.head = this.tail = new LinkElement(object, null);
        } else {
            this.tail = this.tail.next = new LinkElement(object, null);
        }
    }

    public synchronized void remove(Object object) {
        if (this.head == null) {
            return;
        }
        if (this.head.element == object) {
            this.head = this.head.next;
            return;
        }
        LinkElement linkElement = this.head;
        while (linkElement.next != null) {
            if (linkElement.next.element == object) {
                if (linkElement.next == this.tail) {
                    this.tail = linkElement;
                }
                linkElement.next = linkElement.next.next;
                return;
            }
            linkElement = linkElement.next;
        }
    }

    public synchronized Object getFirst() {
        if (this.head == null) {
            return null;
        }
        return this.head.element;
    }

    public synchronized Object enumerate() {
        if (this.head == null) {
            return null;
        }
        this.next_enum = this.head.next;
        return this.head.element;
    }

    public synchronized Object next() {
        if (this.next_enum == null) {
            return null;
        }
        Object object = this.next_enum.element;
        this.next_enum = this.next_enum.next;
        return object;
    }

    public static void main(String[] arrstring) throws Exception {
        System.err.println("\n*** Linked List Tests ...");
        LinkedList linkedList = new LinkedList();
        linkedList.addToHead("One");
        linkedList.addToEnd("Last");
        if (!linkedList.getFirst().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!linkedList.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!linkedList.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        if (linkedList.next() != null) {
            throw new Exception("End of list wrong");
        }
        linkedList.remove("One");
        if (!linkedList.getFirst().equals("Last")) {
            throw new Exception("First element wrong");
        }
        linkedList.remove("Last");
        if (linkedList.getFirst() != null) {
            throw new Exception("End of list wrong");
        }
        linkedList = new LinkedList();
        linkedList.addToEnd("Last");
        linkedList.addToHead("One");
        if (!linkedList.getFirst().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!linkedList.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!linkedList.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        if (linkedList.next() != null) {
            throw new Exception("End of list wrong");
        }
        if (!linkedList.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        linkedList.remove("One");
        if (!linkedList.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        linkedList.remove("Last");
        if (linkedList.next() != null) {
            throw new Exception("End of list wrong");
        }
        linkedList = new LinkedList();
        linkedList.addToEnd("Last");
        linkedList.addToHead("Two");
        linkedList.addToHead("One");
        if (!linkedList.getFirst().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!linkedList.enumerate().equals("One")) {
            throw new Exception("First element wrong");
        }
        if (!linkedList.next().equals("Two")) {
            throw new Exception("Second element wrong");
        }
        if (!linkedList.next().equals("Last")) {
            throw new Exception("Last element wrong");
        }
        if (linkedList.next() != null) {
            throw new Exception("End of list wrong");
        }
        linkedList.remove("Last");
        linkedList.remove("Two");
        linkedList.remove("One");
        if (linkedList.getFirst() != null) {
            throw new Exception("Empty list wrong");
        }
        System.err.println("\n*** Tests finished successfuly");
    }

    private class LinkElement {
        Object element;
        LinkElement next;

        LinkElement(Object object, LinkElement linkElement) {
            this.element = object;
            this.next = linkElement;
        }
    }
}

