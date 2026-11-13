package repository;
import domain.Entity;
import exceptions.RepositoryException;
import validation.Validator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    private final Path filePath;
    protected AbstractFileRepository(Validator<E> validator,String fileName){
        super(validator);
        this.filePath= Paths.get(fileName);
    }
    protected abstract E extractEntity(String line);
    protected abstract String createEntityAsString(E entity);

    /**
     * Loads and validates entities from the configured file.
     *
     * @throws RepositoryException if reading or parsing fails
     */
    protected final void loadFromFile(){
        if(!Files.exists(filePath)){
            return;
        }
        try(Stream<String> lines=Files.lines(filePath,StandardCharsets.UTF_8)){
            List<String> content=lines.collect(Collectors.toList());
            IntStream.range(0,content.size()).filter(index->!content.get(index).trim().isEmpty())
                    .forEach(index->{
                        String line=content.get(index);
                        try{
                            E entity=extractEntity(line);
                            super.save(entity);
                        }catch(RuntimeException ex){
                            throw new RepositoryException("Invalid line "+(index+1)+" in "+filePath+": "+ex.getMessage(),ex);
                        }
                    });
        }catch(IOException ex){
            throw new RepositoryException("Cannot read file: "+filePath,ex);
        }
        /*try(BufferedReader reader= Files.newBufferedReader(filePath,StandardCharsets.UTF_8)){
            String line;
            int lineNumber=0;
            while((line=reader.readLine())!=null){
                lineNumber++;
                if(line.isBlank()){
                    continue;
                }
                try{
                    E entity=extractEntity(line);
                    super.save(entity);
                }catch(RuntimeException ex){
                    throw new RepositoryException("Invalid line "+lineNumber+" in "+filePath+": "+ex.getMessage(),ex);
                }
            }
        }catch(IOException ex){
            throw new RepositoryException("Cannot read file: "+filePath,ex);
        }*/
    }
    private void writeToFile(){
        try{
            Path parent=filePath.getParent();
            if(parent!=null){
                Files.createDirectories(parent);
            }
            List<String> lines=findAll().stream()
                    .map(this::createEntityAsString).collect(Collectors.toList());
            Files.write(filePath,lines,StandardCharsets.UTF_8);
            /*try(BufferedWriter writer=Files.newBufferedWriter(filePath,StandardCharsets.UTF_8)){
                for(E entity:findAll()){
                    writer.write(createEntityAsString(entity));
                    writer.newLine();
                }
            }*/
        }catch(IOException ex){
            throw new RepositoryException("Cannot write file: "+filePath,ex);
        }
    }
    @Override
    public E save(E entity){
        E result=super.save(entity);
        writeToFile();
        return result;
    }
    @Override
    public E delete(ID id){
        E result=super.delete(id);
        if(result!=null){
            writeToFile();
        }
        return result;
    }
    @Override
    public E update(E entity){
        E result=super.update(entity);
        if(result==null){
            writeToFile();
        }
        return result;
    }
}
