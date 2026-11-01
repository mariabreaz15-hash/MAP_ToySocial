package repository;
import domain.Friendship;
import domain.Tuple;
import domain.User;
import validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
/**
 * Factory responsible for creating repositories
 * according to the application configuration.
 */
public class RepositoryFactory {
    private final Properties properties;
    private final RepositoryType repositoryType;
    /**
     * Loads and validates the repository configuration.
     */
    public RepositoryFactory(){
        properties=new Properties();
        Path configPath= Paths.get("src/main/resources/config.properties");
        try(InputStream input=Files.newInputStream(configPath)){
            properties.load(input);
        }catch(IOException e){
            throw new IllegalStateException("Cannot read configuration file: "+configPath,e);
        }
        String type=getRequiredProperty("repository.type");
        try{
            repositoryType=RepositoryType.valueOf(type.toUpperCase(java.util.Locale.ROOT));
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Unknown repository type: "+type,e);
        }
    }
    /**
     * Creates the configured user repository.
     *
     * @param validator the user validator
     * @return a user repository
     */
    public Repository<Long,User> createUtilizatorRepository(Validator<User> validator){
        Map<RepositoryType,Supplier<Repository<Long,User>>> creators=new EnumMap<>(RepositoryType.class);
        creators.put(RepositoryType.MEMORY,
                ()->new InMemoryRepository<>(validator));
        creators.put(RepositoryType.FILE,
                ()->new UtilizatorFileRepository(getRequiredProperty("file.utilizatori"),
                        validator));
        creators.put(RepositoryType.DATABASE,
                ()->new UtilizatorDbRepository(validator,
                        getRequiredProperty("db.url"),getRequiredProperty("db.user"),
                        properties.getProperty("db.password","")));
        return creators.get(repositoryType).get();
        /*switch(repositoryType){
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new UtilizatorFileRepository(getRequiredProperty("file.utilizatori"),validator);
            case DATABASE:
                return new UtilizatorDbRepository(validator,getRequiredProperty("db.url"),getRequiredProperty("db.user"),properties.getProperty("db.password",""));
            default:
                throw new IllegalStateException("Unsupported repository type: "+repositoryType);
        }*/
    }
    /**
     * Creates the configured friendship repository.
     *
     * @param validator the friendship validator
     * @return a friendship repository
     */
    public Repository<Tuple<Long,Long>,Friendship> createPrietenieRepository(Validator<Friendship> validator){
        Map<RepositoryType,Supplier<Repository<Tuple<Long,Long>,Friendship>>> creators=new EnumMap<>(RepositoryType.class);
        creators.put(RepositoryType.MEMORY,
                ()->new InMemoryRepository<>(validator));
        creators.put(RepositoryType.FILE,
                ()->new PrietenieFileRepository(getRequiredProperty("file.prietenii"),
                        validator));
        creators.put(RepositoryType.DATABASE,
                ()->new PrietenieDbRepository(validator,
                        getRequiredProperty("db.url"), getRequiredProperty("db.user"),
                        properties.getProperty("db.password","")));
        return creators.get(repositoryType).get();
        /*switch(repositoryType){
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new PrietenieFileRepository(getRequiredProperty("file.prietenii"),validator);
            case DATABASE:
                return new PrietenieDbRepository(validator,getRequiredProperty("db.url"),getRequiredProperty("db.user"),properties.getProperty("db.password",""));
            default:
                throw new IllegalStateException("Unsupported repository type: "+repositoryType);
        }*/
    }
    /**
     * Returns a required configuration property.
     *
     * @param key property name
     * @return property value
     * @throws IllegalArgumentException if missing or blank
     */
    private String getRequiredProperty(String key){
        String value=properties.getProperty(key);
        if(value==null||value.trim().isEmpty()){
            throw new IllegalArgumentException("Missing configuration property: "+key);
        }
        return value.trim();
    }
}
