package repository;
import domain.Entity;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/** Generic CRUD repository. */
public interface Repository<ID, T extends Entity<ID>> {
    void save(T entity);
    Optional<T> findOne(ID id);
    Collection<T> findAll();
    void delete(ID id);
    void update(T entity);
    /**
     * Finds entities matching a predicate.
     *
     * @param filter the condition used to select filters
     * @return a list containing matching entities
     * @throws IllegalArgumentException if filter is null
     */
    default List<T> findAll(Predicate<T> filter){
        if(filter==null){
            throw new IllegalArgumentException("Filter cannot be null.");
        }
        return findAll().stream().filter(filter).collect(Collectors.toList());
    }
}
