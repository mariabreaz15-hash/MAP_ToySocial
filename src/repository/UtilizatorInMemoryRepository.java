package repository;
import domain.User;
import validation.Validator;
public class UtilizatorInMemoryRepository extends InMemoryRepository<Long,User>{
    private long nextId=1L;
    public UtilizatorInMemoryRepository(Validator<User> validator){
        super(validator);
    }
    @Override
    public User save(User user){
        if(user==null){
            throw new IllegalArgumentException("User cannot be null.");
        }
        if(user.getId()==null) {
            if (nextId <= 0) {
                throw new IllegalStateException("No more user IDs available.");
            }
            User savedUser = new User(nextId, user.getFirstName(), user.getLastName());
            User result=super.save(savedUser);
            nextId++;
            return result;
        }
        User result=super.save(user);
        if(user.getId()>=nextId){
            nextId=user.getId()+1;
        }
        return result;
    }
}
