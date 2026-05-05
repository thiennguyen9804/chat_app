package com.example.api_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Data
@NoArgsConstructor
@DynamoDbBean
public class AuthUser implements UserDetails {
    private Integer id;

    private String username;

    private String password;

    @DynamoDbPartitionKey()
    @DynamoDbAttribute("UserId")
    public Integer getId() {
        return id;
    }

    @DynamoDbAttribute("Password") // Map với cột Password trong DB
    public String getPassword() {
        return password;
    }

    @DynamoDbAttribute("Username") // Map với cột Username trong DB
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();    
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
