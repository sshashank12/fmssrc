package org.opentcs.util.persistence.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "location_generator")
	@SequenceGenerator(name = "location_generator", sequenceName = "location_id_seq")
	Integer id;

	String name;

	String xPosition;

	String yPosition;

	String zPosition;

	String type;

	@OneToMany
	List<Link> links;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getxPosition() {
		return xPosition;
	}

	public void setxPosition(final String xPosition) {
		this.xPosition = xPosition;
	}

	public String getyPosition() {
		return yPosition;
	}

	public void setyPosition(final String yPosition) {
		this.yPosition = yPosition;
	}

	public String getzPosition() {
		return zPosition;
	}

	public void setzPosition(final String zPosition) {
		this.zPosition = zPosition;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(final List<Link> links) {
		this.links = links;
	}

}
