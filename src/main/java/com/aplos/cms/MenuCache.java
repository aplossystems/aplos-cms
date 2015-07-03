package com.aplos.cms;


public class MenuCache {
//	private static HashMap<Long, MenuNode> menuNodesById;
//
//	public static HashMap<Long, MenuNode> getMenuNodesById() {
//		if( menuNodesById == null ) {
//			menuNodesById = new HashMap<Long, MenuNode>();
//			List<MenuNode> menuNodeList = new AqlBeanDao( MenuNode.class ).setIsReturningActiveBeans(true).getAll();
//			for( int i = 0, n = menuNodeList.size(); i < n; i++ ) {
//				menuNodeList.get( i ).hibernateInitialise( true );
//				menuNodesById.put( menuNodeList.get( i ).getId(), menuNodeList.get( i ) );
//			}
//		}
//		return menuNodesById;
//	}
//
//	public static void addToMenu( Long parentNodeId, MenuNode node ) {
//		MenuNode parentNode = getMenuNodesById().get( parentNodeId );
//		// Keep both sides of the relationship consistent
//		node.setParent( parentNode );
//		parentNode.addChild( node );
//
//		// The save will cascade to page without calling saveDetails
//		// so we have a blank page with a blank mapping but with an
//		// id, which is perfect - for now...
//		node.aqlSaveDetails();
//		parentNode.aqlSaveDetails();
//		getMenuNodesById().put( node.getId(), node );
//	}
//
//	public static void moveNode( Long nodeId, Long newParentId, int listIndex ) {
//		MenuNode node = getMenuNodesById().get( nodeId );
//		MenuNode parentNode = getMenuNodesById().get( newParentId );
//		node.getParent().getChildren().remove( node );
//
//		parentNode.getChildren().add( listIndex, node );
//		node.setParent( parentNode );
//	}
}
