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
import java.util.stream.Collectors;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;
/** User repository persisted with to a simple id,firstName,lastName CSV file.*/
public class UserFileRepository extends InMemoryRepository<Long,User> {
    private final Path path;
    public UserFileRepository(String filename,Validator<User> validator){
        super(validator);
        path=Paths.get(filename);
        try{
            if(path.getParent()!=null) Files.createDirectories(path.getParent());
            if(!Files.exists(path)) Files.createFile(path);
            int lineNumber=0;
            try(Stream<String> stream=Files.lines(path,StandardCharsets.UTF_8)){
                List<String> lines=stream.collect(Collectors.toList());
                IntStream.range(0,lines.size()).filter(index->!lines.get(index).trim().isEmpty())
                        .filter(index->!lines.get(index).equalsIgnoreCase("id,firstName,lastName"))
                        .forEach(index->{
                            String line=lines.get(index);
                            try{
                                String[] parts=line.split(",",-1);
                                if(parts.length!=3){
                                    throw new IllegalArgumentException("Expected 3 columns.");
                                }
                                User user=new User(Long.parseLong(parts[0].trim()),
                                        parts[1].trim(),parts[2].trim());
                                super.save(user);
                            }catch(RuntimeException ex){
                                throw new RepositoryException("Invalid CSV file at line "+(index+1)+": "+line,ex);
                            }
                        });
            }
            /*for(String line:Files.readAllLines(path,StandardCharsets.UTF_8)){
                lineNumber++;
                if(line.isBlank() || line.equalsIgnoreCase("id,firstName,lastName")) continue;
                String[] parts=line.split(",",-1);
                if(parts.length!=3) throw new IllegalArgumentException("Expected 3 columns.");
                try{
                    super.save(new User(Long.parseLong(parts[0].trim()),parts[1].trim(),parts[2].trim()));
                } catch(RuntimeException ex){
                    throw new RepositoryException("Invalid CSV file " + lineNumber+": "+line,ex);
                }
            }*/
        }catch(IOException ex){throw new RepositoryException("Cannot read user files: "+path,ex);}
    }
    private void persist(){
        List<String> lines=new ArrayList<>();
        lines.add("id,firstName,lastName");
        /*for(User user:findAll())
            lines.add(user.getId()+","+user.getFirstName()+","+user.getLastName());*/
        lines.addAll(findAll().stream().map(user->user.getId()+","+
                user.getFirstName()+","+user.getLastName())
                .collect(Collectors.toList()));
        try{Files.write(path,lines,StandardCharsets.UTF_8);}
        catch(IOException ex){throw new RepositoryException("Cannot write user file: "+path,ex);}
    }
    @Override public User save(User user){User result=super.save(user); persist(); return result;}
    @Override public User delete(Long id){User result=super.delete(id); if(result != null) persist(); return result;}
    @Override public User update(User user){User result=super.update(user); if(result == null) persist(); return result;}
}
