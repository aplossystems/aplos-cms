package com.aplos.cms.backingpage.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringEscapeUtils;

import com.aplos.cms.backingpage.DeletedPageRevisionListPage;
import com.aplos.cms.backingpage.GeneratorMenuNodeEditPage;
import com.aplos.cms.backingpage.UnclickableMenuNodeEditPage;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageAlias;
import com.aplos.cms.beans.pages.CmsPageFolder;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.beans.pages.CmsPageLink;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsTemplate;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.annotations.DefaultTrailDisplayName;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=CmsPageRevision.class)
@DefaultTrailDisplayName(name="CMS Pages")
public class CmsPageRevisionListPage extends ListPage {

	private static final long serialVersionUID = -5599577548298054132L;
	/* For saving movement in the tree */
	private String dragNode;
	private String dropNode;
	private String dropType;
	private MenuNode rootMenu;
	private List<Long> openIds = new ArrayList<Long>();
	private Map<Long,MenuNode> menuNodeByIdMap;
	private Map<Long,CmsPage> cmsPageByIdMap;
	private Map<Long,List<CmsPageRevision>> cmsPageRevisionByCmsPageIdMap;

	public CmsPageRevisionListPage() {
		super();
	}

	@Override
	public boolean ignoresPaidFeatureCheck(){
		return true;
	}

	public boolean hasEcommerceModule() {
		return ApplicationUtil.getAplosContextListener().getAplosModuleByName("ecommerce") != null;
	}

	public void goToNewPageGenerator() {
		CmsPageGenerator cmsPageGenerator = new CmsPageGenerator();
		GeneratorMenuCmsAtom generatorMenuCmsAtom = new GeneratorMenuCmsAtom();
		cmsPageGenerator.setGeneratorMenuCmsAtom(generatorMenuCmsAtom);
		MenuNode generatorNode = new MenuNode();
		generatorNode.setParentWebsite(Website.getCurrentWebsiteFromTabSession());
		generatorNode.setParent(((CmsWebsite)Website.getCurrentWebsiteFromTabSession()).getUnusedMenu());
		generatorNode.setCmsPage(cmsPageGenerator);
		generatorNode.addToScope();
		JSFUtil.redirect(GeneratorMenuNodeEditPage.class);
	}

	public void goToNewLink() {
		CmsPageLink cmsPageLink = new CmsPageLink();
		cmsPageLink.redirectToEditPage();
	}

	public void goToNewPage() {
		BeanDao cmsPageDao = new BeanDao( CmsPage.class );
		CmsPage newCmsPage = (CmsPage) cmsPageDao.getNew();
		BeanDao cmsPageRevisionDao = new BeanDao( CmsPageRevision.class );
		CmsPageRevision newCmsPageRevision = (CmsPageRevision) cmsPageRevisionDao.getNew();
		newCmsPageRevision.setCmsPage( newCmsPage );
		CmsWebsite website = (CmsWebsite) CmsWebsite.getCurrentWebsiteFromTabSession();
		if (website != null) {
			CmsTemplate main = website.getMainTemplate();
			if (main != null) {
				BeanDao dao = new BeanDao(TopLevelTemplate.class);
				TopLevelTemplate loaded = dao.get(main.getId());
//				loaded.hibernateInitialise( true );
				newCmsPageRevision.setTemplate(loaded, true, true);
			}
		}
		//newCmsPageRevision.setTemplate( (TopLevelTemplate) new AqlBeanDao( TopLevelTemplate.class ).getAllQuery().setMaxResults(1).uniqueResult(), true, true );

		JSFUtil.addToFlashViewMap("cmsPageRevision", newCmsPageRevision);
		newCmsPageRevision.addToScope();

		JSFUtil.redirect( getBeanDao().getEditPageClass() );
	}

