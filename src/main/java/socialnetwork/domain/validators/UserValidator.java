package socialnetwork.domain.validators;

import socialnetwork.domain.User;

/**
 *  user validator
 */
public class UserValidator implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";
        if (entity == null)
            throw new ValidationException("Null user given for validation!");
        else if (!entity.getFirstName().matches("^[A-Z][a-z]{2,}$"))
            errors += "Invalid first name!\n";
        else if (!(entity.getLastName().matches("^[A-Z][a-z]{2,}$")))
            errors += "Invalid last name!\n";
        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
