package com.aplos.cms.beans.pages;

import java.util.Date;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
@Cache
public class JavascriptResource extends AplosSiteBean {
	private static final long serialVersionUID = 6368862923664166237L;
	private String name;
	private String description;
	private int version = 0;

	@Column(columnDefinition="LONGTEXT")
	private String content;

	public String generateXhtml( String contextRoot ) {
		String contextRootWithoutSlash = contextRoot.replace( "\\", "" ).replace( "/", "" );
     	StringBuffer strBuf = new StringBuffer();
     	//for ease of debugging
     	String descriptionString = "";
     	if (this.getDescription() != null && !this.getDescription().equals("")) {
     		descriptionString = " (" + this.getDescription() + ")";
     	}
		strBuf.append( "// File: " + this.getName() + descriptionString + "\n" );
		strBuf.append( "// Last Generated: " + FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(), new Date()) + "\n" );
     	strBuf.append( CommonUtil.includeContextRootInPathsForCss(getContent(), contextRootWithoutSlash) );
		return strBuf.toString();
	}

	public void writeViewToFile() {
		//this will only work from backend, not from filters
		writeViewToFile(JSFUtil.getContextPath());
	}

	public void writeViewToFile(String contextPath) {
		CmsModule.writeViewToFile(generateXhtml(contextPath),CmsWorkingDirectory.JS_VIEW_FILES,getId(),"js");
	}

	@Override
	public void saveBean( SystemUser currentUser ) {
		setVersion(getVersion() + 1);
		super.saveBean(currentUser);
		if (this.isActive()) {
			writeViewToFile();
		}
	}

	@Override
	public String getDisplayName() {
		return (name == null ? super.getDisplayName() : name);
	}

	public void setContent( String content ) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
