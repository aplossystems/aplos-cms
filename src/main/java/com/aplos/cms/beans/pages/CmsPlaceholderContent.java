package com.aplos.cms.beans.pages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;

@Entity
@PluralDisplayName(name="CMS placeholder content")
@Cache
public class CmsPlaceholderContent extends AplosBean implements PlaceholderContent {
	private static final long serialVersionUID = 6088481461821201131L;
	private String placeholderName;
	@Column(columnDefinition="LONGTEXT")
	private String content;
	private ContentPlaceholderType cphType = ContentPlaceholderType.EDITOR;
	private static Pattern regexElPattern = Pattern.compile( "#(%7B|\\{)( *)request\\.contextPath( *)(%7D|\\})" );

	public CmsPlaceholderContent() {}

	public CmsPlaceholderContent getCopy() {
		CmsPlaceholderContent newCmsPlaceHolderContent = new CmsPlaceholderContent();
		newCmsPlaceHolderContent.setContent( content );
		newCmsPlaceHolderContent.setCphType( cphType );
		return newCmsPlaceHolderContent;
	}

	public CmsPlaceholderContent( String content ) {
		setContent( content );
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}

	/**
	 * This is required as the ckEditor will evaluate the images in the content area,
	 * any src beginning with a # will send a request to the server appending the src
	 * to the end of the Url, thus reloading the page and destroying the viewState and
	 * next action on the page will throw an error.
	 * @return
	 */
	public String getRefinedContent() {
		removeRequestContextPathFromContent();
		return getContent();
	}

	public void removeRequestContextPathFromContent() {
		String content = getContent();
		if (content != null) { //new page
			Matcher m = regexElPattern.matcher( content );
			if( m.find() ) {
				setContent( content.replaceAll( regexElPattern.toString(), "" ) );
				JSFUtil.addMessage( "#{ request.contextPath } is not allowed in a ckEditor and has been removed" );
			}
		}
	}

	public void setRefinedContent( String refinedContent ) {
		setContent( refinedContent );
		removeRequestContextPathFromContent();
	}

	@Override
	public ContentPlaceholderType getCphType() {
		return cphType;
	}

	public void setCphType( ContentPlaceholderType cphType ) {
		this.cphType = cphType;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		content = XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(content);
		super.saveBean(currentUser);
	}

	public String getPlaceholderName() {
		return placeholderName;
	}

	public void setPlaceholderName(String placeholderName) {
		this.placeholderName = placeholderName;
	}
}
