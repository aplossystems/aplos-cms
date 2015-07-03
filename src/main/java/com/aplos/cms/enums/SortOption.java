package com.aplos.cms.enums;

public enum SortOption {

	DEFAULT_ORDERING ("Default", " ORDER BY rp.productInfo.defaultRealizedProduct.productInfo.dateCreated DESC"),
	NAME_A_TO_Z ("Item Name", " ORDER BY rp.productInfo.defaultRealizedProduct.productInfo.product.name ASC, rp.productInfo.defaultRealizedProduct.productColour.name ASC"),
	PRICE_LOW_TO_HIGH ("Price, Low to High", " ORDER BY rp.productInfo.defaultRealizedProduct.price ASC"),
	PRICE_HIGH_TO_LOW ("Price, High to Low", " ORDER BY rp.productInfo.defaultRealizedProduct.price DESC");

	private String displayName,sortHql;

	private SortOption( String newDisplayName, String newSortHql ) {
		displayName = newDisplayName;
		sortHql = newSortHql;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getSortingHql() {
		return sortHql;
	}

	public void setSortingHql( String sortingHql ) {
		this.sortHql = sortingHql;
	}
}
