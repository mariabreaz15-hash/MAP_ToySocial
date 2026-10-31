package repository;
import domain.User;
import exceptions.RepositoryException;
import validation.Validator;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
/** User repository persisted with to a simple id,firstName,lastName CSV file.*/
public class UserFileRepository extends InMemoryRepository<Long,User> {
    private final Path path;
    public UserFileRepository(String filename,Validator<User> validator){
        super(validator);
        path=Path.of(filename);
        try{
            if(path.getParent()!=null) Files.createDirectories(path.getParent());
            if(!Files.exists(path)) Files.createFile(path);
            int lineNumber=0;
            for(String line:Files.readAllLines(path,StandardCharsets.UTF_8)){
                lineNumber++;
                if(line.isBlank() || line.equalsIgnoreCase("id,firstName,lastName")) continue;
                String[] parts=line.split(",",-1);
                if(parts.length!=3) throw new IllegalArgumentException("Expected 3 columns.");
                try{
                    super.save(new User(Long.parseLong(parts[0].trim()),parts[1].trim(),parts[2].trim()));
                } catch(RuntimeException ex){
                    throw new RepositoryException("Invalid CSV file " + lineNumber+": "+line,ex);
                }
            }
        }catch(IOException ex){throw new RepositoryException("Cannot read user files: "+path,ex);}
    }
    private void persist(){
        List<String> lines=new ArrayList<>();
        lines.add("id,firstName,lastName");
        for(User user:findAll())
            lines.add(user.getId()+","+user.getFirstName()+","+user.getLastName());
        try{Files.write(path,lines,StandardCharsets.UTF_8);}
        catch(IOException ex){throw new RepositoryException("Cannot write user file: "+path,ex);}
    }
    @Override public void save(User user){super.save(user); persist();}
    @Override public void delete(Long id){super.delete(id); persist();}
    @Override public void update(User user){super.update(user); persist();}
}
