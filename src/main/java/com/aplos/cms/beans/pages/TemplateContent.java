package com.aplos.cms.beans.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import com.aplos.cms.PlaceholderContentWrapper;
import com.aplos.cms.beans.UiParameterData;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.HibernateMapKey;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.ManyToAny;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.JSFUtil;

@MappedSuperclass
public abstract class TemplateContent extends AplosSiteBean {
	private static final long serialVersionUID = -4312068935697916437L;

	@Any( metaColumn = @Column( name = "cmsTemplate_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = { } )
    @JoinColumn(name="cmsTemplate_id")
	@DynamicMetaValues
	private CmsTemplate cmsTemplate;

	@ManyToAny( metaColumn = @Column( name = "cmsAtom_type" ), fetch=FetchType.EAGER )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinTable( inverseJoinColumns = @JoinColumn( name = "cmsAtom_id" ) )
	@Cache
	@DynamicMetaValues
	@Cascade({CascadeType.ALL})
	private List<CmsAtom> cmsAtomList = new ArrayList<CmsAtom>();

    @ManyToAny( metaColumn = @Column( name = "cmsAtom_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinTable( inverseJoinColumns = @JoinColumn( name = "cmsAtom_id" ) )
	@Cache
    @DynamicMetaValues
	@Cascade({CascadeType.ALL})
	private Map<Long,CmsAtom> cmsAtomPassedThroughMap = new HashMap<Long,CmsAtom>();

	@ManyToMany(cascade={CascadeType.ALL})
	@HibernateMapKey(columns = @Column(name = "mapKey"))
	@JoinTable(
			joinColumns = @JoinColumn(name = "templateContent_id"),
			inverseJoinColumns = @JoinColumn(name = "cmsPlaceholderContent_id"))
	@Cache
	@Cascade({CascadeType.ALL})
	private Map<String,CmsPlaceholderContent> placeholderContentMap = new HashMap<String, CmsPlaceholderContent>();

	@Column(columnDefinition="LONGTEXT")
	private String xmlNamespaceStr;

	@Transient
	private ServletContext tempServletContext;

	public TemplateContent() {
	}

	public void checkAtomListForDuplicates() {
		if( JSFUtil.getFacesContext() != null ) {
			Map<String, Map<Long, Integer>> cmsAtomDuplicateMap = new HashMap<String, Map<Long, Integer>>();
			boolean duplicateRemoved = false;
			Map<Long, Integer> atomIdMap;
			for( int i = getCmsAtomList().size() - 1; i >= 0; i-- ) {
				atomIdMap = cmsAtomDuplicateMap.get( ApplicationUtil.getClass( getCmsAtomList().get( i ) ).getName() );
				if( atomIdMap != null ) {
					if( atomIdMap.get( getCmsAtomList().get( i ).getId() ) != null ) {
						atomIdMap.put( getCmsAtomList().get( i ).getId(), atomIdMap.get( getCmsAtomList().get( i ).getId() ) + 1 );
						getCmsAtomList().remove( i );
						duplicateRemoved = true;
					} else {
						atomIdMap.put( getCmsAtomList().get( i ).getId(), 1 );
					}
				} else {
					atomIdMap = new HashMap<Long, Integer>();
					atomIdMap.put( getCmsAtomList().get( i ).getId(), 1 );
					cmsAtomDuplicateMap.put( ApplicationUtil.getClass( getCmsAtomList().get( i ) ).getName(), atomIdMap );
				}
			}

			if( duplicateRemoved ) {
				saveDetails( JSFUtil.getLoggedInUser());
				ErrorEmailSender.sendErrorEmail( JSFUtil.getRequest(), ApplicationUtil.getAplosContextListener(), new Exception() );
			}
		}
	}
	
	public String parseCmsAtoms( String viewAsString ) {
		Pattern pattern = Pattern.compile("\\{_CA_(.*?)_([0-9]*?)_([0-9]*)\\}");
		Matcher matcher = pattern.matcher(viewAsString);
		while (matcher.find()) {
			String atomClassName = matcher.group(1);
			String atomId = matcher.group(2);
			DeveloperCmsAtom developerCmsAtom = getDeveloperCmsAtomFromList( Long.parseLong( atomId ), atomClassName);
			if( developerCmsAtom != null ) {
				try {
					List<UiParameterData> paramList = new ArrayList<UiParameterData>();
					if( developerCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
						StringBuffer cmsAtomBuf = new StringBuffer( getElPath() ).append( ".getDeveloperCmsAtomFromList( " );
						cmsAtomBuf.append( String.valueOf( atomId ) ).append( ", '" );
						cmsAtomBuf.append( atomClassName ).append( "')" );
						StringBuffer strBuf = new StringBuffer( "#{" ).append( cmsAtomBuf.toString() ).append( "}" );
						
						paramList.add( new UiParameterData( CommonUtil.getBinding( ApplicationUtil.getClass( developerCmsAtom ) ), strBuf.toString() ) );

						strBuf = new StringBuffer( "#{" ).append( cmsAtomBuf.toString() ).append( ".frontendModuleBacking" ).append( "}" );
						paramList.add( new UiParameterData( developerCmsAtom.getFeDmbBinding(), strBuf.toString() ) );
					}
					viewAsString = matcher.replaceFirst(developerCmsAtom.getContent( Integer.parseInt(matcher.group(3)), paramList ));
					matcher = pattern.matcher(viewAsString);
				} catch (NumberFormatException nfe) {
					/* be quiet */
				}
			}
		}
		
		pattern = Pattern.compile("\\{_CAPT_([0-9]*?)_([0-9]*)\\}");
		matcher = pattern.matcher(viewAsString);
		while (matcher.find()) {
			Long cmsAtomPassThroughId = Long.parseLong( matcher.group(1) );
			DeveloperCmsAtom developerCmsAtom = (DeveloperCmsAtom) getCmsAtomPassedThroughMap().get( cmsAtomPassThroughId );

			if( developerCmsAtom != null ) {
				try {
					List<UiParameterData> paramList = new ArrayList<UiParameterData>();
					StringBuffer passThroughStrBuff = new StringBuffer( getElPath() ).append( ".cmsAtomPassedThroughMap[" );
					passThroughStrBuff.append( String.valueOf( cmsAtomPassThroughId ) ).append( "]" );
					StringBuffer strBuf = new StringBuffer( "#{" ).append( passThroughStrBuff.toString() ).append( " }" );
					
					paramList.add( new UiParameterData( CommonUtil.getBinding( ApplicationUtil.getClass( developerCmsAtom ) ), strBuf.toString() ) );

					strBuf = new StringBuffer( "#{" ).append( passThroughStrBuff.toString() ).append( ".frontendModuleBacking" ).append( "}" );
					paramList.add( new UiParameterData( developerCmsAtom.getFeDmbBinding(), strBuf.toString() ) );
					
					viewAsString = matcher.replaceFirst(developerCmsAtom.getContent( Integer.parseInt(matcher.group(2)), paramList ));
					matcher = pattern.matcher(viewAsString);
				} catch (NumberFormatException nfe) {
					/* be quiet */
				}
			}
		}
		return viewAsString;
	}
	
	public CmsAtom getCmsAtomPassThroughAtom( Long cmsAtomPassThroughId ) {
		return getCmsAtomPassedThroughMap().get( cmsAtomPassThroughId );
	}

	public boolean initModules( boolean isFrontEnd, boolean isRequestPageLoad, boolean isInitParentTemplates ) {
		for ( CmsAtom tempCmsAtom : getCmsAtomList() ) {
			if ( tempCmsAtom instanceof DeveloperCmsAtom ) {
				if ( isFrontEnd ) {
					if( !((DeveloperCmsAtom) tempCmsAtom).initFrontend(isRequestPageLoad) ) {
						return false;
					}
				} else {
					if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
						if( !((ConfigurableDeveloperCmsAtom) tempCmsAtom).initModule( isFrontEnd, isRequestPageLoad ) ) {
							return false;
						}
					}
				}
			}
		}
		for (Long cmsAtomPassThroughId : getCmsAtomPassedThroughMap().keySet() ) {
			if( getCmsTemplate().cmsAtomPassThroughListContains( cmsAtomPassThroughId ) ) {
				DeveloperCmsAtom tempCmsAtom = (DeveloperCmsAtom) getCmsAtomPassedThroughMap().get( cmsAtomPassThroughId );
				if( tempCmsAtom instanceof DeveloperCmsAtom ) {
					if( isFrontEnd ) {
						if( !tempCmsAtom.initFrontend( isRequestPageLoad) ) {
							return false;
						}
					} else {
						if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
							if( !((ConfigurableDeveloperCmsAtom) tempCmsAtom).initModule( isFrontEnd, isRequestPageLoad ) ) {
								return false;
							}
						}
					}
				}
			}
		}
		if ( getCmsTemplate() != null && getCmsTemplate() instanceof TemplateContent ) {
			if( !((TemplateContent) getCmsTemplate()).initModules(isFrontEnd, isRequestPageLoad, isInitParentTemplates) ) {
				return false;
			}
		}
		return true;
	}

	public String generateXhtml() {
		String contextPath = JSFUtil.getContextPath();
		StringBuffer xhtmlBuffer = new StringBuffer();
		String templateDirectory;
		if (getCmsTemplate() instanceof InnerTemplate) {
			templateDirectory = CmsWorkingDirectory.INNER_TEMPLATE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false), "");
		} else {
			templateDirectory = CmsWorkingDirectory.TOP_TEMPLATE_VIEW_FILES.getDirectoryPath(false).replace(CmsWorkingDirectory.CMS_GENERATED_VIEW_FILES.getDirectoryPath(false), "");
		}
		xhtmlBuffer.append( "<ui:composition template=\"/" + templateDirectory + getCmsTemplate().getId() + ".xhtml" );
		xhtmlBuffer.append( "\" xmlns=\"http://www.w3.org/1999/xhtml\"");
		xhtmlBuffer.append( " xmlns:ui=\"http://java.sun.com/jsf/facelets\" " );
		if( getXmlNamespaceStr() != null ) {
			xhtmlBuffer.append( getXmlNamespaceStr() );
		}
		if (getXmlNamespaceStr() == null || !getXmlNamespaceStr().contains("xmlns:aplos")) {
			//need the aplos library for including menus invisibly
			xhtmlBuffer.append( " xmlns:aplos=\"http://www.aplossystems.co.uk/aplosComponents\" " );
		}
		if (getXmlNamespaceStr() == null || !getXmlNamespaceStr().contains("xmlns:h=")) {
			//need the h library for {_page_name} (becomes a h output text)
			xhtmlBuffer.append( " xmlns:h=\"http://java.sun.com/jsf/html\" " );
		}
		xhtmlBuffer.append( " >\n" );

		StringBuffer innerXhtmlBuff = new StringBuffer();
		appendXhtmlForTemplateContent( contextPath, innerXhtmlBuff );
		xhtmlBuffer.append( CommonUtil.includeContextRootInPaths( contextPath, innerXhtmlBuff.toString() ) );
		xhtmlBuffer.append( "</ui:composition>\n" );

		//TODO: this just adds innertemplate/cmspagerevision content to the holder object (innertemplate content never read back out)
		//addXhtmlContentToPageContentHolder( contextPath, cmsPageRevision, xhtmlBuffer.toString() );
		//return getTemplate().generateXhtml(contextPath, cmsPageRevision);

		return xhtmlBuffer.toString();
	}
	
