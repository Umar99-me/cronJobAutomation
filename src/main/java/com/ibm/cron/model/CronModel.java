package com.ibm.cron.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class CronModel {

	@GeneratedValue (strategy = GenerationType.IDENTITY)
	@Id
	@Column
	private int id;
	
	
	@Column
	private String userID;
	
	@Column
	private String CronID;
	
	@Column
	private String ServerID;
	
	@Column
	private String machineType;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserID() {
		return userID;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public String getCronID() {
		return CronID;
	}
	
	public void setCronID(String cronID) {
		CronID = cronID;
	}
	
	public String getServerID() {
		return ServerID;
	}
	
	public void setServerID(String serverID) {
		ServerID = serverID;
	}
	
	public String getMachineType() {
		return machineType;
	}
	
	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
}

