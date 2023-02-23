package com.capstone.pick.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Hashtag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id; // 해시태그 id

    @Column(length = 50)
    private String content; // 해시태그 본문

    protected Hashtag() {}

    private Hashtag(String content) {
        this.content = content;
    }

    public static Hashtag of(String content) {
        return new Hashtag(content);
    }
}
