package repository;
import domain.Friendship;
import domain.Tuple;
import validation.Validator;
import java.util.Collection;
import java.util.Optional;

/**
 * Database repository skeleton for friendships.
 * Database operations are not implemented in this Laboratory.
 */
public class PrietenieDbRepository implements Repository<Tuple<Long,Long>,Friendship>{
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;
    /**
     * Creates a database repository for friendships.
     *
     * @param validator validator for friendships
     * @param url database connection URL
     * @param username database username
     * @param password database password
     */
    public PrietenieDbRepository(Validator<Friendship> validator,String url,String username,String password){
        this.validator=validator;
        this.url=url;
        this.username=username;
        this.password=password;
    }
    @Override
    public void save(Friendship entity){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public Optional<Friendship> findOne(Tuple<Long,Long> id){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public Collection<Friendship> findAll(){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public void delete(Tuple<Long,Long> id){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
    @Override
    public void update(Friendship entity){
        throw new UnsupportedOperationException("Database repository is not implemented yet.");
    }
}
