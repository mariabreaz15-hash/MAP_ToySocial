package repository;
import domain.Friendship;
import domain.Tuple;
import validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * File repository for friendships.
 *
 * Store friendships in the format:
 * firstUserId;secondUserId;friendshipDate
 */
public class PrietenieFileRepository extends AbstractFileRepository<Tuple<Long,Long>,Friendship> {
    /**
     * Creates a file repository for friendships.
     *
     * @param fileName path to the friendships file
     * @param validator validator for friendships entities
     */
    public PrietenieFileRepository(String fileName,Validator<Friendship> validator){
        super(validator, fileName);
        loadFromFile();
    }
    /**
     * Converts a line from the line into a friendship object.
     *
     * @param line line read from the file
     * @return the friendship represented by the line
     * @throws IllegalArgumentException if the line is invalid
     */
    @Override
    protected Friendship extractEntity(String line){
        String[] fields=line.split(";",-1);
        if(fields.length!=3){
            throw new IllegalArgumentException("Invalid friendship line: "+ line);
        }
        try{
            Long firstUserId=Long.parseLong(fields[0].trim());
            Long secondUserId=Long.parseLong(fields[1].trim());
            LocalDateTime friendshipDate=LocalDateTime.parse(fields[2].trim());
            return new Friendship(firstUserId,secondUserId,friendshipDate);
        }catch(NumberFormatException | DateTimeParseException e){
            throw new IllegalArgumentException("Invalid friendship line: "+line,e);
        }
    }
    /**
     * Converts a friendship object into a line for a file
     *
     * @param friendship friendship to serialize as text
     * @return line in the required file format
     */
    @Override
    protected String createEntityAsString(Friendship friendship){
        return friendship.getFirstUserId()+";"+friendship.getSecondUserId()+";"+friendship.getFriendshipDate();
    }
}
