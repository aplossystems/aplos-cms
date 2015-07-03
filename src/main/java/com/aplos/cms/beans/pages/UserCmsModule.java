package com.aplos.cms.beans.pages;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.UiParameterData;
import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.ManyToAny;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.persistence.PersistentClass;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;

@ManagedBean
@SessionScoped
@Entity
@PluralDisplayName(name="user CMS modules")
@Cache
public class UserCmsModule extends CmsAtom implements PlaceholderContent {
	private static final long serialVersionUID = -9091701112045018263L;
	@Column(columnDefinition="LONGTEXT")
	private String content = "<p>Empty Module</p>";

	@ManyToAny( metaColumn = @Column( name = "cmsAtom_type" ), fetch=FetchType.EAGER )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinTable( inverseJoinColumns = @JoinColumn( name = "cmsAtom_id" ) )
	@Cache
	@DynamicMetaValues
	private List<CmsAtom> cmsAtomList = new ArrayList<CmsAtom>();

	@Column(columnDefinition="LONGTEXT")
	private String xmlNamespaceStr;

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		super.hibernateInitialiseAfterCheck( fullInitialisation );
//		HibernateUtil.initialiseList(cmsAtomList, true);
//		HibernateUtil.initialiseList(cmsAtomList, fullInitialisation);
//	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public ContentPlaceholderType getCphType() {
		return ContentPlaceholderType.CMS_ATOM;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void saveBean( SystemUser currentUser ) {
		setName( XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(getName()) );
		content = XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(content);
		super.saveBean( currentUser );
		if (this.isActive()) {
			writeViewToFile();
		}
	}