	public void goToNewUnclickable() {
		MenuNode unclickableNode = new MenuNode();
		unclickableNode.setName( "" );
		unclickableNode.setParentWebsite(Website.getCurrentWebsiteFromTabSession());
		unclickableNode.setParent(((CmsWebsite)Website.getCurrentWebsiteFromTabSession()).getUnusedMenu());
		unclickableNode.addToScope();
		JSFUtil.redirect( UnclickableMenuNodeEditPage.class );
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();

		BeanDao cmsPageDao = new SiteBeanDao(CmsPage.class);
		List<CmsPage> cmsPages = cmsPageDao.setIsReturningActiveBeans(true).getAll();
		cmsPageByIdMap = new HashMap<Long,CmsPage>();
		for (CmsPage page : cmsPages) {
			cmsPageByIdMap.put( page.getId(), page );
		}

		@SuppressWarnings("unchecked")
		SiteBeanDao tempCmsPageRevisionsDao = new SiteBeanDao( CmsPageRevision.class );
		tempCmsPageRevisionsDao.setSelectCriteria( "bean.id, bean.cmsPage.id, bean.draft, bean.current" );
		tempCmsPageRevisionsDao.addWhereCriteria( "current = true OR draft = true" );
		tempCmsPageRevisionsDao.setIsReturningActiveBeans(true);
		List<CmsPageRevision> tempCmsPageRevisions = tempCmsPageRevisionsDao.getAll();
		cmsPageRevisionByCmsPageIdMap = new HashMap<Long,List<CmsPageRevision>>();
		CmsPageRevision tempCmsPageRevision;
		try {
			for( int i = 0, n = tempCmsPageRevisions.size(); i < n; i++ ) {
				tempCmsPageRevision = new CmsPageRevision();
				Long cmsPageId = tempCmsPageRevisions.get( i ).getCmsPage().getId();
				tempCmsPageRevision.setCmsPage( cmsPageByIdMap.get( cmsPageId ) );
				if( tempCmsPageRevision.getCmsPage() != null) {
					if(cmsPageRevisionByCmsPageIdMap.get( cmsPageId ) == null ) {
						cmsPageRevisionByCmsPageIdMap.put( cmsPageId, new ArrayList<CmsPageRevision>() );
					}
					cmsPageRevisionByCmsPageIdMap.get( cmsPageId ).add( tempCmsPageRevision );
				}
			}
		} catch( Exception ex ) {
			ApplicationUtil.getAplosContextListener().handleError( ex );
		}

//		String sql = "SELECT id, cmsPage_id, parent_id, position, name FROM " + AplosBean.getTableName( MenuNode.class ) + " WHERE active = true AND parentWebsite_id = " + Website.getCurrentWebsiteFromTabSession().getId();
		@SuppressWarnings("unchecked")
		BeanDao menuNodeDao = new BeanDao( MenuNode.class );
		menuNodeDao.addWhereCriteria( "bean.parentWebsite.id = " + Website.getCurrentWebsiteFromTabSession().getId() );
		List<MenuNode> menuNodeList = menuNodeDao.getAll();
		menuNodeByIdMap = new HashMap<Long,MenuNode>();
		for( int i = 0, n = menuNodeList.size(); i < n; i++ ) {
			menuNodeByIdMap.put( menuNodeList.get( i ).getId(), menuNodeList.get( i ) );
			if( menuNodeList.get( i ).getParent() == null ) {
				rootMenu = menuNodeList.get( i );
			}
		}

		return true;
	}

	public String getMenuJson() {
		StringBuffer jsonBuff = new StringBuffer();
		for( int i = 0, n = rootMenu.getChildren().size(); i < n; i++ ) {
			getMenuNodeJson( jsonBuff, rootMenu.getChildren().get( i ) );
			jsonBuff.append( "," );
		}
		getRecycleBinJson( jsonBuff );
		return jsonBuff.toString();
	}

	public void getRecycleBinJson( StringBuffer jsonBuff ) {
		jsonBuff.append( "{ " );
		jsonBuff.append( "\"attributes\" : { " );
		jsonBuff.append( "\"id\" : \"-1\", " );
		jsonBuff.append( "\"rel\" : \"recycle\"" );
		jsonBuff.append( " }, " );
		jsonBuff.append( "\"data\": { " );
		jsonBuff.append( "\"title\" : \"<span style='color:#636363'>Recycle Bin</span>\"" );
		jsonBuff.append( "}" );
		jsonBuff.append( "}" );
	}

	public void getMenuNodeJson( StringBuffer jsonBuff, MenuNode menuNode ) {
		String type = "root";
		if (menuNode.getParent() != null && menuNode.getParent() != rootMenu) {
			type = "node";
		}
		if (menuNode.getCmsPage() != null ) {
			if (menuNode.getCmsPage() instanceof CmsPageAlias) {
				type = "alias";
			} else if (menuNode.getCmsPage() instanceof CmsPageGenerator) {
				type = "generator";
			} else if (menuNode.getCmsPage() instanceof CmsPageLink) {
				type = "link";
			} else {
				type = "edit";
			}
//			type = (menuNode.getCmsPage().isAuthor( JSFUtil.getCurrentUser() )
//							? "edit"
					//	? (page.isDraft() ? "draft" : "edit")
//						: "view"
//					);
		} else if( menuNode.getParent() != null && menuNode.getParent() != rootMenu ) {
			if (menuNode.getName() == null || menuNode.getName().equals("")) {
				type = "menuspacer";
			} else {
				type = "unclickable";
			}
		}

		// Server side type is not updated when moving nodes -----
		jsonBuff.append( "{ " );
		jsonBuff.append( "\"attributes\" : { " );
		jsonBuff.append( "\"id\" : \"" + menuNode.getId() + "\", " );
		jsonBuff.append( "\"rel\" : \"" + type + "\"" );
		jsonBuff.append( " }, " );
		jsonBuff.append( "\"data\": { " );
		jsonBuff.append( "\"title\" : \"" + getDisplayNameHtml( menuNode ).replace( "\"", "\\\"") + "\"" );
		jsonBuff.append( "}" );

		if (!menuNode.getChildren().isEmpty()) {
			jsonBuff.append( ", \"state\" : \"" );
			if ( menuNode.getId() == 1 || openIds.contains(menuNode.getId()) ) {
				jsonBuff.append( "open" );
			} else {
				jsonBuff.append( "closed" );
			}
			jsonBuff.append( "\", \"children\" : [ " );

			boolean first = true;
			for (MenuNode node : menuNode.getChildren()) {
				if (!first) {
					jsonBuff.append( ", " );
				}
				if (first) {
					first = false;
				}
				getMenuNodeJson( jsonBuff, node );
			}

			jsonBuff.append( " ] " );
		}
		jsonBuff.append( "}" );
	}

