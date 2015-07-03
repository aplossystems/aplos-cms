package com.aplos.cms.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.Exhibition;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.ExhibitionCmsAtom;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;

@ManagedBean
@ViewScoped
public class ExhibitionFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8204583565473251762L;
	private List<Exhibition> sortedExhibitions = new ArrayList<Exhibition>();
	private CmsPageRevision cmsPageRevision;
	private Exhibition selectedExhibition;
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoad = super.responsePageLoad(developerCmsAtom);
		cmsPageRevision = JSFUtil.getBeanFromScope( CmsPageRevision.class );
		if( continueLoad ) {
			createSortedExhibitionsList( true );
		}
		String exhibitionIdStr = JSFUtil.getRequestParameter("exhibition_id");
		if( !CommonUtil.isNullOrEmpty( exhibitionIdStr ) ) {
			Long id = null;
			try {
				id = Long.parseLong(exhibitionIdStr);
			} catch (NumberFormatException nfe) {
				ApplicationUtil.getAplosContextListener().handleError(nfe);
			}
			
			setSelectedExhibition((Exhibition)new BeanDao( Exhibition.class ).get( id ));
		}
		return continueLoad;
	}

	public String redirectToExhibition() {
		Exhibition exhibition = (Exhibition) JSFUtil.getRequest().getAttribute("exhibition");
		JSFUtil.redirect(new CmsPageUrl(cmsPageRevision,"exhibition_id=" + exhibition.getId()),true);
		return null;
	}

	@SuppressWarnings("unchecked")
	public void createSortedExhibitionsList( boolean removePastExhibitions ) {
		List<Exhibition> exhibitionsToSort;
		if( removePastExhibitions ) {
			exhibitionsToSort = getFutureExhibitions();
		} else {
			exhibitionsToSort = getExhibitionCmsAtom().getExhibitions(); 
		}
		
		sortedExhibitions = (List<Exhibition>) PositionedBeanHelper.getSortedPositionedBeanList( (List<PositionedBean>) (List<? extends PositionedBean>) exhibitionsToSort );
	}
	
	public List<Exhibition> getFutureExhibitions() {
		List<Exhibition> futureExhibitions = new ArrayList<Exhibition>( getExhibitionCmsAtom().getExhibitions() );
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime( new Date() );
		cal.add( Calendar.DAY_OF_YEAR, -7 );
		for( int i = futureExhibitions.size() - 1; i >= 0; i-- ) {
			if( futureExhibitions.get( i ).getStartDate().compareTo( cal.getTime() ) <= 0 ) {
				futureExhibitions.remove( i );
			}
		}
		return futureExhibitions;
	}
	
	public ExhibitionCmsAtom getExhibitionCmsAtom() {
		return (ExhibitionCmsAtom) getDeveloperCmsAtom();
	}

	public List<Exhibition> getSortedExhibitions() {
		return sortedExhibitions;
	}

	public void setSortedExhibitions(List<Exhibition> sortedExhibitions) {
		this.sortedExhibitions = sortedExhibitions;
	}

	public Exhibition getSelectedExhibition() {
		return selectedExhibition;
	}

	public void setSelectedExhibition(Exhibition selectedExhibition) {
		this.selectedExhibition = selectedExhibition;
	}
}
