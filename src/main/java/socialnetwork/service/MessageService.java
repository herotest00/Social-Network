package socialnetwork.service;

import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.repository.database.GroupDb;
import socialnetwork.repository.database.MessageDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 *  message service
 */
public class MessageService extends Observable {

    private final UserDb userRepository;
    private final MessageDb messageRepository;
    private final GroupDb groupRepository;

    /**
     *  constructor
     * @param userRepository - user repository
     * @param messageRepository - message repository
     * @param groupRepository - group repository
     */
    public MessageService(UserDb userRepository, MessageDb messageRepository, GroupDb groupRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
    }

    /**
     *  sends a message
     * @param from_id - id of the user who sends the message
     * @param group_id - id of the group the message is sent in
     * @param string - the message
     * @throws NoSuchElementException - if there is no user with the given ID
     *                                - if there is no group with the given ID
     * @throws RepoException - if a database error occurs
     */
    public void sendMessage(Long from_id, Long group_id, String string, Message reply) throws NoSuchElementException, RepoException {
        User from_user = userRepository.findOne(from_id);
        Group to_group = groupRepository.findOne(group_id);
        if (from_user == null)
            throw new NoSuchElementException("No user found!");
        if (to_group == null)
            throw new NoSuchElementException("No group found!");
        messageRepository.save(new Message(from_user, to_group, string, reply));
        notifyObserver(Tables.MESSAGES);
    }

    /**
     *  gets the messages from a group
     * @param id - the id of the group
     * @return all the messages from a group
     * @throws NoSuchElementException - if there is no group with the given ID
     * @throws RepoException - if a database error occurs
     */
    public List<Message> findAllMessagesForGroup(Long id) throws NoSuchElementException, RepoException {
        if (groupRepository.findOne(id) == null)
            throw new NoSuchElementException("No group found!");
        return messageRepository.findAllMessagesForGroup(id).values().stream().sorted(Comparator.comparing(Message::getDate)).collect(Collectors.toList());
    }

    /**
     *  gets all the messages sent in a group from a given period
     * @param id - id of the group
     * @param startAt - start time
     * @param endAt - end time
     * @return all the messages sent in a group from a given period
     * @throws RepoException - if a database error occurs
     */
    public List<Message> findAllMessagesForGroupFromPeriod(Long id, LocalDateTime startAt, LocalDateTime endAt) throws RepoException {
        return findAllMessagesForGroup(id).stream()
                .filter(m -> startAt.compareTo(m.getDate()) <= 0 && m.getDate().compareTo(endAt) <= 0)
                .collect(Collectors.toList());
    }
}
