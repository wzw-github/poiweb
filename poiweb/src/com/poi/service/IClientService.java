package com.poi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poi.dao.ClientDao;
import com.poi.entity.Client;

@Service
public class IClientService implements ClientService {
	
	@Autowired
	private ClientDao clientDao;

	@Override
	public boolean saveClient(Client client) {
		// TODO Auto-generated method stub
		if(clientDao.saveClient(client)>0)
			return true;
		else
			return false;
	}

	@Override
	public List<Client> findAll() {
		// TODO Auto-generated method stub
		return clientDao.findAll();
	}
	
}
