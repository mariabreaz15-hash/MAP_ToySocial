package domain;
import java.util.Objects;
/** Base entity identified by and ID. */
public abstract class Entity<ID> {
    private final ID id;
    protected Entity(ID id){this.id=id;}
    public ID getId(){return id;}
    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(obj==null||getClass()!=obj.getClass()){
            return false;
        }
        Entity<?> other=(Entity<?>) obj;
        return Objects.equals(id,other.id);
    }
    @Override
    public int hashCode(){
        return Objects.hash(getClass(),id);
    }
}
