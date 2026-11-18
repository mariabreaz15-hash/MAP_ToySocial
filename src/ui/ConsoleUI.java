package ui;
import service.SocialNetworkService;
import java.util.Scanner;
import domain.User;
import util.paging.Page;

/** Minimal interactive console interface. */
public class ConsoleUI {
    private final SocialNetworkService service;
    private final Scanner scanner=new Scanner(System.in);
    public ConsoleUI(SocialNetworkService service){this.service=service;}
    private long readId(String label){
        System.out.print(label);
        return Long.parseLong(scanner.nextLine().trim());
    }

    /**
     * Displays database users with next and previous navigation.
     *
     * @param pageSize maximum number of users per page
     */
    private void showUsersPaginated(int pageSize) {
        int pageNumber = 0;
        while (true) {
            Page<User> page = service.getUsersPage(pageNumber, pageSize);
            long total = page.getTotalNumberOfElements();
            long totalPages = total / pageSize + (total % pageSize == 0 ? 0 : 1);
            System.out.println("\nUsers:");
            if (page.getElementsOnPage().isEmpty()) {
                System.out.println("No users on this page.");
            } else {
                for (User user : page.getElementsOnPage()) {
                    System.out.println(user.getId() + " - " + user.getFirstName() + " " + user.getLastName());
                }
            }
            System.out.println("Pagina " + (pageNumber + 1) + " din " + Math.max(1, totalPages));
            System.out.println("N - Next page");
            System.out.println("P - Previous page");
            System.out.println("Q - Back to menu");
            String option = scanner.nextLine().trim();
            if (option.equalsIgnoreCase("Q")) {
                return;
            }
            if (option.equalsIgnoreCase("N")) {
                if ((long) pageNumber + 1 < totalPages) {
                    pageNumber++;
                } else {
                    System.out.println("Already on last page.");
                }
            } else if (option.equalsIgnoreCase("P")) {
                if (pageNumber > 0) {
                    pageNumber--;
                } else {
                    System.out.println("Already on first page.");
                }
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    private void showFriendshipsPaginated(int pageSize) {
        int pageNumber = 0;
        System.out.print("Enter user ID: ");
        Long userId = Long.parseLong(scanner.nextLine());
        while (true) {
            Page<User> page = service.getFriendsPage(userId, pageNumber, pageSize);
            long total = page.getTotalNumberOfElements();
            long totalPages = total / pageSize + (total % pageSize == 0 ? 0 : 1);
            System.out.println("\nUsers:");
            if (page.getElementsOnPage().isEmpty()) {
                System.out.println("No users on this page.");
            } else {
                for (User user : page.getElementsOnPage()) {
                    System.out.println(user.getId() + " - " + user.getFirstName() + " " + user.getLastName());
                }
            }
            System.out.println("Pagina " + (pageNumber + 1) + " din " + Math.max(1, totalPages));
            System.out.println("N - Next page");
            System.out.println("P - Previous page");
            System.out.println("Q - Back to menu");
            String option = scanner.nextLine().trim();
            if (option.equalsIgnoreCase("Q")) {
                return;
            }
            if (option.equalsIgnoreCase("N")) {
                if ((long) pageNumber + 1 < totalPages) {
                    pageNumber++;
                } else {
                    System.out.println("Already on last page.");
                }
            } else if (option.equalsIgnoreCase("P")) {
                if (pageNumber > 0) {
                    pageNumber--;
                } else {
                    System.out.println("Already on first page.");
                }
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    public void run(){
        while(true){
            System.out.println("\n1.List users 2.Add user 3.Remove user 4.Update user");
            System.out.println("5.Add friendship 6.Remove friendship 7.List friendships");
            System.out.println("8.Number of communities 9.Most sociable community 10.User community 0.Exit");
            System.out.println("Choice: ");
            String choice=scanner.nextLine().trim();
            try{
                switch(choice){
                    case "1" :
                        service.getUsersSortedByLastName().forEach(System.out::println);
                        /*for(domain.User user:service.getUsers()){
                            System.out.println(user);
                        }*/
                        break;
                    case "2" :
                        System.out.print("First name: "); String first=scanner.nextLine();
                        System.out.print("Last name: "); String last=scanner.nextLine();
                        User savedUser=service.addUser(first,last);
                        System.out.println("User added. Generated ID: "+savedUser.getId());
                        break;
                    case "3" : service.removeUser(readId("ID: ")); System.out.println("User removed."); break;
                    case "4" :
                        long id=readId("ID: ");
                        System.out.print("New first name: ");String newFirst=scanner.nextLine();
                        System.out.print("New last name: "); String newLast=scanner.nextLine();
                        service.updateUser(id,newFirst,newLast);
                        System.out.println("User updated.");
                        break;
                    case "5" : service.addFriendship(readId("First ID: "),readId("Second ID: ")); System.out.println("Friendship added."); break;
                    case "6" : service.removeFriendship(readId("First ID: "),readId("Second ID: ")); System.out.println("Friendship removed."); break;
                    case "7" :
                        service.getFriendships().forEach(System.out::println);
                        /*for(domain.Friendship friendship:service.getFriendships()){
                            System.out.println(friendship);
                        }*/
                        break;
                    case "8" : System.out.println("Communities: "+service.numberOfCommunities()); break;
                    case "9" : System.out.println("Most sociable community (IDs): " + service.mostSociableCommunity()); break;
                    case "10" : System.out.println("Community (IDs): "+service.communityOf(readId("ID: "))); break;
                    case "0" : return;
                    default : System.out.println("Invalid option."); break;
                }
            }catch(NumberFormatException ex){
                System.out.println("Error: ID must be a valid number");
            }catch(exceptions.ValidationException ex){
                System.out.println("Validation error: "+ex.getMessage());
            }catch(exceptions.RepositoryException ex){
                System.out.println("Repository error: "+ex.getMessage());
            }catch(exceptions.ServiceException ex){
                System.out.println("Service error: "+ex.getMessage());
            }catch(IllegalArgumentException ex){
                System.out.println("Invalid input: "+ex.getMessage());
            }catch(UnsupportedOperationException ex){
                System.out.println("Operation unavailable: "+ex.getMessage());
            }
        }
    }
}
