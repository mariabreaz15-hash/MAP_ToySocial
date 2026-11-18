package repository;
import domain.User;
import util.paging.Page;
import util.paging.Pageable;
/**
 * Provides paginated access to a user's friends.
 */
public interface FriendshipPagingRepository {
    /**
     * Retrieves a page of friends for a user.
     *
     * @param userId identifier of the user
     * @param pageable pagination parameters
     * @return the requested page of friends
     */
    Page<User> findFriendsOnPage(Long userId, Pageable pageable);
}
