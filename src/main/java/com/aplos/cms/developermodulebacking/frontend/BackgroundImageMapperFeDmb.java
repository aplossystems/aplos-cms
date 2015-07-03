package com.aplos.cms.developermodulebacking.frontend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.BackgroundImageMapping;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
public class BackgroundImageMapperFeDmb extends DeveloperModuleBacking {

	/**
	 *
	 */
	private static final long serialVersionUID = -5624612758792241288L;
	private Boolean isBackgroundMapped;
	private BackgroundImageMapping backgroundImageMapping;

	public void setIsBackgroundMapped(Boolean isBackgroundMapped) {
		this.isBackgroundMapped = isBackgroundMapped;
	}

	public Boolean getIsBackgroundMapped() {
		if (isBackgroundMapped == null) {
			isBackgroundMapped=false;
			BeanDao dao = new BeanDao(BackgroundImageMapping.class);
			dao.setOrderBy("bean.priority DESC");
			List<BackgroundImageMapping> backgroundMappings = dao.setIsReturningActiveBeans(true).getAll();
			if (backgroundMappings != null && backgroundMappings.size() > 0) {
				String currentPageMapping = JSFUtil.getAplosContextOriginalUrl().replaceAll( ".aplos", "" );
				currentPageMapping = currentPageMapping.replaceFirst( JSFUtil.getContextPath(), "" );
				for (BackgroundImageMapping mapping : backgroundMappings) {
					if (currentPageMapping.contains(mapping.getMapping())) {
						isBackgroundMapped=true;
						backgroundImageMapping=mapping;
						break;
					}
				}
			}
		}
		return isBackgroundMapped;
	}

	public void setBackgroundImageMapping(BackgroundImageMapping backgroundImageMapping) {
		this.backgroundImageMapping = backgroundImageMapping;
	}

	public BackgroundImageMapping getBackgroundImageMapping() {
		return backgroundImageMapping;
	}


}

