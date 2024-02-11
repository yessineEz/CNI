package com.example.stageback.entities;

import com.example.stageback.enumerations.TokenType;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;
}
