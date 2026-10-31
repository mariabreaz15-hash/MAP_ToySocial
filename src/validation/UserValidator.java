package validation;
import domain.User;
import exceptions.ValidationException;
/** Validation strategy for users. */
public class UserValidator implements Validator<User> {
    @Override public void validate(User user){
        if(user==null) throw new ValidationException("User cannot be null.");
        if(user.getId()==null || user.getId()<=0)
            throw new ValidationException("User ID must be positive.");
        if(user.getFirstName()==null || user.getFirstName().isBlank())
            throw new ValidationException("First name cannot be empty.");
        if(user.getLastName()==null || user.getLastName().isBlank())
            throw new ValidationException("Last name cannot be empty.");
        if(user.getFirstName().contains(",")||user.getLastName().contains(",")||user.getFirstName().contains("\n")||user.getLastName().contains("\n")||user.getFirstName().contains("\r")||user.getLastName().contains("\r"))
            throw new ValidationException("Names cannot contains commas or newlines (CVS format).");
    }
}
