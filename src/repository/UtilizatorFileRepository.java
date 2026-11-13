package repository;
import domain.User;
import validation.Validator;

/**
 * File repository for users.
 *
 * Stores users in the format:
 * id;firstName;lastName
 */
public class UtilizatorFileRepository extends AbstractFileRepository<Long,User>{
    /**
     * Creates a file repository for users
     *
     * @param fileName path to the users file
     * @param validator validator for users
     */
    public UtilizatorFileRepository(String fileName, Validator<User> validator){
        super(validator,fileName);
        loadFromFile();
    }
    /**
     * Converts a line from the file into a user entity.
     *
     * @param line line read from the file
     * @return user represented by the line
     * @throws IllegalArgumentException if the line is invalid
     */
    @Override
    protected User extractEntity(String line){
        String[] parts=line.split(";",-1);
        if(parts.length!=3){
            throw new IllegalArgumentException("Expected 3 columns: id;firstName;lastName");
        }
        try{
            Long id=Long.parseLong(parts[0].trim());
            String firstName=parts[1].trim();
            String lastName=parts[2].trim();
            return new User(id,firstName,lastName);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid user ID in line: "+line,e);
        }
    }
    /**
     * Converst a User entity into a line for the file.
     *
     * @param user user to convert
     * @return user represented as text
     */
    @Override
    protected String createEntityAsString(User user){
        return user.getId()+";"+user.getFirstName()+";"+user.getLastName();
    }
    @Override
    public User save(User user){
        if(user==null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if(user.getId()!=null){
            return super.save(user);
        }
        long maxId=0L;
        for(User existingUser : findAll()){
            if(existingUser.getId()>maxId){
                maxId=existingUser.getId();
            }
        }
        if(maxId==Long.MAX_VALUE){
            throw new exceptions.RepositoryException("No more user IDs available.");
        }
        long newId=maxId+1;
        User savedUser=new User(newId,user.getFirstName(),user.getLastName());
        return super.save(savedUser);
    }
}
