package com.example.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.domain.User;

public interface UserRepository extends MongoRepository<User, String>{

}
