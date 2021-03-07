package socialnetwork.domain.validators;

/**
 *  interface
 * @param <T> - generic type
 */
public interface Validator<T> {

    /**
     * validates an entity
     * @param entity - entity must be not null
     * @throws ValidationException - if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}