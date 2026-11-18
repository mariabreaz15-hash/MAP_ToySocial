package repository;
import domain.Friendship;
import domain.Tuple;
import validation.Validator;
import java.util.Collection;
import java.util.Optional;
import exceptions.RepositoryException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import domain.User;
import util.paging.Page;
import util.paging.Pageable;
import java.util.ArrayList;
import java.util.List;
import repository.FriendshipPagingRepository;
/**
 * Database repository skeleton for friendships.
 * Database operations are not implemented in this Laboratory.
 */
public class PrietenieDbRepository implements Repository<Tuple<Long,Long>,Friendship>,FriendshipPagingRepository{
    private final Validator<Friendship> validator;
    private final String url;
    private final String username;
    private final String password;
    /**
     * Creates a database repository for friendships.
     *
     * @param validator validator for friendships
     * @param url database connection URL
     * @param username database username
     * @param password database password
     */
    public PrietenieDbRepository(Validator<Friendship> validator,String url,String username,String password){
        this.validator=validator;
        this.url=url;
        this.username=username;
        this.password=password;
    }
    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }
    private Friendship createFriendship(ResultSet rs) throws SQLException{
        return new Friendship(rs.getLong("id1"),rs.getLong("id2"),rs.getTimestamp("friends_from").toLocalDateTime());
    }
    @Override
    public Friendship save(Friendship friendship){
        if(friendship==null){
            throw new IllegalArgumentException("Friendship cannot be null.");
        }
        validator.validate(friendship);
        long id1=Math.min(friendship.getFirstUserId(),friendship.getSecondUserId());
        long id2=Math.max(friendship.getFirstUserId(),friendship.getSecondUserId());
        String sql="INSERT INTO friendships(id1,id2,friends_from) "+"VALUES(?,?,?)";
        try (Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id1);
            statement.setLong(2,id2);
            statement.setTimestamp(3,Timestamp.valueOf(friendship.getFriendshipDate()));
            statement.executeUpdate();
            return friendship;
        }catch(SQLException e){
            throw new RepositoryException("Could not save friendship: "+e.getMessage(),e);
        }
    }
    @Override
    public Optional<Friendship> findOne(Tuple<Long,Long> id){
        if(id==null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        long id1=Math.min(id.getFirst(),id.getSecond());
        long id2=Math.max(id.getFirst(),id.getSecond());
        String sql="SELECT id1, id2, friends_from "+"FROM friendships WHERE id1=? AND id2=?";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id1);
            statement.setLong(2,id2);
            try(ResultSet rs=statement.executeQuery()){
                if(rs.next()){
                    return Optional.of(createFriendship(rs));
                }
            }
            return Optional.empty();
        }catch(SQLException e){
            throw new RepositoryException("Could not find friendship: "+e.getMessage(),e);
        }
    }
    @Override
    public Collection<Friendship> findAll(){
        List<Friendship> result=new ArrayList<>();
        String sql="SELECT id1, id2, friends_from FROM friendships";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql);
        ResultSet rs=statement.executeQuery()){
            while(rs.next()){
                result.add(createFriendship(rs));
            }
            return result;
        }catch(SQLException e){
            throw new RepositoryException("Could not load friendships: "+e.getMessage(),e);
        }
    }
    @Override
    public Friendship delete(Tuple<Long,Long> id){
        if(id==null){
            throw new IllegalArgumentException("ID cannot be null.");
        }
        long id1=Math.min(id.getFirst(),id.getSecond());
        long id2=Math.max(id.getFirst(),id.getSecond());
        String sql="DELETE FROM friendships "+"WHERE id1=? AND id2=? "+"RETURNING id1,id2,friends_from";
        try(Connection connection=getConnection();
        PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setLong(1,id1);
            statement.setLong(2,id2);
            try(ResultSet rs=statement.executeQuery()){
                if(rs.next()){
                    return createFriendship(rs);
                }
            }
            return null;
        }catch(SQLException e){
            throw new RepositoryException("Could not delete friendship: "+e.getMessage(),e);
        }
    }
    @Override
    public Friendship update(Friendship friendship){
        if (friendship == null) {
            throw new IllegalArgumentException(
                    "Friendship cannot be null");
        }
        validator.validate(friendship);
        long id1 = Math.min(friendship.getFirstUserId(), friendship.getSecondUserId());
        long id2=Math.max(friendship.getFirstUserId(),friendship.getSecondUserId());
        String sql="UPDATE friendships SET friends_from=? "+"WHERE id1=? AND id2=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(friendship.getFriendshipDate()));
            statement.setLong(2, id1);
            statement.setLong(3, id2);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 0 ? friendship : null;

        } catch (SQLException e) {
            throw new RepositoryException("Could not update friendship: " + e.getMessage(), e);
        }
    }
    /**
     * Retrieves a page of friends for the specified user.
     *
     * @param userId identifier of the user
     * @param pageable pagination parameters
     * @return a page of friends and their total count
     */
    public Page<User> findFriendsOnPage(Long userId,Pageable pageable){
        if(userId==null){
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        List<User> friends=new ArrayList<>();
        String sql="SELECT u.id,u.first_name,u.last_name "+
                "FROM friendships f "+
                "JOIN users u ON "+
                "((f.id1=? AND u.id=f.id2) OR "+
                "(f.id2=? AND u.id=f.id1)) "+
                "ORDER BY u.id LIMIT ? OFFSET ?";
        String countSql="SELECT COUNT(*) FROM friendships "+
                "WHERE id1=? OR id2=?";
        try(Connection connection=DriverManager.getConnection(url,username,password)){
            long total=0;
            //Count friends even when the requested page is empty.
            try(PreparedStatement statement=connection.prepareStatement(countSql)){
                statement.setLong(1,userId);
                statement.setLong(2,userId);
                try (ResultSet resultSet=statement.executeQuery()){
                    if(resultSet.next()){
                        total=resultSet.getLong(1);
                    }
                }
            }
            try(PreparedStatement statement=connection.prepareStatement(sql)){
                statement.setLong(1,userId);
                statement.setLong(2,userId);
                statement.setInt(3,pageable.getPageSize());
                long offset=(long) pageable.getPageNumber()*pageable.getPageSize();
                statement.setLong(4,offset);
                try(ResultSet resultSet=statement.executeQuery()){
                    while(resultSet.next()){
                        friends.add(new User(resultSet.getLong("id"),resultSet.getString("first_name"),resultSet.getString("last_name")));
                    }
                }
            }
            return new Page<>(friends,total);
        }catch(SQLException e){
            throw new RepositoryException("Error retrieving friends page.", e);
        }
    }
}
