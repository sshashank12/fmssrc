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
public class VisualLayout {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "visual_layout_generator")
	@SequenceGenerator(name = "visual_layout_generator", sequenceName = "visuallayout_id_seq")
	Integer id;

	@OneToMany
	List<ModelLayoutElement> modelLayoutElements;

	@OneToMany
	List<Property> properties;

	String name;

	String scaleX;

	String scaleY;

	public List<ModelLayoutElement> getModelLayoutElements() {
		return modelLayoutElements;
	}

	public void setModelLayoutElements(final List<ModelLayoutElement> modelLayoutElements) {
		this.modelLayoutElements = modelLayoutElements;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getScaleX() {
		return scaleX;
	}

	public void setScaleX(final String scaleX) {
		this.scaleX = scaleX;
	}

	public String getScaleY() {
		return scaleY;
	}

	public void setScaleY(final String scaleY) {
		this.scaleY = scaleY;
	}

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

}