	public DeveloperCmsAtom getDeveloperCmsAtomFromList( Long atomId, String atomClassName ) {
		for( int i = 0, n = getCmsAtomList().size(); i < n; i++ ) {
			if( getCmsAtomList().get( i ).getId().equals( atomId ) && ApplicationUtil.getClass( cmsAtomList.get( i ) ).getSimpleName().equals( atomClassName ) ) {
				return (DeveloperCmsAtom) getCmsAtomList().get( i );
			}
		}
		return null;
	}

//	public abstract void addXhtmlContentToPageContentHolder( String contextRoot, CmsPageRevision cmsPageRevision, String content );

	public abstract List<String> getTemplateEditableCphNameList(boolean toLowerCase);

	protected void appendXhtmlForTemplateContent( String contextRoot, StringBuffer xhtmlBuffer ) {
		List<String> templateElements = getTemplateEditableCphNameList(false);
		String tempContent;
//		CommonUtil.timeTrial( "start PlaceholderContent", getTempServletContext() );
		CmsPlaceholderContent tempPlaceholderContent;
		for (String cphName : templateElements ) {
			/* If the template defines a place-holder which we have set ... */
			tempPlaceholderContent = getPlaceholderContent( cphName );
			if( tempPlaceholderContent != null &&
					tempPlaceholderContent.getContent() != null &&
					!(tempContent = tempPlaceholderContent.getContent()).equals( "" ) ) {
				includeContent( contextRoot, xhtmlBuffer, cphName, tempContent );
			}
		}

//		CommonUtil.timeTrial( "start cmsPassThroughMap", getTempServletContext() );
		for (Long cmsAtomPassThroughId : getCmsAtomPassedThroughMap().keySet() ) {
			if( getCmsTemplate().cmsAtomPassThroughListContains( cmsAtomPassThroughId ) ) {
				DeveloperCmsAtom tempCmsAtom = (DeveloperCmsAtom) getCmsAtomPassedThroughMap().get( cmsAtomPassThroughId );
				if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
					List<Integer> fileNumbers = tempCmsAtom.getInsertFileNumbers();
					List<UiParameterData> paramList = new ArrayList<UiParameterData>();
					StringBuffer cmsAtomStrBuf = new StringBuffer( getElPath() ).append( ".cmsAtomPassedThroughMap[" );
					cmsAtomStrBuf.append( String.valueOf( cmsAtomPassThroughId ) ).append( "]" );
					StringBuffer strBuf = new StringBuffer( "#{" ).append( cmsAtomStrBuf.toString() ).append( "}" );
					paramList.add( new UiParameterData( CommonUtil.getBinding( ApplicationUtil.getClass( tempCmsAtom ) ), strBuf.toString() ) );

					strBuf = new StringBuffer( "#{" ).append( cmsAtomStrBuf.toString() ).append( ".frontendModuleBacking" ).append( "}" );
					paramList.add( new UiParameterData( tempCmsAtom.getFeDmbBinding(), strBuf.toString() ) );
					
					for( int i = 0, n =fileNumbers.size(); i < n; i++ ) {
						includeContent( contextRoot, xhtmlBuffer, "CAPT:" + cmsAtomPassThroughId + ":" + fileNumbers.get( i ), tempCmsAtom.getContent(fileNumbers.get( i ),paramList) );	
					}
					
				} else {
					UnconfigurableDeveloperCmsAtom unconfigurableDeveloperCmsAtom = (UnconfigurableDeveloperCmsAtom) tempCmsAtom;
					includeContent( contextRoot, xhtmlBuffer, "UNCONFIGURABLE_MODULE:" + unconfigurableDeveloperCmsAtom.getAplosModuleName() + ":" + CommonUtil.firstLetterToUpperCase(unconfigurableDeveloperCmsAtom.getCmsAtomIdStr()), tempCmsAtom.getContent() );
				}
			}
		}
	}
	
	public String getElPath() {
		return "";
	}

	public void includeContent( String contextRoot, StringBuffer xhtmlBuffer, String cphName, String tempContent ) {
		xhtmlBuffer.append("<ui:define name='" + cphName + "'>\n");
		xhtmlBuffer.append(tempContent);
		xhtmlBuffer.append( "</ui:define>\n" );
	}

	//  This hack has been put in here as we had to use the c:forEach in the JSF page so that the ui:include will work
	//  However for some reason this stopped the var from being read through the request variable in the backingbean
	//  so now I've included a wrapper so that when the value is changed I can update the backingPage information as well
	//  as knowing which PlaceholderContent to update
	public List<PlaceholderContentWrapper> getPlaceholderContentWrapperList( boolean isForCmsPageRevision ) {
		List<PlaceholderContentWrapper> phcWrapperList = new ArrayList<PlaceholderContentWrapper>();
		List<String> editableCphList = getCmsTemplate().getEditableCphNameList( isForCmsPageRevision, true );
		PlaceholderContentWrapper tempPhcWrapper;
		for( String mapKey : getPlaceholderContentMap().keySet() ) {
			if( editableCphList.contains( mapKey ) ) {
				tempPhcWrapper = new PlaceholderContentWrapper();
				tempPhcWrapper.setCphName( mapKey );
				tempPhcWrapper.setPhcMap(getPlaceholderContentMap());
				tempPhcWrapper.setPlaceholderContent(getPlaceholderContent( mapKey ));
				phcWrapperList.add( tempPhcWrapper );
			}
		}
		for( CmsAtom cmsAtom : getCmsAtomList() ) {
			if( cmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				tempPhcWrapper = new PlaceholderContentWrapper();
				tempPhcWrapper.setCphName( cmsAtom.getDisplayName() );
				tempPhcWrapper.setPhcMap(getPlaceholderContentMap());
				tempPhcWrapper.setPlaceholderContent((ConfigurableDeveloperCmsAtom) cmsAtom);
				phcWrapperList.add( tempPhcWrapper );
			}
		}
		for( Long cmsAtomPassThroughId : getCmsAtomPassedThroughMap().keySet() ) {
			if( getCmsTemplate().cmsAtomPassThroughListContains( cmsAtomPassThroughId ) ) {
				CmsAtom cmsAtom = getCmsAtomPassedThroughMap().get( cmsAtomPassThroughId );
				tempPhcWrapper = new PlaceholderContentWrapper();
				tempPhcWrapper.setCphName( cmsAtom.getDisplayName() );
				tempPhcWrapper.setPhcMap(getPlaceholderContentMap());
				tempPhcWrapper.setPlaceholderContent((DeveloperCmsAtom) cmsAtom);
				phcWrapperList.add( tempPhcWrapper );
			}
		}
		return phcWrapperList;
	}

	public abstract void setTemplate( CmsTemplate cmsTemplate );

	public void setTemplate(CmsTemplate cmsTemplate, boolean isForCmsPageRevision, boolean hasTemplateChanged ) {
		if( cmsTemplate != null && (this.getCmsTemplate() == null || !this.getCmsTemplate().getId().equals( cmsTemplate.getId() ) || hasTemplateChanged) ) {
			List<String> editableCphNameList = cmsTemplate.getEditableCphNameList(isForCmsPageRevision, false);
			for( int i = 0; i < editableCphNameList.size(); i++ ) {
				if( !getPlaceholderContentMap().containsKey( editableCphNameList.get( i ).toLowerCase() ) ) {
					addPlaceholderContent( editableCphNameList.get( i ), new CmsPlaceholderContent() );
				}
			}

			for( CmsAtomPassThrough cmsAtomPassThrough : cmsTemplate.getCmsAtomPassThroughList() ) {
				if( cmsAtomPassedThroughMap.get( cmsAtomPassThrough.getId() ) == null ) {
					if( cmsAtomPassThrough.getCmsAtom() instanceof UnconfigurableDeveloperCmsAtom ) {
						cmsAtomPassedThroughMap.put( cmsAtomPassThrough.getId(), cmsAtomPassThrough.getCmsAtom() );
					} else {
						cmsAtomPassedThroughMap.put( cmsAtomPassThrough.getId(), cmsAtomPassThrough.getCmsAtomNewInstance() );
					}
				}
			}
		}
		this.setCmsTemplate(cmsTemplate);
	}

	public void setXmlNamespaceStr(String xmlNamespaceStr) {
		this.xmlNamespaceStr = xmlNamespaceStr;
	}

	public String getXmlNamespaceStr() {
		return xmlNamespaceStr;
	}

	public void setPlaceholderContentMap(Map<String,CmsPlaceholderContent> placeholderContentMap) {
		this.placeholderContentMap = placeholderContentMap;
	}
	
	public void addPlaceholderContent( String key, CmsPlaceholderContent cmsPlaceholderContent ) {
		if( cmsPlaceholderContent.getPlaceholderName() == null ) {
			cmsPlaceholderContent.setPlaceholderName( key );
		}
		getPlaceholderContentMap().put( key.toLowerCase(), cmsPlaceholderContent );
	}
	
	public CmsPlaceholderContent getPlaceholderContent( String key ) {
		return getPlaceholderContentMap().get( key.toLowerCase() );
	}

	public Map<String,CmsPlaceholderContent> getPlaceholderContentMap() {
		return placeholderContentMap;
	}

	public void setCmsAtomList(List<CmsAtom> cmsAtomList) {
		this.cmsAtomList = cmsAtomList;
	}

	public List<CmsAtom> getCmsAtomList() {
		return cmsAtomList;
	}

	public void setCmsAtomPassedThroughMap(Map<Long,CmsAtom> cmsAtomPassedThroughMap) {
		this.cmsAtomPassedThroughMap = cmsAtomPassedThroughMap;
	}

	public Map<Long,CmsAtom> getCmsAtomPassedThroughMap() {
		return cmsAtomPassedThroughMap;
	}

	public void setTempServletContext(ServletContext tempServletContext) {
		this.tempServletContext = tempServletContext;
	}

	public ServletContext getTempServletContext() {
		return tempServletContext;
	}

	public void setCmsTemplate(CmsTemplate cmsTemplate) {
		this.cmsTemplate = cmsTemplate;
	}

	public CmsTemplate getCmsTemplate() {
		return cmsTemplate;
	}
}
