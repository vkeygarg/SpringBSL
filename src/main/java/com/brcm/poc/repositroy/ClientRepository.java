package com.brcm.poc.repositroy;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.brcm.poc.entity.Client;



public interface ClientRepository extends MongoRepository<Client, String> {


	@Query("{ 'uname': ?0}")
	public List<Client> findByUname(String uname);

	
	@Query("{'switchNum': ?0}")
	public Client findBySwitchNum(String switchNum);	
}
