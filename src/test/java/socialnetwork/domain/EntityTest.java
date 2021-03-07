package socialnetwork.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class EntityTest {

    @Test
    void getId() {
        Entity<Long> entity = new Entity<>();
        Assertions.assertNull(entity.getId());
        entity.setId(14L);
        Assertions.assertEquals((long) entity.getId(), 14);
        Assertions.assertNotEquals(entity.getId(), null);
    }

    @Test
    void setId() {
        Entity<Long> entity = new Entity<>();
        entity.setId(14L);
        Assertions.assertEquals((long) entity.getId(), 14);
        Assertions.assertNotEquals(entity.getId(), null);
    }
}