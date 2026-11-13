package repository;
import domain.Entity;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/** Generic CRUD repository. */
public interface Repository<ID, E extends Entity<ID>> {
    E save(E entity);
    Optional<E> findOne(ID id);
    Collection<E> findAll();
    E delete(ID id);
    E update(E entity);
    /**
     * Finds entities matching a predicate.
     *
     * @param filter the condition used to select filters
     * @return a list containing matching entities
     * @throws IllegalArgumentException if filter is null
     */
    default List<E> findAll(Predicate<E> filter){
        if(filter==null){
            throw new IllegalArgumentException("Filter cannot be null.");
        }
        return findAll().stream().filter(filter).collect(Collectors.toList());
    }
}
