package org.opentcs.util.persistence.models;

import java.util.ArrayList;
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
public class Model {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "model_generator")
	@SequenceGenerator(name = "model_generator", sequenceName = "model_id_seq")
	Integer id;

	String version;

	String name;

	@OneToMany
	List<Point> points = new ArrayList<>();

	@OneToMany
	List<Path> paths = new ArrayList<>();

	@OneToMany
	List<Vehicle> vehicles = new ArrayList<>();

	@OneToMany
	List<LocationType> locationTypes = new ArrayList<>();

	@OneToMany
	List<Location> locations = new ArrayList<>();

	@OneToMany
	List<Block> blocks = new ArrayList<>();

	@OneToMany
	List<VisualLayout> visualLayouts = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(final List<Path> paths) {
		this.paths = paths;
	}

	public List<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(final List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	public List<LocationType> getLocationTypes() {
		return locationTypes;
	}

	public void setLocationTypes(final List<LocationType> locationTypes) {
		this.locationTypes = locationTypes;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(final List<Location> locations) {
		this.locations = locations;
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(final List<Block> blocks) {
		this.blocks = blocks;
	}

	public List<VisualLayout> getVisualLayouts() {
		return visualLayouts;
	}

	public void setVisualLayouts(final List<VisualLayout> visualLayouts) {
		this.visualLayouts = visualLayouts;
	}

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(final List<Point> points) {
		this.points = points;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
