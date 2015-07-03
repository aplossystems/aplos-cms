package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
@Entity
public class Publication extends AplosBean {

	private static final long serialVersionUID = 8031910725271343311L;

	private String title;
	private String synopsis;
	@Column(columnDefinition="LONGTEXT")
	private String publicationBody;

	@Override
	public String getDisplayName() {
		return (getTitle() == null ? super.getDisplayName() : getTitle());
	}

	public void setPublicationBody(String publicationBody) {
		this.publicationBody = publicationBody;
	}

	public String getPublicationBody() {
		return publicationBody;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public List<Publication> getPublicationList() {
		@SuppressWarnings("unchecked")
		List<Publication> returnList = new BeanDao( Publication.class ).getAll();
		if (returnList == null || returnList.size() < 1) {
			returnList = new ArrayList<Publication>();
		}
		return returnList;
	}

	public static void readMore() {
		AplosBean bean = (AplosBean) JSFUtil.getRequest().getAttribute( "pub" );
//		AplosBean loadedBean = new AqlBeanDao( bean.getClass() ).get( bean.getId() );
//		HibernateUtil.initialise( loadedBean, true );
		bean.addToScope();
		JSFUtil.addToTabSession(CommonUtil.getBinding(Publication.class), bean);
		//TODO: this needs to be a CPR - need to check what atom it refers to and where its used
		JSFUtil.redirect( new CmsPageUrl("read-publication", null), true );
	}

}
