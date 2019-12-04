package com.poi.dao;

import java.util.List;

import com.poi.entity.Client;

public interface ClientDao {
	int saveClient(Client client);
	
	List<Client> findAll();
}
