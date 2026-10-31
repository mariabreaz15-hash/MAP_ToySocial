package domain;
import java.util.Objects;
import java.time.LocalDateTime;
/** Undirected friendship between two distinct users. */
public class Friendship extends Entity<Tuple<Long,Long>>{
    private final Long firstUserId;
    private final Long secondUserId;
    private final LocalDateTime friendshipDate;
    public Friendship(Long firstUserId,Long secondUserId,LocalDateTime friendshipDate){
        super(new Tuple<>(Math.min(Objects.requireNonNull(firstUserId),Objects.requireNonNull(secondUserId)),Math.max(firstUserId,secondUserId)));
        this.firstUserId=Math.min(firstUserId,secondUserId);
        this.secondUserId=Math.max(firstUserId,secondUserId);
        this.friendshipDate=Objects.requireNonNull(friendshipDate,"Friendship date cannot be null.");
    }
    public Friendship(Long firstUserId,Long secondUserId) {
        this(firstUserId, secondUserId, LocalDateTime.now());
    }
    public Long getFirstUserId(){return firstUserId;}
    public Long getSecondUserId(){return secondUserId;}
    public LocalDateTime getFriendshipDate(){
        return friendshipDate;
    }
    @Override public boolean equals(Object obj){
        if(this==obj) return true;
        if(!(obj instanceof Friendship other)) return false;
        return Objects.equals(firstUserId,other.firstUserId) && Objects.equals(secondUserId,other.secondUserId);
    }
    @Override public int hashCode(){return Objects.hash(firstUserId,secondUserId);}
    @Override public String toString(){return firstUserId+"<->"+secondUserId;}
}
