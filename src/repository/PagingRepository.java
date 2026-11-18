package repository;
import domain.Entity;
import util.paging.Page;
import util.paging.Pageable;

/**
 * Extends a repository with database pagination support.
 *
 * @param <ID> identifier type
 * @param <E> entity type
 */
public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID,E> {
    /**
     * Retrieves one page of entities.
     *
     * @param pageable pagination parameters
     * @return the requested page and total element count
     */
    Page<E> findAllOnPage(Pageable pageable);
}
