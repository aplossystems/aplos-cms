package com.aplos.cms.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import com.aplos.cms.CmsMenuCreator;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.beans.pages.CmsPageLink;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.JSFUtil;


@FacesComponent("com.aplos.cms.component.FrontendCmsMenu")
public class FrontendCmsMenu extends UIComponentBase {
	public static final String COMPONENT_TYPE = "com.aplos.core.component.FrontendCmsMenu";
	public static final String MENU_NODE_ID = "menuNodeId";
	public static final String INSERT_DIVS = "insertDivs";
	private static CmsMenuCreator cmsMenuCreator;

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}

	@Override
	public void encodeBegin(FacesContext facesContext) throws IOException {
		super.encodeBegin( facesContext );
		try {
			Integer menuNodeId = Integer.parseInt((String) getAttributes().get( MENU_NODE_ID ));
			HashMap<Integer,String> generatedMenuMap = new HashMap<Integer,String>();
			/* 
			 * The problem with caching the menu is that the active menu doesn't update.
			 */
//			generatedMenuMap = (HashMap<Integer,String>) JSFUtil.getFromTabSession( CmsAppConstants.FRONT_END_GENERATED_MENU_MAP );
//			if( generatedMenuMap == null ) {
//				generatedMenuMap = new HashMap<Integer,String>();
//				JSFUtil.getTabSession().put( CmsAppConstants.FRONT_END_GENERATED_MENU_MAP, generatedMenuMap);
//			}
			String generatedMenu = null;
//			if( !CommonUtil.isNullOrEmpty( generatedMenuMap.get( menuNodeId ) ) && JSFUtil.getCurrentUser() == null ) {
//				generatedMenu = (String) generatedMenuMap.get( menuNodeId );
//			} else {
				MenuNode menuNode = (MenuNode) new SiteBeanDao( MenuNode.class ).get( menuNodeId );
//				menuNode.hibernateInitialise(true);
				CmsPageRevision cmsPageRevision = JSFUtil.getBeanFromScope(CmsPageRevision.class);
				if (menuNode != null && cmsPageRevision != null) {
					HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
					generatedMenu = generateMenuCode( new ArrayList<MenuNode>(menuNode.getChildren()), cmsPageRevision, request.getContextPath(), cmsPageRevision.getCmsPage().getMapping() );
					generatedMenuMap.put( menuNodeId, generatedMenu);
				}
//			}

			if( generatedMenu != null ) {
				ResponseWriter out = facesContext.getResponseWriter();
				out.write(generatedMenu);
				out.close();
			}
		} catch (NumberFormatException nfe) {
			/* do nothing, be quiet */
		}
	}

	/* These methods previously lived in ContentPage */

	public String generateMenuCode(List<MenuNode> nodes, CmsPageRevision page, String contextRoot, String currentPageMapping ) {
		if( cmsMenuCreator == null ) {
			cmsMenuCreator = CmsModule.getCmsMenuCreator();
		}
		StringBuffer menucode = new StringBuffer();

		cmsMenuCreator.appendNavTagOpen( menucode );
		cmsMenuCreator.appendMenuTagOpen( menucode );
		for (MenuNode node : nodes) {
			if ( node != null && node.isActive() && node.getCmsPage() != null && //make sure we have a valid, active node
				node.getCmsPage().getMapping() != null &&
				node.getCmsPage().getStatus() != null &&
				node.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED) ) {
				String nodeMapping = getNodeMapping(contextRoot, node);
				String liMapping = nodeMapping.replaceAll("/|\\.aplos|\\.", "");
				boolean isSelected = isMenuNavigationActive(nodeMapping,currentPageMapping); 
				
				if ( isSelected ) {
					cmsMenuCreator.appendSelectedMenuItemTagOpen( menucode, node, liMapping );
				} else {
					cmsMenuCreator.appendMenuItemTagOpen( menucode, node, liMapping );
				}

				cmsMenuCreator.appendLinkTagOpen( menucode, node, contextRoot, nodeMapping, currentPageMapping, isSelected );
				if (FrontendCmsMenu.nodeHasChild(node)) {
					menucode.append( generateSubmenuCode(node, contextRoot, currentPageMapping ));
				} else {
					menucode.append( "</a>" );
				}
				menucode.append( "</li>" );
			}
		}
		menucode.append("</ul>");
		cmsMenuCreator.appendNavTagClose( menucode );
		return menucode.toString();
	}

	private String getNodeMapping(String contextRoot, MenuNode node) {
		StringBuffer nodeMapping = new StringBuffer();
		if (node.getCmsPage() != null && node.getCmsPage().getMapping() != null) {
			nodeMapping.append( node.getCmsPage().getMapping() );
		}
		
		String nodeMappingStr;
		if( !(node.getCmsPage() instanceof CmsPageLink) ) {
			if (nodeMapping.charAt(0) != '/' ) {
				nodeMapping.insert( 0, "/" );
			}
			nodeMapping.append( ".aplos" );
			nodeMappingStr = nodeMapping.toString();
		} else {
			nodeMappingStr = nodeMapping.toString();
			nodeMappingStr = nodeMappingStr.replace( "#{request.contextPath}", contextRoot );
		}
		
		return nodeMappingStr;
	}

	public static boolean nodeHasChild(MenuNode node) {
		return node.getChildren().size() > 0 || (node.getCmsPage() != null && node.getCmsPage() instanceof CmsPageGenerator && ((CmsPageGenerator)node.getCmsPage()).getGeneratorMenuItems().size() > 0);
	}

	private int countChildNodes(MenuNode node, boolean nodeIsTopLevel) {
		int childNodes = 0;
		if (node.getCmsPage() != null && node.getCmsPage() instanceof CmsPageGenerator) {
			childNodes += ((CmsPageGenerator)node.getCmsPage()).getGeneratorMenuItems().size();
		} else {
			if (!nodeIsTopLevel && node != null && node.isActive() && node.getCmsPage() != null && node.getCmsPage().getStatus().equals(CmsPageRevision.PageStatus.PUBLISHED)) {
				childNodes++;
			}
			for (MenuNode childNode : node.getChildren()){
				if (childNode != null) {
//					HibernateUtil.initialise(childNode, true);
					childNodes += countChildNodes(childNode, false);
				}
			}
		}
		return childNodes;
	}

	private int countDirectChildNodes(MenuNode node) {
		int childNodes = 0;
		if (node.getCmsPage() != null && node.getCmsPage() instanceof CmsPageGenerator) {
			childNodes += ((CmsPageGenerator)node.getCmsPage()).getGeneratorMenuItems().size();
		} else {
			childNodes += node.getChildren().size();
		}
		return childNodes;
	}

	private List<MenuNode> getDirectChildNodes(MenuNode node) {
		List<MenuNode> childNodes = new ArrayList<MenuNode>();
		if (node.getCmsPage() != null && node.getCmsPage() instanceof CmsPageGenerator) {
			CmsPageGenerator pageGen = (CmsPageGenerator) node.getCmsPage();
			String categoryMapping = "";
			if (node.getParent() != null && node.getParent().getCmsPage() != null) {
				categoryMapping += node.getParent().getCmsPage().getMapping() + "/";
			}
			for (GeneratorMenuItem menuItem : pageGen.getGeneratorMenuItems()) {
				if (menuItem != null) {
					CmsPage virtualPage = new CmsPage();
					virtualPage.setName(menuItem.getName());
					virtualPage.setMapping(categoryMapping + menuItem.getGeneratorMenuItemMapping());
					MenuNode virtualNode = new MenuNode(node.getParentWebsite(), node, virtualPage);
					virtualNode.setName(menuItem.getName());
					childNodes.add(virtualNode);
				}
			}
		} else {
			childNodes.addAll(node.getChildren());
		}
		return childNodes;
	}

	/**
	 * Decides whether our menu node is on the path to our current page
	 * which allows us to breadcrumb-highlight through a dropdown menu system
	 * and to keep the right nav tab selected when navigating product and
	 * category sub pages for ecommerce
	 */
	private boolean isMenuNavigationActive(String nodeMapping, String currentPageMapping) {
		if (nodeMapping.equals("")) {
			return false;
		}
		String categoryMapping = JSFUtil.getAplosContextOriginalUrl().replaceAll( ".aplos", "" );
		categoryMapping = categoryMapping.replaceFirst( JSFUtil.getContextPath(), "" );

		int firstMappingCharacterIndex = categoryMapping.lastIndexOf('/');
		if( firstMappingCharacterIndex == -1 ) {
			return false;
		}
		categoryMapping = categoryMapping.substring(0, firstMappingCharacterIndex); // dont take the current page's mapping

		if (categoryMapping.startsWith("/")) {
			categoryMapping = categoryMapping.substring(1);
		}

		if (currentPageMapping.startsWith("/")) {
			currentPageMapping = currentPageMapping.substring(1);
		}

		if (nodeMapping.startsWith("/")) {
			nodeMapping = nodeMapping.substring(1);
		}

		//as an example we may have 'products/menswear/shirts' here or 'category/menswear'

		String[] categoryMappingParts = categoryMapping.split("/");
		if (categoryMappingParts.length > 1) { //if its in multiple parts, we could have a category
			for (int i=1; i < categoryMappingParts.length; i++) {
				if (categoryMappingParts[i].equals(nodeMapping)) {
					return true;
				}
			}
		}
		
		if( nodeMapping.equals( ".aplos" ) ) {
			nodeMapping = "";
		}

		return nodeMapping.equals( currentPageMapping );

	}

	protected String generateSingleMenuItem(MenuNode menuNode, String currentPageMapping, boolean isHeading, String contextRoot) {
		StringBuffer menucode = new StringBuffer();
		String nodeMapping = getNodeMapping(contextRoot,menuNode);
		if ( isMenuNavigationActive(nodeMapping, currentPageMapping) ) {
			menucode.append( "<li class='aplos-active selected'>" );
		} else {
			menucode.append( "<li>" );
		}
		if (isHeading) {
			menucode.append( "<strong>" );
		}
		if (menuNode.getCmsPage() != null) {
			boolean isSelected = isMenuNavigationActive(nodeMapping,currentPageMapping); 
			cmsMenuCreator.appendLinkTagOpenSub( menucode, menuNode, contextRoot, nodeMapping, currentPageMapping, isSelected );
		} else if (menuNode.getName() != null && !menuNode.getName().equals("")) {
			menucode.append( menuNode.getName() );
		} else {
			//we've found a spacer
			menucode.append( "<div class='aplos-menu-spacer'>&#160;</div>" ); //without this, spacers don't work
		}
		if (menuNode.getCmsPage() != null) {
			menucode.append( "</a>" );
		}
		if (isHeading) {
			menucode.append( "</strong>" );
		}
		menucode.append( "</li>" );
		return menucode.toString();
	}

	private String generateSubmenuCode(MenuNode parentNode, String contextRoot, String currentPageMapping ) {
		StringBuffer menucode = new StringBuffer();
		//this is for IE support
		menucode.append( "<!--[if gte IE 7]><!--></a><!--<![endif]--><!--[if lte IE 6]><table><tr><td><![endif]-->" );
		int childNodeSize = countChildNodes(parentNode, true);
		int nodeSize = childNodeSize;
		int columns = 0;
		int numberOfRowsInColumn = parentNode.getCmsPage().getNumberOfRowsInColumn();
		while (nodeSize > 0) {
			columns++;
			nodeSize = nodeSize-numberOfRowsInColumn;
		}
		if (columns == 0) {
			columns=1;
		}
		//135 matches size in modern css, plus 10 for padding
		int calculatedWidth = (145 * columns);
		boolean insertDivs = ComponentUtil.determineBooleanAttributeValue( this, INSERT_DIVS, true);
		if( insertDivs ) {
			menucode.append( "<div style='width:" + calculatedWidth + "px;' class='aplos-dropdown-content'>" );
		}
		int nodesParsed = 0;
		for (MenuNode childNode : getDirectChildNodes(parentNode)) {
			// Make sure it's initialised so that instanceof works correctly
//			HibernateUtil.initialise(childNode, true);
			if (childNode != null) {
				//begin a new column every #dropdownColumnLength nodes
				//or if the next node has enough children that it would overflow #dropdownColumnLength
				if ( (nodesParsed % numberOfRowsInColumn) == 0 || (nodesParsed > 3 && (nodesParsed + countDirectChildNodes(childNode)) > numberOfRowsInColumn)) {
					if (nodesParsed != 0) {
						nodesParsed=0;
						if( insertDivs ) {
							menucode.append( "</ul></div>" );
						} else {
							menucode.append( "</ul>" );
						}
					}

					if( insertDivs ) {
						menucode.append( "<div class='aplos-dropdown-column'>" );
					}
					menucode.append( "<ul class='aplos-navigation aplos-sub-navigation'>" );
				}
				if (countDirectChildNodes(childNode) > 0) {
					if (childNode.getCmsPage() == null || !(childNode.getCmsPage() instanceof CmsPageGenerator)) {
						menucode.append( generateSingleMenuItem(childNode, currentPageMapping, true, contextRoot) );
						nodesParsed++;
					}
					for (MenuNode childChildNode : getDirectChildNodes(childNode)) {
						if (nodesParsed != 0 && nodesParsed % numberOfRowsInColumn == 0) {
							nodesParsed=0;
							if( insertDivs ) {
								menucode.append( "</ul></div>" );
								menucode.append( "<div class='aplos-dropdown-column'>" );
							} else {
								menucode.append( "</ul>" );
							}

							menucode.append( "<ul class='aplos-navigation aplos-sub-navigation'>" );
						}
						menucode.append( generateSingleMenuItem(childChildNode, currentPageMapping, false, contextRoot) );
						nodesParsed++;
					}
				} else {
					menucode.append( generateSingleMenuItem(childNode, currentPageMapping, false, contextRoot) );
					nodesParsed++;
				}
			}
		}
		//we know we definitely had child nodes so we would have opened a column
		if( insertDivs ) {
			menucode.append( "</ul></div>" );
			menucode.append( "</div>" );
		} else {
			menucode.append( "</ul>" );
		}
		//end with with IE support
		menucode.append( "<!--[if lte IE 6]></td></tr></table></a><![endif]-->" );
		return menucode.toString();
	}
}
