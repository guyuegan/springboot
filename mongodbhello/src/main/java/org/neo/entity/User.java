package org.neo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "user")
public class User {
    @Id
    private String id;

    @NotNull
    @Indexed(unique = true)
    private String username;

    private Integer age;

    private String nickname;

    public User(){}

    @PersistenceConstructor
    public User(String id, @NotNull String username, Integer age, String nickname) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.nickname = nickname;
    }
}
