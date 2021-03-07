package socialnetwork.domain.validators;

import socialnetwork.domain.FriendRequest;

/**
 *  friend request validator
 */
public class FriendRequestValidator implements Validator<FriendRequest> {

    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("Null friend request given for validatior!");
        if (entity.getId().getRight() == null || entity.getId().getLeft() == null)
            throw new ValidationException("Invalid ID!");
        else if (!(entity.getId().getLeft().toString().matches("^[0-9]+$")) || !(entity.getId().getRight().toString().matches("^[0-9]+$")))
            throw new ValidationException("ID must be positive a positive integer!");
        else if (entity.getId().getRight().equals(entity.getId().getLeft()))
            throw new ValidationException("Can't send a friend request to yourself!");
    }
}
