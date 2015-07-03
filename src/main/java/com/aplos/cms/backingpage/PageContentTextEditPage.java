package com.aplos.cms.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.backingpage.pages.CmsPageRevisionEditPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.UserCmsModule;
import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.common.aql.BeanDao;

@ManagedBean
@SessionScoped
public class PageContentTextEditPage extends CmsPageRevisionEditPage {
	private static final long serialVersionUID = 4574913675628604777L;

	private CmsPageRevision cmsPageRevision;
	private String cphName;
	//private ContentPlaceholderCacher cphCacher = new ContentPlaceholderCacher();

	public void setPageById(String id) {
		long lid = Long.parseLong( id );
		cmsPageRevision = (CmsPageRevision) new BeanDao( CmsPageRevision.class ).get( lid );
		cmsPageRevision.addToScope();
	}

	public void setMappedModule(UserCmsModule cmsum) {
//		if( getCmsPageRevision().getPlaceholderContentMap().get( getCphName() ).getCphType().equals( ContentPlaceholderType.USER_MODULE ) ) {
//			getCmsPageRevision().getPlaceholderContentMap().put( getCphName() , cmsum);
//		}
	}

//	public SelectItem[] getUserCmsModuleSelectItems() {
//		return AplosAbstractBean.getSelectItemBeans(HibernateUtil.getCurrentSession().createQuery( "FROM " + UserCmsModule.class.getSimpleName() + " WHERE active=true" ).list());
//	}

	public ContentPlaceholderType getCphType() {
		return cmsPageRevision.getPlaceholderContent( getCphName() ).getCphType();
	}

	public void setCphType( ContentPlaceholderType cphType ) {
//		if( !cphType.equals( cmsPageRevision.getPlaceholderContentMap().get( getCphName() ).getCphType() ) ) {
//			cphCacher.setPlaceholderContentCphType( cmsPageRevision.getPlaceholderContentMap(), getCphName(), cphType );
//		}
	}

	public PlaceholderContent getPlaceholderContent() {
		return cmsPageRevision.getPlaceholderContent( getCphName() );
	}

	public CmsPageRevision getCmsPageRevision() {
		return cmsPageRevision;
	}

	public void setCphName(String cphName) {
		this.cphName = cphName;
	}

	public String getCphName() {
		return cphName;
	}

	@Override
	public void applyBtnAction() {
		cmsPageRevision.saveDetails();
	}


	@Override
	public void saveAsDraft() {
		super.saveAsDraft();
	}


}
