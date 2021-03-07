package socialnetwork.service;

import socialnetwork.domain.Group;
import socialnetwork.domain.User;
import socialnetwork.repository.database.GroupDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *  group service
 */
public class GroupService extends Observable {

    private final UserDb userRepository;
    private final GroupDb groupRepository;

    /**
     *  constructor
     * @param userRepository - user repository
     * @param groupRepository - group repository
     */
    public GroupService(UserDb userRepository, GroupDb groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    /**
     *  adds a group
     * @param users - users of the group
     * @throws NoSuchElementException - if no user was found
     * @throws RepoException - if a database error occurs
     */
    public void addGroup(List<User> users) throws NoSuchElementException, RepoException {
        HashMap<Long, User> usersMap = userRepository.findAll();
        users.forEach(user -> {
            if (!usersMap.containsKey(user.getId()))
                throw new NoSuchElementException("No user found!");
        });
        groupRepository.save(new Group(users));
        notifyObserver(Tables.GROUPS);
    }

    /**
     *  updates a group
     * @param id - id of the group
     * @param name - new name of the group
     * @param users - new users list of the group
     * @throws NoSuchElementException - if no user was found
     * @throws RepoException - if a database error occurs
     */
    public void updateGroup(Long id, String name, List<User> users) throws NoSuchElementException, RepoException {
        HashMap<Long, User> usersMap = userRepository.findAll();
        users.forEach(user -> {
            if (!usersMap.containsKey(user.getId()))
                throw new NoSuchElementException("No user found!");
        });
        groupRepository.update(new Group(id, name, users));
        notifyObserver(Tables.GROUPS);
    }

    /**
     *  gets all groups for an user
     * @param id - id of the user
     * @return all groups for the user with the given id
     * @throws NoSuchElementException - if no user was found
     * @throws RepoException - if a database error occurs
     */
    public HashMap<Long, Group> findAllGroupsForUser(Long id) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id) == null)
            throw new NoSuchElementException("No user found!");
        return groupRepository.findAllGroupsForUser(id);
    }

    /**
     *  gets the common groups for two users
     * @param firstId - id of the first user
     * @param secondId - id of the second user
     * @return common groups for the users with the given ids
     * @throws RepoException - if a database error occurs
     */
    public List<Group> findAllGroupForTwoUsers(Long firstId, Long secondId) throws RepoException {
        HashMap<Long, Group> firstUserGroups = findAllGroupsForUser(firstId);
        HashMap<Long, Group> secondUserGroups = findAllGroupsForUser(secondId);
        HashMap<Long, Long> nrAparitii = new HashMap<>();
        List<Group> commonGroups = new ArrayList<>();
        firstUserGroups.forEach(
                (k, v) -> {
                    if (v.getUsers().size() == 2)
                        nrAparitii.put(k, 1L);
                }
        );
        secondUserGroups.forEach(
                (k, v) -> {
                    if (nrAparitii.containsKey(k))
                        commonGroups.add(v);
                }
        );
        return commonGroups;
    }
}
