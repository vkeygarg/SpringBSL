package com.brcm.poc.repositroy;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.brcm.poc.entity.User;



public interface UserRepository extends MongoRepository<User, String> {

	@Query("{ 'uname': ?0}")
	@Cacheable
	public Optional<User> findByUserName(String uname);
	
	@Query(value = "{ $and: [ { 'uname' : ?0 }, { 'pass' : ?0 } ] }")
	@Cacheable
	public Optional<User> findByUserNamePass(String uname, String pass);

}
