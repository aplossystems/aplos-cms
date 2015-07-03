package com.aplos.cms.backingpage;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.cms.backingpage.pages.CmsPageRevisionEditPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CmsPageRevision.class)
public class DeletedPageRevisionListPage extends ListPage {

	private static final long serialVersionUID = -7047528872787424687L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new DeletedPageRevisionLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class DeletedPageRevisionLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 552231363351744792L;

		public DeletedPageRevisionLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getDataTableState().setShowingDeleted(true);
			getDataTableState().setShowingNewBtn(false);
			getBeanDao().setIsReturningActiveBeans(false);
		}

		@Override
		public void reinstateBean() {
			AplosBean reinstateBean = JSFUtil.getTableBean();
			if (reinstateBean != null) {
				
				CmsPageRevision cmsPageRevision = (CmsPageRevision) new BeanDao( CmsPageRevision.class ).get( reinstateBean.getId() );
				CmsPageRevisionEditPage.reinstateCmsPage( cmsPageRevision );

			}
			
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			super.getBeanDao().setOrderBy("bean.dateInactivated DESC");
			super.getBeanDao().setWhereCriteria("bean.parentWebsite.id=" + Website.getCurrentWebsiteFromTabSession().getId());
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}


