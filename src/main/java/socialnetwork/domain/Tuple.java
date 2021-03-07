package socialnetwork.domain;

import java.util.Objects;


/**
 * Define a Tuple o generic type entities
 * @param <E1> - tuple first entity type
 * @param <E2> - tuple second entity type
 */
public class Tuple<E1, E2> {
    private E1 e1;
    private E2 e2;

    /**
     *  constructor
     * @param e1 - the first element of the tuple
     * @param e2 - the second element of the tuple
     */
    public Tuple(E1 e1, E2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     *  getter for the left element of the tuple
     * @return the left element of the tuple
     */
    public E1 getLeft() {
        return e1;
    }

    /**
     *  setter for the left element of the tuple
     * @param e1 - the left element of the tuple that will be saved
     */
    public void setLeft(E1 e1) {
        this.e1 = e1;
    }

    /**
     *  getter for the right element of the tuple
     * @return the left element of the tuple
     */
    public E2 getRight() {
        return e2;
    }

    /**
     *  setter for the right element of the tuple
     * @param e2 - the right element of the tuple that will be saved
     */
    public void setRight(E2 e2) {
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "" + e1 + "," + e2;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.e1.equals(((Tuple) obj).e1) && this.e2.equals(((Tuple) obj).e2)) || (this.e1.equals(((Tuple) obj).e2) && this.e2.equals(((Tuple) obj).e1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2) + Objects.hash(e2, e1);
    }
}