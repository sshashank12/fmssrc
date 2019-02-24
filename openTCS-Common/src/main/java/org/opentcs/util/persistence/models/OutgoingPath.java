package org.opentcs.util.persistence.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class OutgoingPath {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "outgoingpath_generator")
	@SequenceGenerator(name = "outgoingpath_generator", sequenceName = "outgoingpath_id_seq")
	Integer id;

	String name;

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

}
