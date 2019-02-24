package org.opentcs.util.persistence.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "vehicle_generator")
	@SequenceGenerator(name = "vehicle_generator", sequenceName = "vehicle_id_seq")
	Integer id;

	String name;

	String length;

	String energyLevelCritical;

	String energyLevelGood;

	String maxVelocity;

	String maxReverseVelocity;

	String type;

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

	public String getLength() {
		return length;
	}

	public void setLength(final String length) {
		this.length = length;
	}

	public String getEnergyLevelCritical() {
		return energyLevelCritical;
	}

	public void setEnergyLevelCritical(final String energyLevelCritical) {
		this.energyLevelCritical = energyLevelCritical;
	}

	public String getEnergyLevelGood() {
		return energyLevelGood;
	}

	public void setEnergyLevelGood(final String energyLevelGood) {
		this.energyLevelGood = energyLevelGood;
	}

	public String getMaxVelocity() {
		return maxVelocity;
	}

	public void setMaxVelocity(final String maxVelocity) {
		this.maxVelocity = maxVelocity;
	}

	public String getMaxReverseVelocity() {
		return maxReverseVelocity;
	}

	public void setMaxReverseVelocity(final String maxReverseVelocity) {
		this.maxReverseVelocity = maxReverseVelocity;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

}
