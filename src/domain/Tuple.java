package domain;
import java.util.Objects;

/**
 * Represents an immutable pair of values.
 *
 * @param <A> type of the first value
 * @param <B> type of the second value
 */
public final class Tuple<A,B> {
    private final A first;
    private final B second;
    /**
     * Creates a tuple containing two values.
     *
     * @param first the first value
     * @param second the second value
     */
    public Tuple(A first,B second){
        this.first=first;
        this.second=second;
    }
    /**
     * Returns the first value.
     *
     * @return the first value
     */
    public A getFirst(){
        return first;
    }
    /**
     * Returns the second value.
     *
     * @return the second value
     */
    public B getSecond(){
        return second;
    }
    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(!(obj instanceof Tuple<?,?> other)){
            return false;
        }
        return Objects.equals(first,other.first)&&Objects.equals(second,other.second);
    }
    @Override
    public int hashCode(){
        return Objects.hash(first,second);
    }
    @Override
    public String toString(){
        return "("+first+","+second+")";
    }
}
