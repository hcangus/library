package net.hcangus.divider.selector;


import net.hcangus.divider.Direction;
import net.hcangus.divider.Position;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Anydoor
 * Created by hcangus
 *
 * 		 |      |
 *   ___|   ___|   ___
 *
 *
 *
 */

public class GridSelector extends AllItemsSelector {
	@Override
	public EnumSet<Direction> getDirectionsByPosition(Position position) {
		EnumSet<Direction> directions = EnumSet.noneOf(Direction.class);
		if (!position.isLastRow()) {
			directions.addAll(Arrays.asList(Direction.SOUTH_WEST, Direction.SOUTH, Direction.SOUTH_EAST));
		} else {
			directions.addAll(Arrays.asList(Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST));
		}
		if (!position.isLastColumn()) {
			directions.addAll(Arrays.asList(Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST));
		}
		return directions;
	}
}
