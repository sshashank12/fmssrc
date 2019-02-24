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
public class ModelLayoutElement {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "modellayoutelement_generator")
	@SequenceGenerator(name = "modellayoutelement_generator", sequenceName = "modellayoutelement_id_seq")
	Integer id;

	@OneToMany
	List<Property> properties;

	String visualizedObjectName;

	String layer;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(final List<Property> properties) {
		this.properties = properties;
	}

	public String getVisualizedObjectName() {
		return visualizedObjectName;
	}

	public void setVisualizedObjectName(final String visualizedObjectName) {
		this.visualizedObjectName = visualizedObjectName;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(final String layer) {
		this.layer = layer;
	}
}
