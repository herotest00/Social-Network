package socialnetwork.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class TupleTest {

    @Test
    void getLeft() {
        Tuple<Integer, Integer> tuple = new Tuple<>(5, 4);
        Assertions.assertEquals((int) tuple.getLeft(), 5);
    }

    @Test
    void setLeft() {
        Tuple<Integer, Integer> tuple = new Tuple<>(5, 4);
        tuple.setLeft(10);
        Assertions.assertEquals((int) tuple.getLeft(), 10);

    }

    @Test
    void getRight() {
        Tuple<Integer, Integer> tuple = new Tuple<>(5, 4);
        Assertions.assertEquals((int) tuple.getRight(), 4);
    }

    @Test
    void setRight() {
        Tuple<Integer, Integer> tuple = new Tuple<>(5, 4);
        tuple.setRight(20);
        Assertions.assertEquals((int) tuple.getRight(), 20);
    }
}