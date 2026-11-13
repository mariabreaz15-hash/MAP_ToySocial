package repository;
import domain.User;
import validation.Validator;
import java.util.Collection;
import java.util.Optional;
import exceptions.RepositoryException;
import java.sql.*;
import java.util.*;

/**
 * Database repository skeleton for users.
 * Database operations are not implemented in this Laboratory.
 */
public class UtilizatorDbRepository implements Repository<Long,User>{
    private final Validator<User> validator;
    private final String url;
    private final String username;
    private final String password;

    /**
     * Creates a database repository for users.
     *
     * @param validator validator for users
     * @param url database connection URL
     * @param username database username
     * @param password database password
     */
    public UtilizatorDbRepository(Validator<User> validator, String url, String username, String password){
        this.validator=validator;
        this.url=url;
        this.username=username;
        this.password=password;
    }
    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }
    private User createUser(ResultSet rs) throws SQLException{
        return new User(rs.getLong("id"),rs.getString("first_name"),rs.getString("last_name"));
    }
    @Override
    public User save(User user){
        if(user==null){
            throw new IllegalArgumentException("User cannot be null.");
        }
        validator.validate(user);
        String sql="INSERT INTO users(first_name,last_name) "+"VALUES(?,?) RETURNING id";
        try (Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setString(1,user.getFirstName());
            statement.setString(2,user.getLastName());
            try(ResultSet rs=statement.executeQuery()){
                if(rs.next()){
                    long generatedId=rs.getLong("id");
                    return new User(generatedId,user.getFirstName(),user.getLastName());
                }
            }
            throw new RepositoryException("Database did not return a generated ID");
        }catch(SQLException e){
            throw new RepositoryException("Could not save user: "+e.getMessage(),e);
        }
    }
    @Override
    public User update(User user){
        if(user==null){
            throw new IllegalArgumentException("User cannot be null.");
        }
        validator.validate(user);
        String sql="UPDATE users SET first_name=?, "+"last_name=? WHERE id=?";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setString(1,user.getFirstName());
            statement.setString(2,user.getLastName());
            statement.setLong(3,user.getId());
            int affectedRows=statement.executeUpdate();
            return affectedRows == 0 ? user : null;
        }catch(SQLException e){
            throw new RepositoryException("Could not update user: "+e.getMessage(),e);
        }
    }
    @Override
    public User delete(Long id){
        if(id==null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        String sql="DELETE FROM users WHERE id = ? "+"RETURNING id,first_name,last_name";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id);
            try(ResultSet rs=statement.executeQuery()){
                if(rs.next()){
                    return createUser(rs);
                }
            }
            return null;
        }catch(SQLException e){
            throw new RepositoryException("Could not delete user: "+e.getMessage(),e);
        }
    }

    /**
     * Finds a user by its identifier.
     *
     * @param id the identifier of the user
     * @return an Optional containing the user if found, or an empty Optional otherwise
     * @throws IllegalArgumentException if id is null
     * @throws RepositoryException if a database error occurs
     */
    @Override
    public java.util.Optional<User> findOne(Long id){
        if(id==null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        String sql="SELECT id,first_name,last_name "+"FROM users WHERE id=?";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id);
            try(ResultSet rs=statement.executeQuery()){
                if(rs.next()){
                    return Optional.of(createUser(rs));
                }
            }
            return Optional.empty();
        }catch(SQLException e){
            throw new RepositoryException("Could not find user: "+e.getMessage(),e);
        }
    }
    @Override
    public Collection<User> findAll(){
        List<User> users=new ArrayList<>();
        String sql="SELECT id,first_name,last_name FROM users";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql);
        ResultSet rs=statement.executeQuery()){
            while(rs.next()){
                users.add(createUser(rs));
            }
            return users;
        }catch(SQLException e){
            throw new RepositoryException("Could not load users: "+e.getMessage(),e);
        }
    }
}
