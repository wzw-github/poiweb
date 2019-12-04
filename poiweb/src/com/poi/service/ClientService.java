package com.poi.service;

import java.util.List;

import com.poi.entity.Client;

public interface ClientService {
	
	boolean saveClient(Client client);
	
	List<Client> findAll();
	
}
