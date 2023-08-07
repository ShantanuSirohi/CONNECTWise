package com.smart.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="CONTACTS")
public class Contacts {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int c_id;
	
	private String name;
	
	private String nickName;
	
	private String work;
	
	private String email;
		
	private String number;
	
	private String image_url;

	@Column(length=500)
	private String description;
	
	@ManyToOne
	private User user=new User();
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Contacts() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Contacts(int c_id, String name, String nickName, String work, String email, String number, String image_url,
			String description) {
		super();
		this.c_id = c_id;
		this.name = name;
		this.nickName = nickName;
		this.work = work;
		this.email = email;
		this.number = number;
		this.image_url = image_url;
		this.description = description;
	}
	
	

	/*
	 * @Override public String toString() { return "Contacts [c_id=" + c_id +
	 * ", name=" + name + ", nickName=" + nickName + ", work=" + work + ", email=" +
	 * email + ", number=" + number + ", image_url=" + image_url + ", description="
	 * + description + "]"; }
	 */

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.c_id==((Contacts)obj).getC_id();
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
