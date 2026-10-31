package validation;
import domain.Friendship;
import domain.User;
import exceptions.ValidationException;
import repository.Repository;

/**
 * Validates friendship entities and their referenced users.
 */
public class FriendshipValidator implements Validator<Friendship> {
    private final Repository<Long,User> userRepository;
    /**
     * Creates a friendship validator.
     *
     * @param userRepository repository used to verify that users exists
     */
    public FriendshipValidator(Repository<Long,User> userRepository){
        if(userRepository==null){
            throw new IllegalArgumentException("User repository cannot be null.");
        }
        this.userRepository=userRepository;
    }
    /**
     * Validates a friendship
     *
     * @param friendship friendship to validate
     * @throws ValidationException if the friendship is invalid
     */
    @Override
    public void validate(Friendship friendship){
        if(friendship==null){
            throw new ValidationException("Friendship cannot be null");
        }
        Long firstId=friendship.getFirstUserId();
        Long secondId=friendship.getSecondUserId();
        if(firstId==null || secondId==null){
            throw new ValidationException("Friendship users IDs cannot be null.");
        }
        if(firstId<=0||secondId<=0){
            throw new ValidationException("Friendship users IDs must be positive.");
        }
        if(firstId.equals(secondId)){
            throw new ValidationException("A user cannot be friends with themselves.");
        }
        if(friendship.getFriendshipDate()==null){
            throw new ValidationException("Friendship date cannot be null.");
        }
        if(userRepository.findOne(firstId).isEmpty()){
            throw new ValidationException("User with ID "+firstId+" does not exist.");
        }
        if(userRepository.findOne(secondId).isEmpty()){
            throw new ValidationException("User with ID "+secondId+" does not exist.");
        }
    }
}
