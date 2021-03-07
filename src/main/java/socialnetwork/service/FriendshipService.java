package socialnetwork.service;

import socialnetwork.domain.FriendDTO;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.repository.database.FriendshipDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 *  friendship service
 */
public class FriendshipService extends Observable {

    private final UserDb userRepository;
    private final FriendshipDb friendshipRepository;

    /**
     *  constructor
     * @param userRepository - user repository
     * @param friendshipRepository - friendship repository
     */
    public FriendshipService(UserDb userRepository, FriendshipDb friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    /**
     *  saves the friendship
     * @param id1 - id of the first user
     * @param id2 - id of the second user
     * @throws NoSuchElementException - if there are no users having the IDs of the friendship
     * @throws RepoException - if the friendship already exists
     */
    public void addFriendship(Long id1, Long id2) throws NoSuchElementException, RepoException {
        User leftUser = userRepository.findOne(id1);
        User rightUser = userRepository.findOne(id2);
        if (leftUser == null || rightUser == null)
            throw new NoSuchElementException("No user with this ID found!");
        friendshipRepository.save(new Friendship(id1, id2));
        notifyObserver(Tables.FRIENDS);
    }

    /**
     *  removes the friendship
     * @param id1 - id of the first user
     * @param id2 - id of the second user
     * @throws NoSuchElementException - if there are no users having the IDs of the friendship
     * @throws RepoException - if a database error occurs
     */
    public void removeFriendship(Long id1, Long id2) throws NoSuchElementException, RepoException {
        User leftUser = userRepository.findOne(id1);
        User rightUser = userRepository.findOne(id2);
        if (leftUser == null || rightUser == null)
            throw new NoSuchElementException("No user with this ID found!");
        friendshipRepository.delete(new Tuple<>(id1, id2));
        notifyObserver(Tables.FRIENDS);
    }

    /**
     *  filter friendships by ID
     * @param ID - user id
     * @return filtered friends
     * @throws NoSuchElementException - if there is no user with id
     * @throws RepoException - if a database error occurs
     */
    public List<FriendDTO> filterFriendshipsByID (long ID) throws NoSuchElementException, RepoException {
        User user = userRepository.findOneWithFriends(ID);
        if (user == null)
            throw new NoSuchElementException("No user with this ID found!");
        return user.getFriends().stream()
                .map(user1 -> {
                    Friendship friendship = friendshipRepository.findOne(new Tuple<>(user.getId(), user1.getId()));
                    return new FriendDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), friendship.getDate());
                })
                .collect(Collectors.toList());
    }

    /**
     *
     * @param id - user id
     * @param startAt - period from when to start searching
     * @param endAt - period till when to start searching
     * @return filtered friendships
     * @throws RepoException - if a database error occurs
     */
    public List<FriendDTO> filterFriendshipsByPeriod(long id, LocalDate startAt, LocalDate endAt) throws NoSuchElementException, RepoException {
        User user = userRepository.findOneWithFriends(id);
        if (user == null)
            throw new NoSuchElementException("No user with this ID found!");
        return user.getFriends().stream()
                .map(user1 -> {
                    Friendship friendship = friendshipRepository.findOne(new Tuple<>(user.getId(), user1.getId()));
                    return new FriendDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), friendship.getDate());
                }).filter(f -> startAt.compareTo(f.getDate()) <= 0 && f.getDate().compareTo(endAt) <= 0)
                .collect(Collectors.toList());

    }

    /**
     *  filter friendships by ID and month
     * @param ID - user id
     * @param month - month
     * @return filtered friends
     * @throws NoSuchElementException - if there is no user with id
     * @throws RepoException - if a database error occurs
     */
    public List<FriendDTO> filterFriendshipsByMonth (long ID, long month) throws NoSuchElementException, RepoException {
        User user = userRepository.findOneWithFriends(ID);
        if (user == null)
            throw new NoSuchElementException("No user with this ID found!");
        return user.getFriends().stream()
                .filter(user1 -> friendshipRepository.findOne(new Tuple<>(user.getId(), user1.getId())).getDate().getMonthValue() == month)
                .map(user1 -> {
                    Friendship friendship = friendshipRepository.findOne(new Tuple<>(user.getId(), user1.getId()));
                    return new FriendDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), friendship.getDate());
                })
                .collect(Collectors.toList());
    }

    /**
     *  classic BFS
     * @param visited - map<User, Boolean>
     * @param user - user of type User
     */
    private void BreadthFirstSearch(Map<User, Boolean> visited, User user) {
        Queue<User> queue = new LinkedList<>();
        queue.add(user);
        while (!queue.isEmpty()) {
            User user1 = queue.remove();
            user1.getFriends().forEach(user2 -> {
                if (!visited.get(user2)) {
                    visited.put(user2, true);
                    queue.add(user2);
                }
            });
        }
    }

    /**
     *  determines the number of communities
     * @return the number of communities
     * @throws RepoException - if a database error occurs
     */
    public int numberOfCommunities() throws RepoException {
        Map<User, Boolean> visited = new HashMap<>();
        int counter = 0;
        userRepository.findAllWithFriends().values().forEach(user -> visited.put(user, false));
        for (User user : visited.keySet()){
            if (!visited.get(user)) {
                BreadthFirstSearch(visited, user);
                counter++;
            }
        }
        return counter;
    }

    /**
     *  determines the longest path of a community
     * @param visited - map<User, Boolean>
     * @param localVisited - map<User, Boolean> a local map that helps to traverse the nodes i've already traversed on the other paths
     * @param user - user of type User
     * @param len -  length of the path
     * @return a tuple made of an Array which contains every user that is part of the longest path and the length of the path
     */
    private Tuple<ArrayList<User>, Integer> DepthFirstSearch(Map<User, Boolean> visited, Map<User, Boolean> localVisited, User user, int len) {
        visited.put(user, true);
        localVisited.put(user, true);
        int maxLen = len;
        ArrayList<User> maxArray = new ArrayList<>();
        User maxUser = null;
        for (User user1 : user.getFriends())
            if (!localVisited.get(user1)) {
                Tuple<ArrayList<User>, Integer> pair = DepthFirstSearch(visited, new HashMap<>(localVisited), user1, len + 1);
                if (pair.getRight() > maxLen) {
                    maxLen = pair.getRight();
                    maxArray = pair.getLeft();
                    maxUser = user1;
                }
            }
        if (maxUser != null)
            maxArray.add(maxUser);
        return new Tuple<>(maxArray, maxLen);
    }

    /**
     *  determines the community with the longest path and the length of it
     * @return a tuple made of an Array which contains every user that is part of the longest path and the length of the path
     * @throws RepoException - if a database error occurs
     */
    public Tuple<ArrayList<User>, Integer> mostActiveCommunity() throws RepoException {
        Map<User, Boolean> visited = new HashMap<>(userRepository.getSize());
        userRepository.findAllWithFriends().values().forEach(user -> visited.put(user, false));
        Tuple<ArrayList<User>, Integer> maxPair = new Tuple<>(new ArrayList<>(), 0), pair;
        for (User user : visited.keySet()) {
            if (user.getFriends().size() % 2 == 1) {
                if (!visited.get(user)) {
                    pair = DepthFirstSearch(visited, new HashMap<>(visited), user, 0);
                    if (pair.getRight() > maxPair.getRight()) {
                        maxPair = pair;
                        maxPair.getLeft().add(user);
                    }
                }
            }
        }
        for (User user : visited.keySet()) {
            if (!visited.get(user)) {
                pair = DepthFirstSearch(visited, new HashMap<>(visited), user, 0);
                if (pair.getRight() > maxPair.getRight()) {
                    maxPair = pair;
                    maxPair.getLeft().add(user);
                }
            }
        }
        return maxPair;
    }
}