	public CmsPageRevision getLatestRevision( CmsPage cmsPage ) {
		List<CmsPageRevision> cmsPageRevisionList = cmsPageRevisionByCmsPageIdMap.get( cmsPage.getId() );
		if (cmsPageRevisionList != null) {
			if(  cmsPageRevisionList.size() == 1 ) {
				return cmsPageRevisionList.get ( 0 );
			} else {
				for( CmsPageRevision tempCmsPageRevision : cmsPageRevisionList ) {
					if( tempCmsPageRevision.isDraft() ) {
						return tempCmsPageRevision;
					}
				}
			}
		}
		return null;
	}

	public String getDisplayNameHtml( MenuNode menuNode ) {
		if( menuNode.getCmsPage() != null ) {
			StringBuffer strBuf = new StringBuffer(  );
			String name = StringEscapeUtils.escapeHtml(menuNode.getCmsPage().getName());
			if (menuNode.getCmsPage() instanceof CmsPageAlias) {
				strBuf.append( "<em>" );
				strBuf.append( name );
				strBuf.append( "</em>" );
			} else {
				strBuf.append( name );
			}
			String mapping = menuNode.getCmsPage().getMapping();
			if ((mapping == null || mapping.equals("")) && menuNode.getCmsPage() instanceof CmsPageAlias) {
				if (((CmsPageAlias)menuNode.getCmsPage()).getCmsPage() != null) {
					mapping = ((CmsPageAlias)menuNode.getCmsPage()).getCmsPage().getMapping();
				}
			}
			if (mapping != null && !mapping.equals("")) {
				strBuf.append( "&#160;<em style='color:#939393'>(&#160;" );
				strBuf.append( mapping );
				strBuf.append( "&#160;)</em>" );
			}
			CmsPageRevision latestRevision = getLatestRevision( menuNode.getCmsPage() );
			if( latestRevision != null && latestRevision.isDraft() ) {
				strBuf.append( "<em>(Draft)</em>" );
			}
			return strBuf.toString();
		} else {
			if (menuNode.getName() == null || menuNode.getName().equals("")) {
				return "<em style='color:#939393'>- spacer -</em>";
			} else {
				return menuNode.getName();
			}
		}
	}

	public boolean isTemplateAvailable() {
		if( ((Long) ApplicationUtil.getFirstResult("select count(*) from " + TopLevelTemplate.class.getSimpleName() + " bean WHERE bean.active = true"  )[0]).intValue() > 0 ) {
			return true;
		} else {
			return false;
		}

	}

	public void updateTree() {
		MenuNode drag = (MenuNode)new BeanDao(MenuNode.class).get(Long.parseLong( dragNode ));
		MenuNode drop = null;
		if (dropNode != null && !dropNode.equals( "-1" ) ) {
			try {
				drop = (MenuNode)new BeanDao(MenuNode.class).get(Long.parseLong( dropNode ));
			} catch (NumberFormatException nfe) {
				//do nothing, thrown when moving folder to root
			}
		}
		if( drag != null ) {
			drag = drag.getSaveableBean();
		}
		if( drop != null ) {
			drop = drop.getSaveableBean();
		}

		if( drag.getParent() != null ) {
			drag.getParent().getChildren().remove( drag );
		}
//		CmsModule.getMenuNodesById().get( drag.getId() ).getParent().getChildren().remove( drag );

		if (dropNode != null && !dropNode.equals( "-1" ) ) {
			moveNode( drag, drop, dropType );
		}
		if (drag.getCmsPage() != null) { //null when moving a division/folder
			CmsPage saveableCmsPage = drag.getCmsPage().getSaveableBean();
			saveableCmsPage.updateCachedMappingPath();
			saveableCmsPage.saveDetails();
		}
		
		if( dropNode != null && dropNode.equals( "-1" ) ) {
			drag.delete();
		} else {
			drag.saveDetails();
		}
		if ( drop != null && dropNode != null && !dropNode.equals( "-1" )) {
			drop.saveDetails();
		}
	}

