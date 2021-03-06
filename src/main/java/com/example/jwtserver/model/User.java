package com.example.jwtserver.model;

import com.example.jwtserver.dto.SignupRequestDto;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="uid")
    private Long uid;

    @Column(nullable = false)
    private String username; //변수명 바꾸면 안됨

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    public User(SignupRequestDto userRequestDto) {
        this.username = userRequestDto.getUsername();
        this.password = userRequestDto.getPassword();
        this.email = userRequestDto.getEmail();
        this.name = userRequestDto.getName();
        this.address = userRequestDto.getAddress();
    }

}
