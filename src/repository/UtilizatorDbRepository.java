package repository;
import domain.User;
import validation.Validator;
import java.util.Collection;
import java.util.Optional;

/**
 * Database repository skeleton for users.
 * Database operations are not implemented in this Laboratory.
 */
public class UtilizatorDbRepository implements Repository<Long,User>{
    private final Validator<User> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Creates a database repository for users.
     *
     * @param validator validator for users
     * @param url database connection URL
     * @param username database username
     * @param password database password
     */
    public UtilizatorDbRepository(Validator<User> validator, String url, String username, String password){
        this.validator=validator;
        this.url=url;
        this.username=username;
        this.password=password;
    }
    @Override
    public void save(User entity){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public void update(User entity){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public void delete(Long id){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public java.util.Optional<User> findOne(Long id){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public Collection<User> findAll(){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
}