	public void selectNode() {
		if (dropNode.equals("-1")) {
			//recycle bin clicked
			JSFUtil.redirect( DeletedPageRevisionListPage.class );
			return;
		}
		MenuNode drop = (MenuNode)new BeanDao(MenuNode.class).get(Long.parseLong( dropNode ));
		
		
		if (drop.getCmsPage() != null && ApplicationUtil.getClass(drop.getCmsPage()).equals( CmsPage.class ) ) {
			drop.getCmsPage().getLatestRevision().redirectToEditPage();
		} else {
			drop.getSaveableBean().addToScope();
			
			if( drop.getCmsPage() == null || drop.getCmsPage() instanceof CmsPageFolder ) {
				JSFUtil.redirect( UnclickableMenuNodeEditPage.class );
			} else {
//				loadedDropMenu.getCmsPage().hibernateInitialise( true );
				if ( drop.getCmsPage() instanceof CmsPageAlias ) {
					drop.getCmsPage().getSaveableBean().addToScope();
					JSFUtil.redirect( AliasMenuNodeEditPage.class );
				} else if( drop.getCmsPage() instanceof CmsPageGenerator ) {
					drop.getCmsPage().getSaveableBean().addToScope();
					JSFUtil.redirect( GeneratorMenuNodeEditPage.class );
				} else if( drop.getCmsPage() instanceof CmsPageLink ) {
					drop.getCmsPage().redirectToEditPage();
				} 
			}
		}
	}

	public Integer getPageLimit() {
		return ((CmsWebsite)Website.getCurrentWebsiteFromTabSession()).getCmsPageLimit();
	}

	public boolean getHasReachedPageLimit() {
		Integer limit = getPageLimit();
		//limit can be -1 for unlimited
		return limit != null && limit >= 0 && cmsPageByIdMap.size() >= limit;
	}

	private void moveNode(MenuNode drag, MenuNode drop, String type) {
		MenuNode dropParent = drop.getParent();
		MenuNode dragParent = drag.getParent();
		if( dropParent != null ) {
			dropParent = dropParent.getSaveableBean();
		}
		if( dragParent != null ) {
			dragParent = dragParent.getSaveableBean();
		}
		if ("before".equals( type )) {
			if (drop != null) { //moving to root
				dropParent.getChildren().add( dropParent.getChildren().indexOf( drop ), drag );
				drag.setParent( dropParent );
			} else {
				drag.setParent( null );
			}			
		} else if ("after".equals(type)) {
			if (drop != null) { //moving to root
				dropParent.getChildren().add( dropParent.getChildren().indexOf( drop )+1, drag );
				drag.setParent( dropParent );
			} else {
				drag.setParent( null );
			}
		} else if ("inside".equals(type)) {
			if (drop != null) { //moving to root
				drop.getChildren().add( drag );
				drag.setParent( drop );
			} else {
				drag.setParent( null );
			}			
		}
		if( dragParent != null ) {
			dragParent.saveDetails();
		}
		if( dropParent != null && !dropParent.equals( dragParent ) ) {
			dropParent.saveDetails();
		}
	}

	public void save() {
		for( int i = 0, n = rootMenu.getChildren().size(); i < n; i++ ) {
			rootMenu.getChildren().get( i ).saveDetails();
		}
		JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ));
	}

	public List<MenuNode> getMenus() {
//		List<Menu> menus = new AqlBeanDao(MenuNode.class).addWhereCriteria( "parent = null").getAll();
		return rootMenu.getChildren();
	}
	public String getDragNode() {
		return dragNode;
	}

	public void setDragNode( String dragNode ) {
		this.dragNode = dragNode;
	}

	public String getDropNode() {
		return dropNode;
	}

	public void setDropNode( String dropNode ) {
		this.dropNode = dropNode;
	}

	public String getDropType() {
		return dropType;
	}

	public void setDropType( String dropType ) {
		this.dropType = dropType;
	}

	public void openNode() {
		try {
			openIds.add( Long.parseLong(dropNode) );
		} catch (NumberFormatException nfe) {}
	}
	
	public void closeNode() {
		try {
			openIds.remove( Long.parseLong(dropNode) );
		} catch (NumberFormatException nfe) {}
	}

	public List<Long> getOpenIds() {
		return openIds;
	}

	public void setOpenIds(List<Long> openIds) {
		this.openIds = openIds;
	}
	
}
