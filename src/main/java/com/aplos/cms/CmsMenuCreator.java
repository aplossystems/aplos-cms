package com.aplos.cms;

import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPageLink;
import com.aplos.cms.component.FrontendCmsMenu;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;

public class CmsMenuCreator {
	public void appendNavTagOpen( StringBuffer strBuf ) {
		strBuf.append("<nav class='aplos-nav'>");
	}
	public void appendNavTagClose( StringBuffer strBuf ) {
		strBuf.append("</nav>");
	}
	public void appendMenuTagOpen( StringBuffer strBuf ) {
		strBuf.append("<ul class='aplos-navigation nav sf-menu'>");
	}
	
	public void appendMenuItemTagOpen( StringBuffer strBuf, MenuNode node, String liMapping ) {
		if (FrontendCmsMenu.nodeHasChild(node)) {
			strBuf.append( "<li class='aplos-menu-" + liMapping + " aplos-has-submenu'>" );
		} else {
			strBuf.append( "<li class='aplos-menu-" + liMapping + "'>" );
		}
	}
	public void appendSelectedMenuItemTagOpen( StringBuffer strBuf, MenuNode node, String liMapping ) {
		if (FrontendCmsMenu.nodeHasChild(node)) {
			strBuf.append( "<li class='aplos-menu-" + liMapping + " aplos-has-submenu aplos-active selected'>" );
		} else {
			strBuf.append( "<li class='aplos-menu-" + liMapping + " aplos-active selected'>" );
		}
	}
	
	public void appendHref( StringBuffer strBuf, MenuNode node, String contextPath, String nodeMapping ) {
		String fullNodeMapping = nodeMapping;
		if( !(node.getCmsPage() instanceof CmsPageLink) ) {
			fullNodeMapping = contextPath + nodeMapping;
		}
		strBuf.append( fullNodeMapping );
		if( CommonConfiguration.getCommonConfiguration().isUsingWindowId() ) {
			strBuf.append( "?windowId=" ).append( JSFUtil.getWindowId() );
		}
	}
	
	public void appendLinkTagOpen( StringBuffer strBuf, MenuNode node, String contextPath, String nodeMapping, String currentPageMapping, boolean isSelected ) {
		strBuf.append( "<a href='" );
		appendHref( strBuf, node, contextPath, nodeMapping );
		strBuf.append( "'>" );
		strBuf.append( node.getCmsPage().getName().replace("&", "&amp;") );
	}
	
	public void appendLinkTagOpenSub( StringBuffer strBuf, MenuNode menuNode, String contextPath, String nodeMapping, String currentPageMapping, boolean isSelected ) {
		strBuf.append( "<a href='" );
		appendHref( strBuf, menuNode, contextPath, nodeMapping );
		//TODO: this is going to need updating again if we have any other type of 'page generator'
		if (menuNode.getIsNew()) {
			//if we are 'new' it actually means we are a generated 'virtual' node
			//which in turn means we were created by a page generator
			//which at the moment, means we are a category page as thats the only type of
			//page generator we have. The type param is only needed for urls which redirect
			//to the generic product list/view pages
			strBuf.append( "?type=category'>" );
		} else {
			strBuf.append( "'>" );
		}
		strBuf.append( menuNode.getCmsPage().getName().replace("&", "&amp;") );
	}
}
