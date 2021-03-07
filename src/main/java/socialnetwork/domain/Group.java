package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;

public class Group extends Entity<Long>{

    private String name;
    private List<User> users = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public Group(Long id, String name) {
        this.setId(id);
        this.name = name;
    }

    public Group(Long id, String name, List<User> users) {
        this.setId(id);
        this.name = name;
        this.users = users;
    }

    public Group(List<User> users) {
        this.users = users;
        this.name = getGeneratedName(users);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getGeneratedName(List<User> users){
        String name = users.stream().map(User::getLastName).reduce("", (partialString, element) -> partialString +  element +", ");
        name = name.substring(0, name.length() - 2);
        if (name.length() > 23) {
            name = name.substring(0, 23);
            name += "..";
        }
        return name;
    }


    public List<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return name;
    }
}
