package net.hcangus.divider.selector;

import net.hcangus.divider.Direction;
import net.hcangus.divider.Position;

import java.util.EnumSet;

/**
 * Anydoor
 * Created by hcangus
 */

public class SouthSelector extends AllItemsSelector {

	@Override
	public boolean isPositionSelected(Position position) {
		return !position.isLastRow();
	}

	@Override
	public EnumSet<Direction> getDirectionsByPosition(Position position) {
		return EnumSet.of(Direction.SOUTH);
	}
}
