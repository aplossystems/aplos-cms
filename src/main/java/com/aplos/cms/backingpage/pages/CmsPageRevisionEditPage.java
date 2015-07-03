package com.aplos.cms.backingpage.pages;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringEscapeUtils;
import org.primefaces.event.SelectEvent;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.ContentPlaceholderCacher;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPageAlias;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.beans.pages.CmsTemplate;
import com.aplos.cms.beans.pages.CodeSnippet;
import com.aplos.cms.beans.pages.CssResource;
import com.aplos.cms.beans.pages.InnerTemplate;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.cms.module.CmsModule;
import com.aplos.cms.utils.CmsUtil;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CmsPageRevision.class)
public class CmsPageRevisionEditPage extends EditPage {

	private static final long serialVersionUID = 5359749408520072345L;
	private SystemUser newAuthor;
	private ContentPlaceholderCacher cphCacher = new ContentPlaceholderCacher();
	private CmsAtom selectedCmsAtom;
	private CmsTemplate selectedTemplate;
	private String newSnippetName = null;
	
	@Override
	public boolean saveBean() {
		boolean beanSaved = false;
		CmsPageRevision cmsPageRevision = (CmsPageRevision) JSFUtil.getBeanFromScope(CmsPageRevision.class);

		if(cmsPageRevision.getCmsPage().getSiteMapPriority() >= 1 && cmsPageRevision.getCmsPage().getSiteMapPriority() <= 0.0 ) {
			JSFUtil.addMessage( "Site map priority must be between 0 and 1" );
		}
	
		if( CmsUtil.validateMapping( cmsPageRevision.getCmsPage(), cmsPageRevision.getCmsPage().getMapping() ) ) {
			callModuleBackingsSaveBtnAction( cmsPageRevision );
			beanSaved = saveCmsPageRevision(cmsPageRevision);
			JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ));
		} else {
			JSFUtil.addMessage( "This mapping is already in use by another page.", FacesMessage.SEVERITY_ERROR );	
		}
		return beanSaved;
	}
	
	public List<SelectItem> getSslProtocolSelectItems() {
		return CommonUtil.getEnumSelectItems( SslProtocolEnum.class );
	}
	
	public String getRedirectDescription() {
		CmsPageRevision cmsPageRevision = resolveAssociatedBean();
		if (cmsPageRevision != null) {
			if (cmsPageRevision.getDeletedRedirect() != null) {
				return "Requests to this page are being redirected to " + cmsPageRevision.getDeletedRedirect().getCmsPage().getName() + " (" + cmsPageRevision.getDeletedRedirect().getCmsPage().getMapping() + ".aplos).";
			}
		}
		return "Requests to this page are not currently redirected. Select a page below, to forward traffic.";
	}
	
	public void removeRedirect() {
		CmsPageRevision cmsPageRevision = resolveAssociatedBean();
		if (cmsPageRevision != null) {
			cmsPageRevision.setDeletedRedirect(null);
		}
	}
	
	public void selectDeleteRedirect( SelectEvent event ) {
		CmsPageRevision selectedCmsPageRevision = (CmsPageRevision) event.getObject();
		if (selectedCmsPageRevision != null) {
			((CmsPageRevision) resolveAssociatedBean()).setDeletedRedirect(selectedCmsPageRevision);
		}
	}
	
	public List<? extends AplosAbstractBean> suggestDeleteRedirects(String searchStr) {
		BeanDao pageDao = new BeanDao(CmsPageRevision.class);
		pageDao.setSelectCriteria( "bean.id, bean.cmsPage.name, bean.cmsPage.cachedMappingPath, bean.cmsPage.mapping, bean.active" );
		pageDao.setWhereCriteria("bean.cmsPage.name LIKE :similarSearchStr OR bean.cmsPage.mapping LIKE :similarSearchStr");
		pageDao.setNamedParameter("similarSearchStr", "%" + searchStr + "%");
		pageDao.setMaxResults( 25 );
		return CmsPageRevision.sortByDisplayName((List<CmsPageRevision>)pageDao.getAll());
	}
	
	protected boolean saveCmsPageRevision(CmsPageRevision cmsPageRevision) {
		boolean beanSaved = false;
		if( cmsPageRevision.isNew() ) {
			beanSaved = saveNewPage( cmsPageRevision );
		} else {
			beanSaved = cmsPageRevision.saveDetails( JSFUtil.getLoggedInUser() );
		}
		return beanSaved;
	}
	
	public String getEditorCssUrl() {
		String editorCss = ((CmsWebsite) Website.loadWebsiteBySession()).getEditorCssUrl();
		if( editorCss == null ) {
			return "\"\"";
		} else {
			return editorCss;
		}
	}

	public void callModuleBackingsSaveBtnAction( CmsPageRevision cmsPageRevision ) {
		for( CmsAtom tempCmsAtom : cmsPageRevision.getCmsAtomList() ) {
			if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				DeveloperModuleBacking moduleBacking = ((ConfigurableDeveloperCmsAtom) tempCmsAtom).getBackendModuleBacking();
				if( moduleBacking != null ) {
					moduleBacking.applyBtnAction();
				}
			}
		}
		for( CmsAtom tempCmsAtom : cmsPageRevision.getCmsAtomPassedThroughMap().values() ) {
			/* 26/10/11 - added check to see that atom is in the current templates list
			 * otherwise we attempt to save older templates' pass through atoms and can experience
			 * NullPointerExceptions
			 */
			if( tempCmsAtom instanceof ConfigurableDeveloperCmsAtom && cmsPageRevision.getCmsTemplate().getCmsAtomPassThroughList().contains( tempCmsAtom ) ) {
				DeveloperModuleBacking moduleBacking = ((ConfigurableDeveloperCmsAtom) tempCmsAtom).getBackendModuleBacking();
				if( moduleBacking != null ) {
					moduleBacking.applyBtnAction();
				}
			}
		}
	}

	@Override
	public boolean requestPageLoad() {
		return super.requestPageLoad();
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		CmsPageRevision cmsPageRevision = resolveAssociatedBean();
		if ( cmsPageRevision == null ) {
			JSFUtil.addMessage( "A page has not been selected for editing" );
			JSFUtil.redirect( CmsWebsite.getCurrentWebsiteFromTabSession().getPackageName(), CmsPageRevisionListPage.class );
			JSFUtil.getFacesContext().responseComplete();
			return false;
		}
		cmsPageRevision.initModules( false, false, false );
		selectedTemplate = cmsPageRevision.getCmsTemplate();
		cmsPageRevision.setMetaDescription(XmlEntityUtil.replaceEntitiesWith(cmsPageRevision.getMetaDescription(), XmlEntityUtil.EncodingType.CHARACTER));
		cmsPageRevision.setMetaKeywords(XmlEntityUtil.replaceEntitiesWith(cmsPageRevision.getMetaKeywords(), XmlEntityUtil.EncodingType.CHARACTER));
		cmsPageRevision.setTitle(XmlEntityUtil.replaceEntitiesWith(cmsPageRevision.getTitle(), XmlEntityUtil.EncodingType.CHARACTER));

		for (CmsPlaceholderContent cmsPlaceholderContent : cmsPageRevision.getPlaceholderContentMap().values()) {
			if (cmsPlaceholderContent.getContent() != null) {
				cmsPlaceholderContent.setContent(
						XmlEntityUtil.replaceEntitiesWith(cmsPlaceholderContent.getContent(), XmlEntityUtil.EncodingType.CHARACTER));
			}
		}
				
		return true;
	}

	public void duplicatePage() {
		try {
			CmsPageRevision cmsPageRevisionCopy = new CmsPageRevision((CmsPageRevision) JSFUtil.getBeanFromScope(CmsPageRevision.class));
			cmsPageRevisionCopy.getCmsPage().saveDetails();
			cmsPageRevisionCopy.saveDetails();

			BeanDao menuNodeBeanDao = new BeanDao(MenuNode.class);
			menuNodeBeanDao.setWhereCriteria("bean.cmsPage.id = " + ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class)).getCmsPage().getId());
			MenuNode originalMenuNode = (MenuNode)menuNodeBeanDao.getFirstBeanResult();
			MenuNode menuNodeCopy = new MenuNode(Website.getCurrentWebsiteFromTabSession(), originalMenuNode.getParent(), cmsPageRevisionCopy.getCmsPage());
			menuNodeCopy.saveDetails();
			MenuNode menuNodeParent = menuNodeCopy.getParent().getSaveableBean();
			menuNodeParent.addChild(menuNodeCopy);
			menuNodeParent.saveDetails();
