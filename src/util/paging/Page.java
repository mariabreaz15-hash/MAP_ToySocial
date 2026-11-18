package util.paging;
import java.util.List;

/**
 * Represents a page of elements and the total number
 * of elements available in the repository.
 *
 * @param <E> elemt type
 */
public class Page<E> {
    private final List<E> elementsOnPage;
    private final long totalNumberOfElements;
    /**
     * Creates a page
     *
     * @param elementsOnPage elements retrieved for the page
     * @param totalNumberOfElements total matching elements
     */
    public Page(List<E> elementsOnPage,long totalNumberOfElements){
        this.elementsOnPage=elementsOnPage;
        this.totalNumberOfElements=totalNumberOfElements;
    }
    public List<E> getElementsOnPage(){
        return elementsOnPage;
    }
    public long getTotalNumberOfElements(){
        return totalNumberOfElements;
    }
}
