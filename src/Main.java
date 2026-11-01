import repository.UserFileRepository;
import service.SocialNetworkService;
import ui.ConsoleUI;
import validation.UserValidator;
import domain.Friendship;
import domain.User;
import repository.Repository;
import repository.RepositoryFactory;
import validation.FriendshipValidator;
import domain.Tuple;
import exceptions.RepositoryException;
import validation.Validator;
import exceptions.ValidationException;
/** Application entry point. Run with the project root as working directory. */
public class Main {
    public static void main(String[] args){
        try{
            RepositoryFactory factory=new RepositoryFactory();
            Validator<User> additionalValidation=user->{
                if(user.getFirstName().length()>50){
                    throw new ValidationException("First name cannot exceed 50 characters.");
                }
            };
            Validator<User> userValidator=new UserValidator().andThen(additionalValidation);
            Repository<Long,User> users=factory.createUtilizatorRepository(userValidator);
            Repository<Tuple<Long,Long>,Friendship> friendships=factory.createPrietenieRepository(new FriendshipValidator(users));
            SocialNetworkService service=new SocialNetworkService(users,friendships);
            new ConsoleUI(service).run();
        }catch(RepositoryException | IllegalStateException e){
            System.err.println("ERROR: application could not be started.");
            System.err.println(e.getMessage());
        }
    }
}
