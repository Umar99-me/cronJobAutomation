package com.ibm.cron.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.cron.model.CronModel;
import com.ibm.cron.repository.CronRepository;

@Service
public class CronService {
	
	@Autowired
	CronRepository cronRepo;

	public List<CronModel> getAllCrons() {
		List<CronModel> crons = new ArrayList<CronModel>();
		cronRepo.findAll().forEach(cron -> crons.add(cron));
		return crons;
	}
	
	public List<CronModel> getCronsByUser(String userID) {
		List<CronModel> crons = new ArrayList<CronModel>();
		cronRepo.findAll().forEach(cron -> {
			if(cron.getUserID().equals(userID)) 
				crons.add(cron);	
		});
		return crons;
		
	}
	
	public CronModel getCron(int id) {
		return cronRepo.findById(id).get(); 
	}
	
	public String getFileNameByID(int id) {
		Optional<CronModel> cron = cronRepo.findById(id);
		if (cron.isPresent()) {
			return cron.get().getCronID()+"-"+cron.get().getServerID();
		}
		return null;
	}
	
	public void SaveOrUpdate(CronModel cron) {
		cronRepo.save(cron);
	}
	
	public void delete(int id) {
		cronRepo.deleteById(id);
	}

}

