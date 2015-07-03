package com.aplos.cms.beans.developercmsmodules;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.UiParameterData;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

@MappedSuperclass
public abstract class DeveloperCmsAtom extends CmsAtom implements PlaceholderContent {
	private static final long serialVersionUID = -4034267168947232322L;

	private String cmsAtomIdStr;

	public DeveloperCmsAtom() {}

	public boolean initFrontend( boolean isRequestPageLoad ) {
		return this.initModuleBacking( getFeDmbBinding(), isRequestPageLoad );
	}

	public String getFeDmbBinding() {
		return getCmsAtomIdStr() + "FeDmb";
	}
	
	public DeveloperModuleBacking getFeDmb() {

		DeveloperModuleBacking developerModuleBacking = (DeveloperModuleBacking) JSFUtil.getViewMap().get(getFeDmbBinding()); 
		if (developerModuleBacking == null) {
			JSFUtil.getFromTabSession(getFeDmbBinding()); 
		}
		
		return developerModuleBacking;
		
	}

	public DeveloperModuleBacking getFrontendModuleBacking() {
		return CmsModule.getDeveloperModuleBacking( getFeDmbBinding(), this );
	}
	
	public static void sortList( List<? extends DeveloperCmsAtom> cmsAtomList ) {
		Collections.sort( cmsAtomList, new Comparator<DeveloperCmsAtom>() {
			@Override
			public int compare(DeveloperCmsAtom o1, DeveloperCmsAtom o2) {
				return (o1.getAplosModuleName() + o1.getName()).compareTo( o2.getAplosModuleName() + o2.getName() ) ;
			}
		});
	}

	public String getAllInsertTexts() {
		List<Integer> files = getInsertFileNumbers(getAplosModuleName(), getFrontEndBodyName());
		StringBuffer insertTextsBuf = new StringBuffer();
		for (Integer i : files) {
			addNewInsertText(insertTextsBuf, i);
		}
		return insertTextsBuf.toString();
	}

	public List<Integer> getInsertFileNumbers() {
		return getInsertFileNumbers( getAplosModuleName(), getFrontEndBodyName() );
	}

	public static List<Integer> getInsertFileNumbers(String aplosModuleName, String frontEndBodyName ) {
		URL url = null;
		String fileName;
		int count = 0;
		List<Integer> files = new ArrayList<Integer>();
		while( url != null || count == 0 ) {
			count++;
			fileName = getFrontendBodyUrl( count, aplosModuleName, frontEndBodyName );
			url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
			if( url == null && count == 1 ) {
				// This is some code to check for files that don't have the count appended on
				// for backwards compatibility
				fileName = fileName.replace( "_1.xhtml", ".xhtml" );
				url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
				if( url != null ) {
					files.add(0);
				} else {
					fileName = fileName.replace( ".xhtml", "_default_1.xhtml" );
					url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
				}
			} else if( url != null ) {
				files.add(count);
			}
		}
		return files;
	}

	public String getFirstInsertText() {
		String fileName;
		URL url = null;
		int count = 0;
		while( (url != null || count == 0) && count < 10 ) {
			count++;
			fileName = "/" + getAplosModuleName() + "/developermodule/frontend/" + getCmsAtomIdStr() + "Body_" + count + ".xhtml";
			url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
			if( url == null && count == 1 ) {
				// This is some code to check for files that don't have the count appended on
				// for backwards compatibility
				fileName = "/" + getAplosModuleName() + "/developermodule/frontend/" + getCmsAtomIdStr() + "Body.xhtml";
				url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
				if( url != null ) {
					return "{_CA_" + AplosBean.getTableName( ApplicationUtil.getClass( this ) ) + "_" + getId() + "_0}";
				}
			} else if( url != null ) {
				return "{_CA_" + AplosBean.getTableName( ApplicationUtil.getClass( this ) ) + "_" + getId() + "_" + count + "}";
			}
		}
		return "";
	}

	public void addNewInsertText( StringBuffer insertTextsBuf, int count ) {
		if( insertTextsBuf.length() != 0 ) {
			insertTextsBuf.append( "," );
		}
		insertTextsBuf.append( "{_CA_" + AplosBean.getTableName( ApplicationUtil.getClass( this ) ) + "_" + getId() + "_" + count + "}" );
	}

