package repository;
import domain.Entity;
import exceptions.RepositoryException;
import validation.Validator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**Generic in memory repository with injectable validation strategy. */
public class InMemoryRepository<ID, T extends Entity<ID>> implements Repository<ID,T> {
    protected final Map<ID,T> entities=new LinkedHashMap<>();
    private final Validator<T> validator;
    public InMemoryRepository(Validator<T> validator){this.validator=validator;}
    @Override public void save(T entity){
        if(entity==null){
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        validator.validate(entity);
        if(entities.containsKey(entity.getId())) throw new RepositoryException("ID already exists.");
        entities.put(entity.getId(),entity);
    }
    @Override public Optional<T> findOne(ID id){
        if(id==null){
            throw new IllegalArgumentException("ID cannnot be null.");
        }
        return Optional.ofNullable(entities.get(id));
    }
    @Override public Collection<T> findAll(){return java.util.List.copyOf(entities.values());}
    @Override public void delete(ID id){
        if(id==null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if(entities.remove(id)==null) throw new RepositoryException("ID not found.");
    }
    @Override public void update(T entity){
        if(entity==null){
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        validator.validate(entity);
        if(!entities.containsKey(entity.getId())) throw new RepositoryException("ID not found.");
        entities.put(entity.getId(),entity);
    }
}
