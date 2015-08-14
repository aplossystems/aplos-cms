package com.aplos.cms.backingpage.pages;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.primefaces.model.SortOrder;

import com.aplos.cms.ContentPlaceholderCacher;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsAtomPassThrough;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsTemplate;
import com.aplos.cms.beans.pages.InnerTemplate;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.aql.aqltables.unprocessed.UnprocessedAqlTable;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=InnerTemplate.class)
public class InnerTemplateEditPage extends EditPage {
	private static final long serialVersionUID = -1285062737144342632L;

	private ContentPlaceholderCacher cphCacher = new ContentPlaceholderCacher();
	private DataTableState dataTableState;
	private CmsAtom selectedCmsAtom;
	private CmsAtom selectedCmsAtomForPassThrough;
	private CmsTemplate selectedTopLevelTemplate;


	public InnerTemplateEditPage() {
		super();
		dataTableState = CommonUtil.createDataTableState( this, InnerTemplateCmsPageLdm.class );
		InnerTemplateCmsPageLdm innerTemplateCmsPageLdm = new InnerTemplateCmsPageLdm( dataTableState, getListBeanDao() );
		dataTableState.setLazyDataModel( innerTemplateCmsPageLdm );
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		InnerTemplate innerTemplate = (InnerTemplate) resolveAssociatedBean();
		if (innerTemplate != null) {
			innerTemplate.initModules( false, false, false );
			setSelectedTopLevelTemplate( innerTemplate.getCmsTemplate() ); 
		}
		return true;
	}

	public SelectItem[] getUserCmsModuleSelectItems() {
		return AplosAbstractBean.getSelectItemBeans( UserCmsModule.class );
	}

	public void addCmsAtomPassThrough() {
		if( getSelectedCmsAtomForPassThrough() != null ) {
			((InnerTemplate) resolveAssociatedBean()).getCmsAtomPassThroughList().add( new CmsAtomPassThrough( getSelectedCmsAtomForPassThrough() ) );
		} else {
			JSFUtil.addMessage( "Please select an atom that you would like to add");
		}
	}

	public void removeCmsAtomPassThrough() {
		CmsAtomPassThrough cmsAtomPassThrough = (CmsAtomPassThrough) JSFUtil.getRequest().getAttribute( "cmsAtomPassThrough" );
		((InnerTemplate) resolveAssociatedBean()).getCmsAtomPassThroughList().remove( cmsAtomPassThrough );
	}

	public void addCmsAtom() {
		CmsAtom newCmsAtom;
		if( getSelectedCmsAtom() != null ) {
			if( getSelectedCmsAtom() instanceof UnconfigurableDeveloperCmsAtom ) {
				newCmsAtom = getSelectedCmsAtom();
			} else {
				newCmsAtom = (CmsAtom) CommonUtil.getNewInstance( ApplicationUtil.getClass( selectedCmsAtom ), null );
			}
			((InnerTemplate) resolveAssociatedBean()).getCmsAtomList().add( newCmsAtom );
			if( newCmsAtom instanceof ConfigurableDeveloperCmsAtom ) {
				((ConfigurableDeveloperCmsAtom) newCmsAtom).initBackend();
			}
		} else {
			JSFUtil.addMessage( "Please select an atom that you would like to add");
		}
	}

	public void removeCmsAtom() {
		CmsAtom cmsAtom = (CmsAtom) JSFUtil.getRequest().getAttribute( "cmsAtom" );
		((InnerTemplate) resolveAssociatedBean()).getCmsAtomList().remove( cmsAtom );
	}

	public SelectItem[] getAvailableCmsAtomSelectItemBeans() {
		CmsWebsite cmsWebsite = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
		return AplosBean.getSelectItemBeans( cmsWebsite.getAvailableCmsAtomList() );
	}

	public void setCphCacher(ContentPlaceholderCacher cphCacher) {
		this.cphCacher = cphCacher;
	}

	public ContentPlaceholderCacher getCphCacher() {
		return cphCacher;
	}

	public void setSelectedCmsAtom(CmsAtom selectedCmsAtom) {
		this.selectedCmsAtom = selectedCmsAtom;
	}

	public CmsAtom getSelectedCmsAtom() {
		return selectedCmsAtom;
	}

	public void setSelectedCmsAtomForPassThrough(
			CmsAtom selectedCmsAtomForPassThrough) {
		this.selectedCmsAtomForPassThrough = selectedCmsAtomForPassThrough;
	}

	public CmsAtom getSelectedCmsAtomForPassThrough() {
		return selectedCmsAtomForPassThrough;
	}

	public void goToTopLevelTemplate() {
		InnerTemplate innerTemplate = (InnerTemplate)JSFUtil.getBeanFromScope(InnerTemplate.class);
		innerTemplate.getCmsTemplate().redirectToEditPage();
	}

	public void setSelectedTopLevelTemplate(CmsTemplate selectedTopLevelTemplate) {
		this.selectedTopLevelTemplate = selectedTopLevelTemplate;
	}

	public CmsTemplate getSelectedTopLevelTemplate() {
		return selectedTopLevelTemplate;
	}

	public void templateChanged() {
		InnerTemplate innerTemplate = (InnerTemplate) resolveAssociatedBean();
		@SuppressWarnings("unchecked")
		CmsTemplate loadedTemplate = (CmsTemplate) new BeanDao( (Class<? extends AplosAbstractBean>) selectedTopLevelTemplate.getClass() ).get( selectedTopLevelTemplate.getId() );
//		loadedTemplate.hibernateInitialise( true );
		innerTemplate.setTemplate( loadedTemplate, true, true );
		innerTemplate.initModules( false, false, false );
	}

	public void templateChanged(AjaxBehaviorEvent event) {
		templateChanged();
	}

	public SelectItem[] getTopLevelTemplateSelectItems() {
		BeanDao aqlBeanDao = new SiteBeanDao( TopLevelTemplate.class );
		aqlBeanDao.setSelectCriteria( "bean.id, bean.name, bean.active" );
		return TopLevelTemplate.getSelectItemBeans( aqlBeanDao.getAll(), true );
	}

	public DataTableState getDataTableState() {
		return dataTableState;
	}

	public void setDataTableState(DataTableState dataTableState) {
		this.dataTableState = dataTableState;
	}

	public BeanDao getListBeanDao() {
		return new BeanDao( CmsPage.class );
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new InnerTemplateCmsPageLdm(getDataTableState(), getListBeanDao());
	}

	public class InnerTemplateCmsPageLdm extends AplosLazyDataModel {

		private static final long serialVersionUID = -32751833138105854L;

		public InnerTemplateCmsPageLdm(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			UnprocessedAqlTable aqlTable = getBeanDao().addReverseJoinTable( CmsPageRevision.class, "cpr.cmsPage", "bean" );
			getBeanDao().setSelectCriteria( "bean.id, bean.name, bean.active" );
			setEditPageClass( CmsPageRevisionEditPage.class );
		}

		@Override
		public CmsPage selectBean() {
			CmsPage cmsPage = (CmsPage) super.selectBean();
			cmsPage.getLatestRevision().redirectToEditPage();
			return cmsPage;
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			InnerTemplate innerTemplate = JSFUtil.getBeanFromScope(InnerTemplate.class);
			getBeanDao().addWhereCriteria("cpr.cmsTemplate.id = " + innerTemplate.getId());
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}
}
