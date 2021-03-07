package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.repository.database.EventDb;
import socialnetwork.repository.database.UserDb;
import socialnetwork.repository.exceptions.RepoException;
import socialnetwork.ui.gui.utils.enums.Tables;
import socialnetwork.ui.gui.utils.observer.Observable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/**
 *  event service
 */
public class EventService extends Observable {

    private final UserDb userRepository;
    private final EventDb eventRepository;

    /**
     *  constructor
     * @param userRepository - user repository
     * @param eventRepository - event repository
     */
    public EventService(UserDb userRepository, EventDb eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     *  adds an event
     * @param user - user who creates the event
     * @param title - title of the event
     * @param description - description of the event
     * @param start_time - when the event begins
     * @param end_time - when the event ends
     * @throws NoSuchElementException - if no user was found
     * @throws RepoException - if a database error occurs
     */
    public void addEvent(User user, String title, String description, LocalDateTime start_time, LocalDateTime end_time) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(user.getId()) == null)
            throw new NoSuchElementException("User not found!");
        eventRepository.save(new Event(user, title, description, start_time, end_time));
        notifyObserver(Tables.ALLEVENTS);
        notifyObserver(Tables.SUBSCRIBEDEVENTS);
    }

    /**
     *  deletes an event
     * @param id - id of the event
     * @throws NoSuchElementException - if no event was found
     * @throws RepoException - if a database error occurs
     */
    public void deleteEvent(long id) throws NoSuchElementException, RepoException {
        Event event = eventRepository.findOne(id);
        if (event == null)
            throw new NoSuchElementException("Event not found!");
        eventRepository.delete(id);
        notifyObserver(Tables.ALLEVENTS);
        notifyObserver(Tables.SUBSCRIBEDEVENTS);
    }

    /**
     *  updates an event
     * @param id - id of the event
     * @param title - new title
     * @param description - new description
     * @throws NoSuchElementException - if no event was found
     * @throws RepoException - if a database error occurs
     */
    public void updateEvent(long id, String title, String description) throws NoSuchElementException, RepoException {
        Event event = eventRepository.findOne(id);
        if (event == null)
            throw new NoSuchElementException("Event not found!");
        eventRepository.update(new Event(id, title, description));
        notifyObserver(Tables.ALLEVENTS);
        notifyObserver(Tables.SUBSCRIBEDEVENTS);
    }

    /**
     *  saves an user in an event
     * @param id_user - id of the user
     * @param id_event - id of the event
     * @throws NoSuchElementException - if no user was found
     *                                  if no event was found
     * @throws RepoException - if the user is already attending to the event
     */
    public void saveUserInEvent(long id_user, long id_event) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id_user) == null)
            throw new NoSuchElementException("User not found!");
        if (eventRepository.findOne(id_event) == null)
            throw new NoSuchElementException("Event not found!");
        eventRepository.saveUserInEvent(id_user, id_event);
        notifyObserver(Tables.SUBSCRIBEDEVENTS);
    }

    /**
     *  removes an user from an event
     * @param id_user - id of the user
     * @param id_event - id of the event
     * @throws NoSuchElementException - if no user was found
     *                                  if no event was found
     * @throws RepoException - if a database error occurs
     */
    public void deleteUserFromEvent(long id_user, long id_event) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id_user) == null)
            throw new NoSuchElementException("User not found!");
        if (eventRepository.findOne(id_event) == null)
            throw new NoSuchElementException("Event not found!");
        eventRepository.deleteUserFromEvent(id_user, id_event);
        notifyObserver(Tables.SUBSCRIBEDEVENTS);
    }

    /**
     *  update the notify value for an user attending to an event
     * @param id_user - id of the user
     * @param id_event - id of the event
     * @param new_val - true -> if he wants to get notified
     *                  false -> otherwise
     * @throws NoSuchElementException - if no user was found
     *                                  if no event was found
     * @throws RepoException - if a database error occurs
     */
    public void updateNotifyForEvent(long id_user, long id_event, boolean new_val) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id_user) == null)
            throw new NoSuchElementException("User not found!");
        if (eventRepository.findOne(id_event) == null)
            throw new NoSuchElementException("Event not found!");
        eventRepository.updateNotifyForEvent(id_user, id_event, new_val);
    }

    /**
     *  finds all users attending to an event
     * @param id - id of the event
     * @return users attending to the event with the specified id
     * @throws NoSuchElementException - if no event was found
     * @throws RepoException - if a database error occurs
     */
    public HashMap<Long, User> findAllForEvent(long id) throws NoSuchElementException, RepoException {
        if (eventRepository.findOne(id) == null)
            throw new NoSuchElementException("Event not found!");
        return eventRepository.findAllForEvent(id);
    }

    /**
     *  finds all events that an user is attending to
     * @param id - id of the user
     * @return events
     * @throws NoSuchElementException - if no user was found
     * @throws RepoException - if a database error occurs
     */
    public HashMap<Long, Event> findAllForUser(long id) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id) == null)
            throw new NoSuchElementException("User not found!");
        return eventRepository.findAllForUser(id);
    }

    public HashMap<Long, Event> findAllForUser(long id, long pageIndex) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id) == null)
            throw new NoSuchElementException("User not found!");
        return eventRepository.findAllForUser(id, pageIndex);
    }

    /**
     *  finds all events that an user has enabled notifications for
     * @param id - id of the user
     * @param difference - minimum difference of time between the actual time and the beginning of the event
     * @return all events that an user has enabled notifications for
     * @throws NoSuchElementException - if no user was found
     * @throws RepoException - if a database error occurs
     */
    public ArrayList<Event> findAllNotifiedForUser(long id, Duration difference) throws NoSuchElementException, RepoException {
        if (userRepository.findOne(id) == null)
            throw new NoSuchElementException("User not found!");
        HashMap<Long, Event> events = eventRepository.findAllForUser(id);
        return events.values().stream().filter(event -> event.isNotify() && event.getStart_time().isAfter(LocalDateTime.now()) && event.getStart_time().isBefore(LocalDateTime.now().plus(difference))).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     *  finds an event
     * @param id - id of the event
     * @return the event
     * @throws RepoException - if a database error occurs
     */
    public Event findOne(long id) throws RepoException {
        return eventRepository.findOne(id);
    }

    /**
     *  finds all events
     * @return all events
     * @throws RepoException - if a database error occurs
     */
    public HashMap<Long, Event> findAll() throws RepoException {
        return eventRepository.findAll();
    }

    /**
     *  finds all events for a page
     * @return all events
     * @throws RepoException - if a database error occurs
     */
    public HashMap<Long, Event> findAll(long pageIndex) throws RepoException {
        return eventRepository.findAll(pageIndex);
    }

    /**
     *  gets the number of pages for all events
     * @return the number of pages
     * @throws RepoException - if a database error occurs
     */
    public int getNoPages() {
        return eventRepository.getNoPages();
    }

    /**
     *  gets the number of pages for all events for a user
     * @return the number of pages
     * @throws RepoException - if a database error occurs
     */
    public int getNoPagesForUser(long id) {
        if (userRepository.findOne(id) == null)
            throw new NoSuchElementException("User not found!");
        return eventRepository.getNoPagesForUser(id);
    }
}
