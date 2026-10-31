package service;
import domain.User;
import java.util.function.Predicate;
public final class CriteriiUtilizator {
    private CriteriiUtilizator(){

    }
    /**
     * Creates a predicate for a given last name.
     *
     * @param lastName the last name to search for
     * @return a predicate matching the last name
     */
    public static Predicate<User> nume(String lastName){
        return user->user.getLastName().equalsIgnoreCase(lastName);
    }
    /**
     * Creates a predicate for a given first name.
     *
     * @param firstName the first name to search for
     * @returnn a predicate matching the last name
     */
    public static Predicate<User> prenume(String firstName){
        return user->user.getFirstName().equalsIgnoreCase(firstName);
    }
    /**
     * Creates a predicate for IDs greater than a limit.
     *
     * @param minimum the exclusive lower bound
     * @return a predicate matching IDs greater than minimum
     */
    public static Predicate<User> idMaiMareDecat(long minimum){
        return user->user.getId()>minimum;
    }
    /**
     * Creates a predicate for users without friendships.
     *
     * @param service the social network service
     * @return a predicate matching users without friends
     */
    public static Predicate<User> faraPrieteni(SocialNetworkService service){
        return user->service.getFriendships().stream().noneMatch(friendship->friendship.getFirstUserId().equals(user.getId())||friendship.getSecondUserId().equals(user.getId()));
    }
}
