package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

/**
 *  message validator
 */
public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {
        String errors = "";
        if (entity == null)
            throw new ValidationException("Null user given for validation!");
        if (entity.getFrom() == null)
            throw new ValidationException ("Invalid ID!");
        if (!(entity.getFrom().getId().toString().matches("^[0-9]+$")))
            errors += "ID must be a positive integer!\n";
        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
