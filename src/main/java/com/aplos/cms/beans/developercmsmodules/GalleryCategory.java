package com.aplos.cms.beans.developercmsmodules;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.lookups.LookupBean;

@Entity
@PluralDisplayName(name="image gallery categories")
public class GalleryCategory extends LookupBean {
	private static final long serialVersionUID = -8675375919922297502L;

	public GalleryCategory() {}

	public GalleryCategory( String categoryName ) {
		this.setName( categoryName );
	}
}
