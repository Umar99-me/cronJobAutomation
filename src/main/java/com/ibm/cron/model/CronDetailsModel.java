package com.ibm.cron.model;

public class CronDetailsModel {
	
	private String CronName;
    private String description;
    private String parameters;
    
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getCronName() {
		return CronName;
	}
	public void setCronName(String cronName) {
		CronName = cronName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
