package socialnetwork.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 *  class that defines an entity
 * @param <ID> - generic type
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;
    private ID id;

    /**
     *  id getter
     * @return id of the entity
     */
    public ID getId() {
        return id;
    }

    /**
     *  id setter
     * @param id - new id of the entity
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}