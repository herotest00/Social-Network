package socialnetwork.repository;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.exceptions.RepoException;

import java.util.HashMap;


/**
 * CRUD operations repository interface
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */

public interface Repository<ID, E extends Entity<ID>> {

    /**
     * return the entity with the specified id
     * @param id -the id of the entity to be returned
     * @return the entity with the specified id
     *         or null - if there is no entity with the given id
     * @throws IllegalArgumentException - if id is null.
     */
    E findOne(ID id);

    /**
     *  returns all entities
     * @return all entities
     */
     HashMap<ID, E> findAll();

    /**
     *  saves the entity
     * @param entity - entity must be not null
     * @throws ValidationException - if the entity is not valid
     * @throws RepoException - if the user couldn't be saved
     */
    void save(E entity);


    /**
     *  removes the entity with the specified id
     * @param id - id must be not null
     * @throws IllegalArgumentException - if the given id is null.
     * @throws RepoException - if the user couldn't be deleted
     */
    void delete(ID id);

    /**
     *  updates the entity
     * @param entity - entity must not be null
     * @throws ValidationException - if the entity is not valid.
     * @throws RepoException - if the user couldn't be updated
     */
    void update(E entity);

    /**
     *  getter for the size of the map
     * @return the size of the hashmap.
     */
    int getSize();
}