	/* Because the modules can be nested */
	public String getParsedContent(String contextRoot, CmsPageRevision cmsPageRevision) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		JDynamiTe jd = new JDynamiTe();
		try {
			jd.setInput( new ByteArrayInputStream(this.getContent().getBytes()));

			//parses everything fill jdyanmite does except '_PAGE_'
			Enumeration keys = jd.getVariableKeys();
			while(keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				if (key.startsWith( "_FORM_" )) {
					int id = Integer.parseInt( key.replaceFirst( "_FORM_", "" ) );
					Form form = (Form) new BeanDao( Form.class ).get(id);
					if (form != null) {
						jd.setVariable(key, form.getHtml(cmsPageRevision, contextRoot) );
					} else {
						jd.setVariable( key, "" );
					}
				} else if (key.startsWith( "_MENU_")) {
//					int id = Integer.parseInt( key.replaceFirst( "_MENU_", "" ) );
//					MenuNode menuNode = (MenuNode) new AqlBeanDao( MenuNode.class ).load(id);
//					if (menuNode != null) {
//						ContentPage contentPage = (ContentPage) PageBindingPhaseListener.resolveBackingPage( ContentPage.class );
//						jd.setVariable( key, contentPage.generateMenuCode( menuNode.getChildren(), cmsPageRevision, contextRoot, cmsPageRevision.getCmsPage().getMapping(), 12 ) );
//					}
				} else if (key.startsWith( "_CSS_")) {
					int id = Integer.parseInt( key.replaceFirst( "_CSS_", "" ) );
					CssResource cssResource = (CssResource) new BeanDao( CssResource.class ).get(id);
					if (cssResource != null) {
						jd.setVariable( key, "<link href='" + contextRoot + "/content/css/" + id + ".css?version=" + cssResource.getVersion() + "' rel='stylesheet' type='text/css' />"  );
					}
				} else if (key.startsWith( "_USER_MODULE_")) {
					int id = Integer.parseInt( key.replaceFirst( "_USER_MODULE_", "" ) );
					UserCmsModule userCmsModule = (UserCmsModule) new BeanDao( UserCmsModule.class ).get(id);
					if (userCmsModule != null) {
						jd.setVariable( key, userCmsModule.getParsedContent(contextRoot,cmsPageRevision) );
					}
				}
			}

			jd.parse();
			return jd.toString();

		} catch( IOException e ) {

			e.printStackTrace();
			return null;
		}

	}

	public void initModules( boolean isFrontEnd, boolean isRequestPageLoad ) {
		System.err.println( "UserCmsModules" );
		for( CmsAtom tempCmsAtom : getCmsAtomList() ) {
			if( tempCmsAtom instanceof DeveloperCmsAtom ) {
				if( isFrontEnd ) {
					((DeveloperCmsAtom) tempCmsAtom).initFrontend(isRequestPageLoad);
				} else {
					if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
						((ConfigurableDeveloperCmsAtom) tempCmsAtom).initModule( isFrontEnd, isRequestPageLoad );
					}
				}
			}
		}
	}

	public String generateXhtml( String contextRoot ) {
		StringBuffer xhtmlBuffer = new StringBuffer();
		xhtmlBuffer.append( "<ui:composition");
		xhtmlBuffer.append( " xmlns=\"http://www.w3.org/1999/xhtml\"");
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
		//for ease of debugging
		xhtmlBuffer.append( "<!-- User CMS Module: " + this.getName() + " -->\n" );
		xhtmlBuffer.append( "<!-- Last Generated: " + FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(), new Date()) + " -->\n" );
		String processedContent = getContent();
		processedContent = parseCmsAtoms( processedContent );
		processedContent = CommonUtil.includeContextRootInPaths( contextRoot, processedContent );
		xhtmlBuffer.append(processedContent);
		xhtmlBuffer.append( "</ui:composition>\n" );
		return xhtmlBuffer.toString();
	}
	
	public String parseCmsAtoms( String viewAsString ) {
		Pattern pattern = Pattern.compile("\\{_CA_(.*?)_([0-9]*?)_([0-9]*)\\}");
		Matcher matcher = pattern.matcher(viewAsString);
		while (matcher.find()) {
			PersistentClass persistentClass = (PersistentClass) ApplicationUtil.getPersistentApplication().getPersistableTableBySqlNameMap().get( matcher.group(1).toLowerCase() );
			DeveloperCmsAtom developerCmsAtom = (DeveloperCmsAtom) new BeanDao( (Class<? extends AplosAbstractBean>) persistentClass.getTableClass() ).get( Long.parseLong( matcher.group(2) ) );
			int cmsAtomIdx = -1;
			for( int i = 0, n = cmsAtomList.size(); i < n; i++ ) {
				if( cmsAtomList.get( i ).equals( developerCmsAtom )) {
					cmsAtomIdx = i;
					break;
				}
			}

			try {

				List<UiParameterData> paramList = new ArrayList<UiParameterData>();
				StringBuffer cmsAtomStrBuf = new StringBuffer( "cmsPageRevision.getUserModule( " ).append( getId() );
				cmsAtomStrBuf.append( ").cmsAtomList[" ).append( String.valueOf( cmsAtomIdx ) ).append( "]" );
				
				StringBuffer strBuf = new StringBuffer( "#{" ).append( cmsAtomStrBuf.toString() ).append( "}" );
				paramList.add( new UiParameterData( CommonUtil.getBinding( developerCmsAtom.getClass() ), strBuf.toString() ) );

				strBuf = new StringBuffer( "#{" ).append( cmsAtomStrBuf.toString() ).append( ".frontendModuleBacking" ).append( "}" );
				paramList.add( new UiParameterData( developerCmsAtom.getFeDmbBinding(), strBuf.toString() ) );
				
				viewAsString = matcher.replaceFirst(developerCmsAtom.getContent( Integer.parseInt(matcher.group(3)), paramList ));
				matcher = pattern.matcher(viewAsString);
			} catch (NumberFormatException nfe) {
				/* be quiet */
			}
		}
		return viewAsString;
	}

	public File writeViewToFile() {
		return CmsModule.writeViewToFile(generateXhtml( JSFUtil.getContextPath() ), CmsWorkingDirectory.CMS_USER_MODULE_VIEW_FILES, getId(), "xhtml");
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setXmlNamespaceStr(String xmlNamespaceStr) {
		this.xmlNamespaceStr = xmlNamespaceStr;
	}

	public String getXmlNamespaceStr() {
		return xmlNamespaceStr;
	}

	@Override
	public CmsAtom getCopy() {  // should never be called?
		return this;
	}

	public void setCmsAtomList(List<CmsAtom> cmsAtomList) {
		this.cmsAtomList = cmsAtomList;
	}

	public List<CmsAtom> getCmsAtomList() {
		return cmsAtomList;
	}
}
