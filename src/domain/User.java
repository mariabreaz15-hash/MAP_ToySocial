package domain;
/** A user in the social network. */
public class User extends Entity<Long>{
    private final String firstName;
    private final String lastName;
    public User(Long id,String firstName,String lastName){
        super(id);
        this.firstName=firstName;
        this.lastName=lastName;
    }
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    @Override public String toString(){
        return getId()+": "+firstName+" "+lastName;
    }
}