//			HibernateUtil.getCurrentSession().flush();
		}
		catch (Exception e) {
			e.printStackTrace();
			JSFUtil.addMessage("An error occured, the page has not been duplicated.", FacesMessage.SEVERITY_ERROR);
		}
		JSFUtil.addMessage("The page has been duplicated.", FacesMessage.SEVERITY_INFO);
		((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class)).goToListPage();
	}

	public String aliasPage() {
		CmsWebsite cmsSite = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
		try {
			CmsPageRevision cmsPageRevision = (CmsPageRevision) JSFUtil.getBeanFromScope(CmsPageRevision.class);
			if (cmsPageRevision.isNew()) {
				cmsPageRevision.saveDetails();
			}
			CmsPageAlias cmsPageAlias = new CmsPageAlias(cmsPageRevision.getCmsPage());
			cmsPageRevision.getCmsPage().saveDetails();
//			HibernateUtil.startNewTransaction();
			cmsPageAlias.saveDetails();
			//need to reload in case previously deleted one of its children
			MenuNode unusedPages = cmsSite.getUnusedMenu().getSaveableBean();
			MenuNode aliasMenuNode = new MenuNode(cmsSite, unusedPages, cmsPageAlias);
			aliasMenuNode.saveDetails();
			MenuNode saveableParent = aliasMenuNode.getParent().getSaveableBean(); 
			saveableParent.addChild(aliasMenuNode);
			saveableParent.saveDetails();
			cmsSite.setUnusedMenu(aliasMenuNode.getParent());
//			HibernateUtil.getCurrentSession().flush();

		} catch (Exception e) {
			e.printStackTrace();
			JSFUtil.addMessage("An error occured, the shortcut has not been created.", FacesMessage.SEVERITY_ERROR);
			return null;
		}
		JSFUtil.addMessage("The a shortcut to the page has been created in " + cmsSite.getUnusedMenu().getName() + ".", FacesMessage.SEVERITY_INFO);
		//return ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class)).getListPageNavigation();
		return null;
	}
	
	public String getDefaultEditorStyleClass() {
		return CmsConfiguration.getCmsConfiguration().getDefaultStyleClass();
	}

	//public void templateChanged(AjaxBehaviorEvent event) { //f:ajax needs event, p:ajax doesnt
	public void templateChanged() {
		if (selectedTemplate != null) {
			CmsPageRevision cmsPageRevision = resolveAssociatedBean();
			cmsPageRevision.setTemplate( selectedTemplate, true, true );
			cmsPageRevision.initModules( false, false, false );
		}
	}
	
	public void addCodeSnippet() {
		if (newSnippetName != null && !newSnippetName.equals("")) {
			CodeSnippet newCodeSnippet = new CodeSnippet();
			newCodeSnippet.setName(newSnippetName);
			CmsPageRevision cmsPageRevision = (CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class);
			newCodeSnippet.setCmsPageRevision(cmsPageRevision);
			cmsPageRevision.getCodeSnippetList().add( newCodeSnippet );
			newCodeSnippet.initBackend();
			if( cmsPageRevision.isNew() ) {
				cmsPageRevision.saveDetails();
			}
			newCodeSnippet.saveDetails();
			newSnippetName=null;
		} else {
			JSFUtil.addMessageForError("Please enter a name for the new snippet");
		}
	}
	
	public void removeCodeSnippet() {
		CodeSnippet codeSnippet = (CodeSnippet) JSFUtil.getRequest().getAttribute( "codeSnippet" );
		((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class)).getCodeSnippetList().remove( codeSnippet );
		codeSnippet.hardDelete();
	}

	public void addCmsAtom() {
		//the category options return a null
		if (selectedCmsAtom != null) {
			CmsAtom newCmsAtom;
			if( selectedCmsAtom instanceof UnconfigurableDeveloperCmsAtom ) {
				newCmsAtom = selectedCmsAtom;
			} else {
				newCmsAtom = (CmsAtom) CommonUtil.getNewInstance( ApplicationUtil.getClass( selectedCmsAtom ), null );
			}
			CmsPageRevision cmsPageRevision = JSFUtil.getBeanFromScope(CmsPageRevision.class); 
			cmsPageRevision.getCmsAtomList().add( newCmsAtom );
			if( newCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				newCmsAtom.saveDetails();
				((ConfigurableDeveloperCmsAtom) newCmsAtom).initBackend();
			}
			if( newCmsAtom.getSslProtocolEnum().equals( SslProtocolEnum.FORCE_SSL ) ) {
				cmsPageRevision.getCmsPage().setSslProtocolEnum(newCmsAtom.getSslProtocolEnum());
			}
		} else {
			JSFUtil.addMessageForError("Please select a valid atom");
		}
	}

	public void removeCmsAtom() {
		CmsAtom cmsAtom = (CmsAtom) JSFUtil.getRequest().getAttribute( "cmsAtom" );
		((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class)).getCmsAtomList().remove( cmsAtom );
	}

	/* For CKEditor dialogs */

	public String getCkFormItems() {
		BeanDao formDao = new BeanDao( Form.class );
		formDao.setSelectCriteria("bean.id,bean.active,bean.name");
		return ckItems( formDao.getAll(), "FORM" );
	}

	public String getCkMenuItems() {
		BeanDao menuNodeDao = new BeanDao( MenuNode.class );
		menuNodeDao.setSelectCriteria("bean.id,bean.active,bean.cmsPage.name");
		menuNodeDao.addWhereCriteria("parent = null");
		return ckItems( menuNodeDao.getAll(), "MENU" );
	}

	public String getCkCssItems() {
		BeanDao cssResourceDao = new BeanDao( CssResource.class );
		cssResourceDao.setSelectCriteria("bean.id,bean.name,bean.active");
		return ckItems( cssResourceDao.getAll(), "CSS" );
	}

	public String getCkPageItems() {
		BeanDao cmsPageRevisionDao = new BeanDao( CmsPageRevision.class );
		cmsPageRevisionDao.setSelectCriteria("bean.id,bean.active,bean.cmsPage.name");
		return ckItems( cmsPageRevisionDao.getAll(), "PAGE" );
	}

	public SelectItem[] getAvailableCmsAtomSelectItemBeans() {
		CmsWebsite cmsWebsite = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
		List<DeveloperCmsAtom> atomList = cmsWebsite.getAvailableCmsAtomList();
		List<DeveloperCmsAtom> configurableAtomList = new ArrayList<DeveloperCmsAtom>();
		List<DeveloperCmsAtom> unconfigurableAtomList = new ArrayList<DeveloperCmsAtom>();

		for (DeveloperCmsAtom cmsAtom : atomList) {
			if (cmsAtom instanceof ConfigurableDeveloperCmsAtom) {
				//System.out.println(cmsAtom.getClass().getSimpleName());
				configurableAtomList.add( cmsAtom );
			}
		}
		DeveloperCmsAtom.sortList( configurableAtomList );

		for (DeveloperCmsAtom cmsAtom : atomList) {
			if (cmsAtom instanceof UnconfigurableDeveloperCmsAtom) {
				unconfigurableAtomList.add( cmsAtom );
			}
		}
		DeveloperCmsAtom.sortList( unconfigurableAtomList );

		SelectItem[] items = new SelectItem[atomList.size() + 6];
		int pointer = 0;
		items[pointer++] = new SelectItem(null,"---", "", true,false,true);
		items[pointer++] = new SelectItem(null,"Configurable Atoms", "", true,false,true);
		items[pointer++] = new SelectItem(null,"---", "", true,false,true);
		for (DeveloperCmsAtom cmsAtom : configurableAtomList) {
			items[pointer++] = new SelectItem( cmsAtom, CommonUtil.firstLetterToUpperCase( cmsAtom.getAplosModuleName() ) + ": " + cmsAtom.getName() );
		}
		items[pointer++] = new SelectItem(null,"---", "", true,false,true);
		items[pointer++] = new SelectItem(null,"Non-configurable Atoms", "", true,false,true);
		items[pointer++] = new SelectItem(null,"---", "", true,false,true);

		for (DeveloperCmsAtom cmsAtom : unconfigurableAtomList) {
			items[pointer++] = new SelectItem( cmsAtom, CommonUtil.firstLetterToUpperCase( cmsAtom.getAplosModuleName() ) + ": " + cmsAtom.getName() );
		}
		return items;
	}

	public String ckItems(List<AplosAbstractBean> beans, String name) {
		StringBuilder s = new StringBuilder("[ ");
		boolean first = true;
		for (AplosAbstractBean aplosAbstractBean : beans) {
			s.append( (first ? "" : ",") + "[ '" + StringEscapeUtils.escapeJavaScript( aplosAbstractBean.getDisplayName() ) + "', '_" + name + "_" + aplosAbstractBean.getId() + "' ]\n" );
			first = false;
		}
		s.append( " ]" );
		return s.toString();
	}

	public void goToTemplate() {
		CmsPageRevision cmsPageRevision = resolveAssociatedBean();
		cmsPageRevision.getCmsTemplate().redirectToEditPage();
	}

	public void addAuthor() {
		if (newAuthor == null) {
			return;
		}
		CmsPageRevision cmsPageRevision = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		cmsPageRevision.getCmsPage().getAuthors().add( newAuthor );
	}

	public void removeAuthor(SystemUser author) {
		((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class)).getCmsPage().getAuthors().remove( author );
	}

	public List<SystemUser> getPossibleAuthors() {
		CmsPageRevision cmsPageRevision = (CmsPageRevision) JSFUtil.getBeanFromScope(CmsPageRevision.class);
		List<SystemUser> users = new BeanDao(SystemUser.class).setIsReturningActiveBeans(true).getAll();
		users.removeAll( (cmsPageRevision).getCmsPage().getAuthors() );
		users.remove( JSFUtil.getLoggedInUser() );
		return users;
	}

	public SelectItem[] getTemplateSelectItems() {
		List<AplosBean> templateList = new SiteBeanDao( TopLevelTemplate.class ).setIsReturningActiveBeans(true).getAll();
		templateList.addAll( new SiteBeanDao( InnerTemplate.class ).setIsReturningActiveBeans(true).getAll() );
		return AplosAbstractBean.getSelectItemBeans( CommonUtil.sortAlphabetically(templateList) );
	}

	public String previewBtnAction() {
		CmsPageRevision page = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		page.saveDetails();
		CmsPageRevision cmsPageRevision = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));

		JSFUtil.redirect( new CmsPageUrl(cmsPageRevision.getCmsPage()), true);
		return null;
	}

	public boolean getIsNotBasicUser() {
		return !( JSFUtil.getLoggedInUser().getUserLevel().equals(CmsModule.getUserLevelUtil().getBasicUserLevel()) );
	}

	public void templateChanged(ValueChangeEvent event) {
		event.getNewValue();
	}

	public boolean saveNewPage( CmsPageRevision page ) {
		boolean beanSaved = false;
		if (page.getCmsPage().getName() != null && !page.getCmsPage().getName().trim().equals("")) {
			page.setOwner( JSFUtil.getLoggedInUser() );
			if( page.getCmsTemplate() == null ) {
				page.setTemplate( (TopLevelTemplate) new BeanDao(TopLevelTemplate.class).get(0), true, true );
			}
			beanSaved = page.saveDetails( JSFUtil.getLoggedInUser() );

			if( beanSaved ) {
				addCmsPageRevisionToUnusedMenu(page);
			}
		} else {
			JSFUtil.addMessageForError("You must enter a name to create a new page.");
		}
		return beanSaved;
	}

	public static void addCmsPageRevisionToUnusedMenu( CmsPageRevision cmsPageRevision ) {
		MenuNode node = new MenuNode();
		node.setCmsPage( cmsPageRevision.getCmsPage() );
		//if we deleted one of unused menu's children this menu wont be clean and will cause the save to die
		MenuNode parentNode = new BeanDao(MenuNode.class).get(((CmsWebsite) Website.getCurrentWebsiteFromTabSession()).getUnusedMenu().getId());
		parentNode = parentNode.getSaveableBean();
		node.setParent( parentNode );
		parentNode.addChild( node );
		node.saveDetails();
		parentNode.saveDetails();
	}

	public void saveAsDraft() {
		CmsPageRevision page = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		if( page.isDraft() ) {
			page.saveDetails();
		} else {
			CmsPageRevision newPage = page.getDraftCopy();
			newPage.setDraft( true );
			newPage.setCurrent( false );
			newPage.saveDetails();
			newPage.addToScope();
		}
	}

	public void commitDraft() {
		CmsPageRevision page = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		page.commit();
	}

	public SystemUser getNewAuthor() {
		return newAuthor;
	}

	public void setNewAuthor( SystemUser newAuthor ) {
		this.newAuthor = newAuthor;
	}

	public void setCphCacher(ContentPlaceholderCacher cphCacher) {
		this.cphCacher = cphCacher;
	}

	public ContentPlaceholderCacher getCphCacher() {
		return cphCacher;
	}

	@Override
	public String deleteBean() {
		CmsPageRevision cmsPageRevision = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		if( cmsPageRevision.isCurrent() ) {
			if( cmsPageRevision.getCmsPage() != null ) {
				BeanDao menuNodeDao = new BeanDao( MenuNode.class );
				menuNodeDao.addWhereCriteria( "cmsPage.id = " + cmsPageRevision.getCmsPage().getId() );
				MenuNode menuNode = (MenuNode) menuNodeDao.getFirstBeanResult();
				if (menuNode.getParent() != null) {
					menuNode.getParent().getChildren().remove( menuNode );
					menuNode.getParent().saveDetails();
				}
				//if we hard delete a node that has children, children are 'lost forever'!
				menuNode.delete();

				MenuNode unusedMenu = ((CmsWebsite) Website.getCurrentWebsiteFromTabSession()).getUnusedMenu();
				for (MenuNode childMenuNode : menuNode.getChildren()) {
					childMenuNode.setParent( unusedMenu );
					childMenuNode.saveDetails();
				}
				//deal with aliases
				BeanDao aliasDao = new BeanDao(CmsPageAlias.class);
				aliasDao.setWhereCriteria("bean.cmsPage.id=" + cmsPageRevision.getCmsPage().getId());
				List<CmsPageAlias> aliases = aliasDao.getAll();
				for (CmsPageAlias cmsPageAlias : aliases) {
					BeanDao menuDao = new BeanDao( MenuNode.class );
					menuDao.addWhereCriteria( "cmsPage.id = " + cmsPageAlias.getId() );
					MenuNode menu = (MenuNode) menuDao.getFirstBeanResult();
					if (menu.getParent() != null && menu.getParent().getChildren() != null) {
						menu.getParent().getChildren().remove( menu );
						menu.getParent().saveDetails();
					}
//					HibernateUtil.startNewTransaction();
					menu.hardDelete();
					cmsPageAlias.hardDelete();
				}
				//finish up
				cmsPageRevision.getCmsPage().delete();
			}
		}
		super.deleteBean();
		return null;
	}

	@Override
	public String reinstateBean() {
		CmsPageRevision cmsPageRevision = ((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		reinstateCmsPage(cmsPageRevision);
		//TODO: Generate view file ?
		return super.reinstateBean();
	}
	
	public void formSubmitted() {
		JSFUtil.getViewMap().get( "myArray" );
	}
	
	public static void reinstateCmsPage(CmsPageRevision cmsPageRevision) {
		if ( cmsPageRevision != null && cmsPageRevision.isCurrent() ) {
			cmsPageRevision.getCmsPage().reinstate();
			cmsPageRevision.reinstate();
			addCmsPageRevisionToUnusedMenu( cmsPageRevision );
		} else {
			JSFUtil.addMessage("The cms page is not valid and current, it has not been reinstated. (There is probably a more current version)");
		}
	}

	public void setSelectedCmsAtom(CmsAtom selectedCmsAtom) {
		this.selectedCmsAtom = selectedCmsAtom;
	}

	public CmsAtom getSelectedCmsAtom() {
		return selectedCmsAtom;
	}

	public void setSelectedTemplate(CmsTemplate selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	public CmsTemplate getSelectedTemplate() {
		return selectedTemplate;
	}

	public String getNewSnippetName() {
		return newSnippetName;
	}

	public void setNewSnippetName(String newSnippetName) {
		this.newSnippetName = newSnippetName;
	}
}
