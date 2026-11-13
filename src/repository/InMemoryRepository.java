package repository;
import domain.Entity;
import exceptions.RepositoryException;
import validation.Validator;
import java.util.*;
/**Generic in memory repository with injectable validation strategy. */
public class InMemoryRepository<ID, T extends Entity<ID>> implements Repository<ID,T> {
    protected final Map<ID,T> entities=new LinkedHashMap<>();
    private final Validator<T> validator;
    public InMemoryRepository(Validator<T> validator){this.validator=validator;}
    @Override public T save(T entity){
        if(entity==null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        validator.validate(entity);
        if(entity.getId()==null){
            throw new IllegalArgumentException("Entity ID cannot be null.");
        }
        if(entities.containsKey(entity.getId())) throw new RepositoryException("ID already exists.");
        entities.put(entity.getId(),entity);
        return entity;
    }
    @Override public Optional<T> findOne(ID id){
        if(id==null){
            throw new IllegalArgumentException("ID cannnot be null.");
        }
        return Optional.ofNullable(entities.get(id));
    }
    @Override public Collection<T> findAll(){return new ArrayList<>(entities.values());}
    @Override public T delete(ID id){
        if(id==null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        T result=entities.remove(id);
        //if(result==null) throw new RepositoryException("ID not found.");
        return result;
    }
    @Override public T update(T entity){
        if(entity==null){
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        validator.validate(entity);
        if(!entities.containsKey(entity.getId())) return entity;
        entities.put(entity.getId(),entity);
        return null;
    }
}
