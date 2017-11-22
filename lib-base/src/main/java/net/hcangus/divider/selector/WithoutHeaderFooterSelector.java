package net.hcangus.divider.selector;

import net.hcangus.divider.Direction;
import net.hcangus.divider.Position;
import net.hcangus.ptr.recycler.AutoLoadRecyclerView;
import net.hcangus.ptr.recycler.HeaderFooterRecyclerAdapter;

import java.util.EnumSet;

/**
 * Anydoor Header 和 Footer不含分割线
 * Created by Administrator.
 */

public class WithoutHeaderFooterSelector extends AllItemsSelector {

	private final HeaderFooterRecyclerAdapter outAdapter;

	public WithoutHeaderFooterSelector(AutoLoadRecyclerView alRecycler) {
		outAdapter = alRecycler.getOutAdapter();
		if (outAdapter == null) {
			throw new RuntimeException("The recyclerView does not set an adapter!");
		}
	}

	@Override
	public boolean isPositionSelected(Position position) {
		int absoluteIndex = position.getAbsoluteIndex();
		return !outAdapter.isHeader(absoluteIndex) && !outAdapter.isFooter(absoluteIndex + 1);
	}

	@Override
	public EnumSet<Direction> getDirectionsByPosition(Position position) {
		return EnumSet.of(Direction.SOUTH);
	}
}
