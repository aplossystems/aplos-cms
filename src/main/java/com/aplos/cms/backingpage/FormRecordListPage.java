package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.customforms.Form;
import com.aplos.cms.beans.customforms.FormRecord;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=FormRecord.class)
public class FormRecordListPage extends ListPage {
	private static final long serialVersionUID = 8979904120919774161L;

	@Override
	public boolean responsePageLoad() {
		Form form = (Form) JSFUtil.getBeanFromScope( Form.class );
		getBeanDao().addWhereCriteria( "form.id=" + form.getId() ).setOrderBy( "position" );
		return true;
	}


}
