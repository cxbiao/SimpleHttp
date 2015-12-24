package com.bryan.httpdemo.model;

import java.io.Serializable;

public class Course implements Serializable{
	private  int id;
	private int timelength;
	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTimelength() {
		return timelength;
	}

	public void setTimelength(int timelength) {
		this.timelength = timelength;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Course{" +
				"id=" + id +
				", timelength=" + timelength +
				", title='" + title + '\'' +
				'}';
	}
}
