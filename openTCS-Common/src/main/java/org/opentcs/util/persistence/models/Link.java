package org.opentcs.util.persistence.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class Link {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "link_generator")
	@SequenceGenerator(name = "link_generator", sequenceName = "link_id_seq")
	Integer id;

	String point;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(final String point) {
		this.point = point;
	}

}
