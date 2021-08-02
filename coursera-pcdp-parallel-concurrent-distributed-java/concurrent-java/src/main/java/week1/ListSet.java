package week1;

/**
 * The abstract interface implemented by each of the List versions tested in
 * this mini-project. Lists that support this interface must be able to add
 * objects, remove objects, and test for existence of an object. These methods
 * are required to maintain a sorted list of items internally with no
 * duplicates.
 */
public interface ListSet {

    /**
     * Add an integer value to this sorted list, ensuring uniqueness. This
     * method must use ListSet.head as the head of the list.
     *
     * @param key The integer to add.
     *
     * @return false if this value already exists in the list, true otherwise
     */
    boolean add(int key);

    /**
     * Remove an integer value from this list if it exists. This method must use
     * ListSet.head as the head of the list.
     *
     * @param key The integer to remove.
     *
     * @return true if this value is found in the list and successfully removed,
     * false otherwise
     */
    boolean remove(int key);

    /**
     * Check if this list contains the provided value. This method must use
     * ListSet.head as the head of the list.
     *
     * @param key The integer to check for.
     *
     * @return true if this list contains the target value, false otherwise.
     */
    boolean contains(int key);

    Entry getHead();
}