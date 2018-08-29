package org.neo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Document(collection = "user")
public class User {
    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    private String username;

    private Integer age;

    private String nickname;

    private Date birth;

    private List<Ability> ablities;

    public class Ability{
        String item;
        Integer value;

        public Ability(String item, Integer value) {
            this.item = item;
            this.value = value;
        }
    }

    public User(){}

    @PersistenceConstructor
    public User(String id, @NotNull String username, Integer age, String nickname) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public List<Ability> getAblities() {
        return ablities;
    }

    public void setAblities(List<Ability> ablities) {
        this.ablities = ablities;
    }
}
