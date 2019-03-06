package com.facilio.bmsconsole.context;

public class SpaceContext extends BaseSpaceContext {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private SpaceContext parentSpace;
	public SpaceContext getParentSpace() {
		return parentSpace;
	}
	public void setParentSpace(SpaceContext parentSpace) {
		this.parentSpace = parentSpace;
	}
	//	private SpaceContext space1;
//	public SpaceContext getSpace() {
//		if ((space1 == null || space1.getId() == -1) && super.getSpaceId() != -1) {
//			SpaceContext space = new SpaceContext();
//			space.setId(super.getSpaceId());
//			return space;
//		}
//		return space1;
//	}
//	public void setSpace(SpaceContext space) {
//		this.space1 = space;
//		if(space != null) {
//			super.setSpaceId(space.getId());
//		}
//	}

	private SpaceCategoryContext spaceCategory;
	public SpaceCategoryContext getSpaceCategory() {
		return spaceCategory;
	} 
	public void setSpaceCategory(SpaceCategoryContext spaceCategory) {
		this.spaceCategory = spaceCategory;
	}
}
