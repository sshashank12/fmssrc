package org.opentcs.util;

import java.util.ArrayList;
import java.util.List;

import org.opentcs.util.persistence.binding.LocationTypeTO;
import org.opentcs.util.persistence.binding.PlantModelTO;
import org.opentcs.util.persistence.binding.PointTO;
import org.opentcs.util.persistence.binding.PointTO.OutgoingPath;
import org.opentcs.util.persistence.models.AllowedOperation;
import org.opentcs.util.persistence.models.Block;
import org.opentcs.util.persistence.models.LocationType;
import org.opentcs.util.persistence.models.Member;
import org.opentcs.util.persistence.models.Model;
import org.opentcs.util.persistence.models.Point;
import org.opentcs.util.persistence.models.Property;

public final class PlantModelConverter {

	private PlantModelConverter() {

	}

	public static final PlantModelTO convertModelToPlantModelTO(final Model model) {
		final PlantModelTO plantModelTo = new PlantModelTO();
		plantModelTo.setVersion(model.getVersion());
		plantModelTo.setName(model.getName());

		final List<Block> modelBlocks = model.getBlocks();

		return plantModelTo;

	}

	public static final Model convertPlantModelTOtoDbModel(final PlantModelTO plantModelTo) {
		final Model model = new Model();
//				model.setId(5);

		model.setVersion(plantModelTo.getVersion());
		model.setName(plantModelTo.getName());

		for (final PointTO pointTO : plantModelTo.getPoints()) {
			model.getPoints().add(pointTOtoPoint(pointTO));
		}

		//Setting Up Blocks
		final List<Block> modelBlocks = new ArrayList<>();
		final List<Member> modelMembers = new ArrayList<>();
		plantModelTo.getBlocks().forEach(blockTO -> {
			final Member member = new Member();
			member.setName(blockTO.getName());
			modelMembers.add(member);
			final Block block = new Block();
			block.setMembers(modelMembers);
			block.setName(blockTO.getName());
			modelBlocks.add(block);
		});
		model.setBlocks(modelBlocks);

		//Setting LocationType
		final List<LocationType> modelLoactionTypes = new ArrayList<>();
		final List<AllowedOperation> allowedOperationsModel = new ArrayList<>();
		final List<Property> propertyListForLocationType = new ArrayList<>();
		final List<LocationTypeTO> plantModelLocationTypeTO = plantModelTo.getLocationTypes();
		plantModelLocationTypeTO.forEach(locationTypeTo -> {
			locationTypeTo.getAllowedOperations().forEach(allowedOperation -> {
				final AllowedOperation allowedOperationForModel = new AllowedOperation();
				//        allowedOperationForModel.setId(Integer.getInteger(allowedOperation.getId().toString()));
				allowedOperationForModel.setName(allowedOperation.getName());
				allowedOperationsModel.add(allowedOperationForModel);
			});
			locationTypeTo.getProperties().forEach(toProperty -> {
				final Property property = new Property();
				property.setName(toProperty.getName());
				property.setValue(toProperty.getValue());
				propertyListForLocationType.add(property);
			});
			final LocationType locationType = new LocationType();
			locationType.setName(locationTypeTo.getName());
			locationType.setAllowedOperations(allowedOperationsModel);
			locationType.setProperties(propertyListForLocationType);
			modelLoactionTypes.add(locationType);
		});
		model.setLocationTypes(modelLoactionTypes);

		return model;
	}

	private static Point pointTOtoPoint(final PointTO pointTo) {
		final Point point = new Point();
		point.setName(pointTo.getName());
		point.setType(pointTo.getType());
		point.setVehicleOrientationAngle(pointTo.getVehicleOrientationAngle().toString());
		point.setxPosition(pointTo.getxPosition().toString() );
		point.setyPosition(pointTo.getyPosition().toString());
		point.setzPosition(pointTo.getzPosition().toString());

		final List<OutgoingPath> outgoingPathsTO = pointTo.getOutgoingPaths();
		for (final OutgoingPath outgoingPathTO : outgoingPathsTO) {
			final org.opentcs.util.persistence.models.OutgoingPath op = new org.opentcs.util.persistence.models.OutgoingPath();
			op.setName(outgoingPathTO.getName());
			point.getOutGoingPaths().add(op);
		}
		return point;

	}
}
