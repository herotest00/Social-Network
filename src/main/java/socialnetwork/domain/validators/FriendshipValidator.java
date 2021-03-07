package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

/**
 *  friendship validator
 */
public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Null friendship given for validation!");
        if (entity.getId().getRight() == null || entity.getId().getLeft() == null)
            throw new ValidationException("Invalid ID!");
        else if (!(entity.getId().getLeft().toString().matches("^[0-9]+$")) || !(entity.getId().getRight().toString().matches("^[0-9]+$")))
            throw new ValidationException("ID must be positive a positive integer!");
        else if (entity.getId().getRight().equals(entity.getId().getLeft()))
            throw new ValidationException("IDs can't be the same!");
    }
}
