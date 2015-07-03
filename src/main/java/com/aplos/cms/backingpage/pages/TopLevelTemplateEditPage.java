package com.aplos.cms.backingpage.pages;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.SortOrder;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.pages.CmsAtomPassThrough;
import com.aplos.cms.beans.pages.InnerTemplate;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=TopLevelTemplate.class)
public class TopLevelTemplateEditPage extends EditPage {
	private static final long serialVersionUID = -3696732935771340497L;
	private DataTableState dataTableState;
	private CmsAtom selectedCmsAtomForPassThrough;

	public TopLevelTemplateEditPage() {
		dataTableState = CommonUtil.createDataTableState( this, TopLevelTemplateCmsPageLdm.class );
//		HibernateUtil.initialise(dataTableState, true);
		TopLevelTemplateCmsPageLdm topLevelTemplateCmsPageLdm = new TopLevelTemplateCmsPageLdm( dataTableState, getListBeanDao() );
		dataTableState.setLazyDataModel( topLevelTemplateCmsPageLdm );
	}

	@Override
	public boolean responsePageLoad() {
		return super.responsePageLoad();
	}

	public void addCmsAtomPassThrough() {
		((TopLevelTemplate) resolveAssociatedBean()).getCmsAtomPassThroughList().add( new CmsAtomPassThrough( getSelectedCmsAtomForPassThrough() ) );
	}

	public void removeCmsAtomPassThrough() {
		CmsAtomPassThrough cmsAtomPassThrough = (CmsAtomPassThrough) JSFUtil.getRequest().getAttribute( "cmsAtomPassThrough" );
		((TopLevelTemplate) resolveAssociatedBean()).getCmsAtomPassThroughList().remove( cmsAtomPassThrough );
	}

	public SelectItem[] getAvailableCmsAtomSelectItemBeans() {
		CmsWebsite cmsWebsite = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
		return AplosBean.getSelectItemBeans( cmsWebsite.getAvailableCmsAtomList() );
	}

	public void setSelectedCmsAtomForPassThrough(
			CmsAtom selectedCmsAtomForPassThrough) {
		this.selectedCmsAtomForPassThrough = selectedCmsAtomForPassThrough;
	}

	public CmsAtom getSelectedCmsAtomForPassThrough() {
		return selectedCmsAtomForPassThrough;
	}

	public DataTableState getDataTableState() {
		return dataTableState;
	}

	public void setDataTableState(DataTableState dataTableState) {
		this.dataTableState = dataTableState;
	}

	public BeanDao getListBeanDao() {
		return new BeanDao( InnerTemplate.class );
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new TopLevelTemplateCmsPageLdm(dataTableState, getListBeanDao());
	}

	public class TopLevelTemplateCmsPageLdm extends AplosLazyDataModel {

		private static final long serialVersionUID = -32751833138105854L;

		public TopLevelTemplateCmsPageLdm(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			//getBeanDao().setBeanClass(InnerTemplate.class);
			//getBeanDao().setOptimisedSearch(true);
			//getBeanDao().setSelectCriteria( "bean.id, bean.name, bean.active" );
		}

		@Override
		public void goToNew() {
			super.goToNew();
			InnerTemplate innerTemplate = JSFUtil.getBeanFromScope( InnerTemplate.class );
			innerTemplate.setTemplate( (TopLevelTemplate) TopLevelTemplateEditPage.this.resolveAssociatedBean(), false, true );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			getBeanDao().addWhereCriteria( "bean.cmsTemplate.id = " + TopLevelTemplateEditPage.this.resolveAssociatedBean().getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}
