package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.repository.database.FriendRequestDb;
import socialnetwork.repository.database.FriendshipDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observable;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 *  friend request service
 */
public class  FriendRequestService extends Observable {

    private final UserDb userRepository;
    private final FriendshipDb friendshipRepository;
    private final FriendRequestDb friendRequestRepository;

    /**
     *  constructor
     * @param userRepository - user repository
     * @param friendshipRepository - friendship repository
     * @param friendRequestRepository - friend request repository
     */
    public FriendRequestService(UserDb userRepository, FriendshipDb friendshipRepository, FriendRequestDb friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    /**
     *  sends a friend request to an user
     * @param id1 - id of who sent the friend request
     * @param id2 - id of who receives the friend request
     * @throws NoSuchElementException - if there is no user with id1/id2
     * @throws KeyAlreadyExistsException - if a friendship between id1 and id2 already exists
     *                                   - if there is a pending friend request from id2 to id1
     * @throws RepoException - if the friend request already exists
     */
    public void sendFriendRequest(Long id1, Long id2) throws NoSuchElementException, KeyAlreadyExistsException, RepoException {
        User leftUser = userRepository.findOne(id1);
        User rightUser = userRepository.findOne(id2);
        if (leftUser == null || rightUser == null)
            throw new NoSuchElementException("No user with this ID found!");
        if (friendshipRepository.findOne(new Tuple<>(id1, id2)) != null)
            throw new KeyAlreadyExistsException("Friendship already exists!");
        if (friendRequestRepository.findOne(new Tuple<>(id2, id1)) != null)
            throw new KeyAlreadyExistsException("There is already a friend request pending!");
        friendRequestRepository.save(new FriendRequest(id1, id2));
        notifyObserver(Tables.SENTREQUESTS);
    }

    /**
     *  deletes a friend request
     * @param id1 - id of who sent the friend request
     * @param id2 - id of who receives the friend request
     * @throws NoSuchElementException - if there is no user with id1/id2
     *                                - if there is no friend request pending
     * @throws RepoException - if a database error occurs
     */
    public void deleteFriendRequest(Long id1, Long id2) throws NoSuchElementException, RepoException {
        User leftUser = userRepository.findOne(id1);
        User rightUser = userRepository.findOne(id2);
        if (leftUser == null || rightUser == null)
            throw new NoSuchElementException("No user with this ID found!");
        if (friendRequestRepository.findOne(new Tuple<>(id1, id2)) == null)
            throw new NoSuchElementException("No friend request found!");
        friendRequestRepository.delete(new Tuple<>(id1, id2));
        notifyObserver(Tables.SENTREQUESTS);
        notifyObserver(Tables.RECEIVEDREQUESTS);
    }

    /**
     * get friend requests received from an user
     * @param ID - user id
     * @return filtered friend requests
     * @throws NoSuchElementException - if there is no user with id
     * @throws RepoException - if a database error occurs
     */
    public List<FriendDTO> receivedFriendRequests(long ID) throws NoSuchElementException, RepoException {
        User user = userRepository.findOne(ID);
        if (user == null)
            throw new NoSuchElementException("No user with this ID found!");
        return friendRequestRepository.findAll().values().stream()
                .filter(friendRequest -> friendRequest.getId().getRight().equals(ID))
                .map(friendRequest -> {
                    User user1 = userRepository.findOne(friendRequest.getId().getLeft());
                    return new FriendDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), friendRequest.getDate().toLocalDate());
                })
                .collect(Collectors.toList());
    }

    /**
     * get friend requests received from an user
     * @param ID - user id
     * @return filtered friend requests
     * @throws NoSuchElementException - if there is no user with id
     * @throws RepoException - if a database error occurs
     */
    public List<FriendDTO> sentFriendRequests(long ID) throws NoSuchElementException, RepoException {
        User user = userRepository.findOne(ID);
        if (user == null)
            throw new NoSuchElementException("No user with this ID found!");
        return friendRequestRepository.findAll().values().stream()
                .filter(friendRequest -> friendRequest.getId().getLeft().equals(ID))
                .map(friendRequest -> {
                    User user1 = userRepository.findOne(friendRequest.getId().getRight());
                    return new FriendDTO(user1.getId(), user1.getFirstName(), user1.getLastName(), friendRequest.getDate().toLocalDate());
                })
                .collect(Collectors.toList());
    }
}
