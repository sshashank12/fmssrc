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
public class LocationType {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "locationtype_generator")
	@SequenceGenerator(name = "locationtype_generator", sequenceName = "locationtype_id_seq")
	Integer id;

	String name;

	@OneToMany
	List<AllowedOperation> allowedOperations;

	@OneToMany
	List<Property> properties;

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

	public List<AllowedOperation> getAllowedOperations() {
		return allowedOperations;
	}

	public void setAllowedOperations(final List<AllowedOperation> allowedOperations) {
		this.allowedOperations = allowedOperations;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(final List<Property> properties) {
		this.properties = properties;
	}

}
