package service;
import domain.Friendship;
import domain.User;
import exceptions.ServiceException;
import repository.Repository;
import java.util.*;
import domain.Tuple;
import java.util.stream.Collectors;
/**Application use cases and graph algorithms for the social network. */
public class SocialNetworkService {
    private final Repository<Long,User> users;
    private final Repository<Tuple<Long,Long>,Friendship> friendships;
    public SocialNetworkService(Repository<Long,User> users,Repository<Tuple<Long,Long>,Friendship> friendships){
        this.users=users;
        this.friendships=friendships;
    }

    /**
     * Adds a user with an automatically generated ID.
     *
     * @param firstName the first name
     * @param lastName the last name
     * @return the generated ID
     * @throws ServiceException if no more IDs are available
     */
    public long addUser(String firstName,String lastName){
        long maxId=users.findAll().stream().map(User::getId).max(Long::compareTo).orElse(0L);
        if(maxId==Long.MAX_VALUE){
            throw new ServiceException("No more user IDs available.");
        }
        long newId=maxId+1;
        User newUser=new User(newId,firstName,lastName);
        users.save(newUser);
        return newId;
    }
    public void updateUser(long id,String firstName,String lastName){
        users.update(new User(id,firstName,lastName));
    }
    public void removeUser(long id){
        checkUser(id);
        List<Tuple<Long,Long>> toDelete=friendships.findAll().stream().filter(friendship->friendship.getFirstUserId().equals(id)||friendship.getSecondUserId().equals(id)).map(Friendship::getId).collect(Collectors.toList());
        //Delete only after collecting the IDs.
        toDelete.forEach(friendships::delete);
        users.delete(id);
    }
    public Collection<User> getUsers(){return users.findAll();}
    public Collection<Friendship> getFriendships(){return friendships.findAll();}
    private User checkUser(long id){
        return findUser(id).orElseThrow(()->new ServiceException("User "+id+" does not exist."));
    }
    public void addFriendship(long a,long b){
        checkUser(a); checkUser(b);
        if(a==b) throw new ServiceException("Self friendship is not allowed.");
        Tuple<Long,Long> id=new Tuple<>(Math.min(a,b),Math.max(a,b));
        if(friendships.findOne(id).isPresent()){
            throw new ServiceException("Friendship already exists");
        }
        friendships.save(new Friendship(a,b));
    }
    public void removeFriendship(long a,long b){
        checkUser(a); checkUser(b);
        Tuple<Long,Long> id=new Tuple<>(Math.min(a,b),Math.max(a,b));
        if(friendships.findOne(id).isEmpty()){
            throw new ServiceException("Friendship does not exists.");
        }
        friendships.delete(id);
    }
    private Map<Long,Set<Long>> graph(){
        /*Map<Long, Set<Long>> graph=new LinkedHashMap<>();
        for(User user:users.findAll()) graph.put(user.getId(),new HashSet<>());
        for(Friendship friendship:friendships.findAll()){
            Long first=friendship.getFirstUserId();
            Long second=friendship.getSecondUserId();
            if(graph.containsKey(first)&&graph.containsKey(second)){
                graph.get(first).add(second);
                graph.get(second).add(first);
            }
        }
        return graph;*/
        return users.findAll().stream()
                .map(User::getId).collect(Collectors.toMap(
                        id->id,id->friendships.findAll()
                                .stream().filter(f->
                                        f.getFirstUserId().equals(id)||f.getSecondUserId().equals(id))
                                .map(f->f.getFirstUserId().equals(id)
                                ?f.getSecondUserId():f.getFirstUserId())
                                .collect(Collectors.toSet()),(first,second)->first,
                        LinkedHashMap::new
                ));
    }
    private List<Set<Long>> components(Map<Long,Set<Long>> graph){
        Set<Long> unassigned=new LinkedHashSet<>(graph.keySet());
        //Set<Long> visited=new HashSet<>();
        List<Set<Long>> result=new ArrayList<>();
        /*for(Long start:graph.keySet()){
            if(visited.contains(start)) continue;
            Set<Long> component=new LinkedHashSet<>();
            Deque<Long> queue=new ArrayDeque<>();
            queue.add(start);
            visited.add(start);
            while(!queue.isEmpty()){
                Long current=queue.remove();
                component.add(current);
                for(Long neighbour:graph.get(current))
                    if(visited.add(neighbour)) queue.add(neighbour);
            }
            result.add(component);
        }*/
        collectComponents(graph,unassigned,result);
        return result;
    }
    public int numberOfCommunities(){return Math.toIntExact(components(graph()).stream().count());}
    public Set<Long> communityOf(long id){
        checkUser(id);
        /*for(Set<Long> component:components(graph()))
            if(component.contains(id)) return Set.copyOf(component);
        throw new ServiceException("Community not found.");*/
        return components(graph()).stream()
                .filter(component->component.contains(id)).findFirst()
                .map(LinkedHashSet::new).orElseThrow(()->
                        new ServiceException("Community not found."));
    }
    private int eccentricity(long start,Map<Long,Set<Long>> graph){
        Map<Long,Integer> distance=new HashMap<>();
        //Deque<Long> queue=new ArrayDeque<>();
        //queue.add(start);
        distance.put(start,0);
        /*int maximum=0;
        while(!queue.isEmpty()){
            long current=queue.remove();
            for(long neighbour:graph.get(current)){
                if(!distance.containsKey(neighbour)){
                    int d=distance.get(current)+1;
                    distance.put(neighbour,d);
                    maximum=Math.max(maximum,d);
                    queue.add(neighbour);
                }
            }
        }
        return maximum;*/
        return distances(graph,distance,Collections.singleton(start)).values()
                .stream().mapToInt(Integer::intValue)
                .max().orElse(0);
    }
    /**
     * Returns the community with the largest graph diameter.
     *
     * @return the IDs in the most sociable community
     */
    public Set<Long> mostSociableCommunity(){
        Map<Long,Set<Long>> graph=graph();
        /*Set<Long> best=Set.of();
        int bestDiameter=-1;
        for(Set<Long> component:components(graph)){
            int diameter=0;
            for(long userId:component)
                diameter=Math.max(diameter,eccentricity(userId,graph));
            if(diameter>bestDiameter){
                bestDiameter=diameter;
                best=component;
            }
        }
        return Set.copyOf(best);*/
        return components(graph()).stream()
                .reduce((best,candidate)->diameter(candidate,graph)>diameter(best,graph)
                ?candidate:best)
                .map(LinkedHashSet::new).orElseGet(LinkedHashSet::new);
    }
    /**
     * Finds a user by ID.
     *
     * @param id the users ID
     * @return the user, if present
     */
    public Optional<User> findUser(long id){
        return users.findOne(id);
    }
    public List<User> getUsersSortedByLastName(){
        return users.findAll().stream().sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName)).collect(Collectors.toList());
    }
    public List<User> getUsersByCriteria(java.util.function.Predicate<User> filter){
        return users.findAll(filter);
    }
    public List<User> getFriendsOf(long id){
        checkUser(id);
        return friendships.findAll().stream().filter(friendship->friendship.getFirstUserId().equals(id)||friendship.getSecondUserId().equals(id)).map(friendship->friendship.getFirstUserId().equals(id)?friendship.getSecondUserId():friendship.getFirstUserId()).distinct().map(friendId->findUser(friendId).orElseThrow(()->new ServiceException("Friend not found: "+friendId))).sorted(Comparator.comparing(User::getLastName).thenComparing(User::getFirstName)).collect(Collectors.toList());
    }
    /**
     * Recursively explores a connected component.
     *
     * @param queue nodes waiting to be explored
     * @param visited already discovered nodes
     * @param graph the adjacency map
     * @return all nodes in the connected component
     */
    private Set<Long> explore(Deque<Long> queue, Set<Long> visited,Map<Long,Set<Long>> graph){
        if(queue.isEmpty()){
            return visited;
        }
        Long current=queue.removeFirst();
        Set<Long> newNeighbours=graph.get(current).stream()
                .filter(id->!visited.contains(id)).collect(Collectors.toCollection(LinkedHashSet::new));
        visited.addAll(newNeighbours);
        queue.addAll(newNeighbours);
        return explore(queue,visited,graph);
    }
    private Set<Long> connectedComponent(Long start, Map<Long,Set<Long>> graph){
        Set<Long> visited=new LinkedHashSet<>();
        Deque<Long> queue=new ArrayDeque<>();
        visited.add(start);
        queue.add(start);
        return explore(queue,visited,graph);
    }
    private void collectComponents(Map<Long,Set<Long>> graph,Set<Long> unassigned,List<Set<Long>> result){
        if(unassigned.isEmpty()){
            return;
        }
        Long start=unassigned.iterator().next();
        Set<Long> component=connectedComponent(start,graph);
        result.add(component);
        unassigned.removeAll(component);
        collectComponents(graph,unassigned,result);
    }
    private Map<Long,Integer> distances(Map<Long,Set<Long>> graph,Map<Long,Integer> distance,Set<Long> frontier){
        if(frontier.isEmpty()){
            return distance;
        }
        Set<Long> next=frontier.stream().flatMap(id->graph.get(id).stream())
                .filter(id->!distance.containsKey(id)).collect(Collectors.toSet());
        int nextDistance=frontier.stream().mapToInt(distance::get)
                .max().orElse(0)+1;
        next.forEach(id->distance.put(id,nextDistance));
        return distances(graph,distance,next);
    }
    private int diameter(Set<Long> component,Map<Long,Set<Long>> graph){
        return component.stream().mapToInt(id->eccentricity(id,graph))
                .max().orElse(0);
    }
}