	@Override
	public ContentPlaceholderType getCphType() {
		return ContentPlaceholderType.CMS_ATOM;
	}

	public static String getFrontendBodyUrl( int fileNumber, String aplosModuleName, String cmsAtomIdStr ) {
		String fileName = "/" + aplosModuleName + "/developermodule/frontend/" + cmsAtomIdStr;
		if( fileNumber != 0 ) {
			fileName += "_" + fileNumber;
		}
		return fileName + ".xhtml";
	}

	public int getPrimaryViewIdFileNumber() {
		String fileName = "/" + getAplosModuleName() + "/developermodule/frontend/" + getCmsAtomIdStr() + "Body.xhtml";
		URL url = JSFUtil.checkFileLocations(fileName, "resources/faceletviews", true );
		if( url == null) {
			return 1;
		} else {
			return 0;
		}
	}

	public String addFrontEndHeadUrl() {
		String frontendHeadUrl = getFrontendHeadUrl();
		URL url = JSFUtil.checkFileLocations(frontendHeadUrl, "resources/faceletviews", true );
		if( url != null ) {
			return "<ui:include src=\"" + frontendHeadUrl + "\" />";
		}
		return "";
	}

	public String getFrontendHeadUrl() {
		return  "/" + getAplosModuleName() + "/developermodule/frontend/" + getCmsAtomIdStr() + "Head.xhtml";
	}

	@Override
	public String getContent() {
		return getContent( getPrimaryViewIdFileNumber(), null );
	}
	
	public String getFrontEndBodyName() {
		return getCmsAtomIdStr() + "Body";
	}

	public String getContent( int fileNumber, List<UiParameterData> paramList ) {
		StringBuffer strBuf = new StringBuffer( "<ui:include src=\"" );
		strBuf.append( getFrontendBodyUrl( fileNumber, getAplosModuleName(), getFrontEndBodyName() ) ).append( "\" >" );
		if( paramList != null ) {
			for( int i = 0, n = paramList.size(); i < n; i++ ) {
				strBuf.append( "<ui:param name=\"" ).append( paramList.get( i ).getName() );
				strBuf.append( "\" value=\"" ).append( paramList.get( i ).getValue() ).append( "\" />" );
			}
		}
		strBuf.append( "</ui:include>" );
		return strBuf.toString();
	}


	public boolean initModuleBacking(String binding, boolean isRequestPageLoad ) {
		this.addToScope();

		DeveloperModuleBacking developerModuleBacking = CmsModule.getDeveloperModuleBacking( binding, this );
		if( developerModuleBacking != null ) {
			addModuleBackingToScope( binding, developerModuleBacking );
			developerModuleBacking.addAssociatedBackingsToScope();
			if ( isRequestPageLoad ) {
				return developerModuleBacking.requestPageLoad( this );
			} else {
				return developerModuleBacking.responsePageLoad( this );
			}
		}
		return true;
	}
	
	public static void addModuleBackingToScope( String binding, DeveloperModuleBacking developerModuleBacking ) {
		if( developerModuleBacking.getClass().getAnnotation( ViewScoped.class ) != null ) {
			JSFUtil.getViewMap().put("moduleFeDmb", developerModuleBacking); //for use when the module is extended
			JSFUtil.getViewMap().put(binding, developerModuleBacking); //for use when the module is extended
		} else if( developerModuleBacking.getClass().getAnnotation( CustomScoped.class ) != null ) {
			JSFUtil.addToTabSession("moduleFeDmb", developerModuleBacking); //for use when the module is extended
			JSFUtil.addToTabSession(binding, developerModuleBacking); //for use when the module is extended
		}
	}

	public void setCmsAtomIdStr(String cmsAtomIdStr) {
		this.cmsAtomIdStr = cmsAtomIdStr;
	}

	public String getCmsAtomIdStr() {
		return cmsAtomIdStr;
	}

	public abstract String getAplosModuleName();

	@Override
	public abstract DeveloperCmsAtom getCopy();
}
