package com.aplos.cms;

import java.util.HashMap;
import java.util.Map;

import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.enums.ContentPlaceholderType;
import com.aplos.cms.interfaces.PlaceholderContent;

public class ContentPlaceholderCacher {

	private HashMap<String, HashMap<ContentPlaceholderType, CmsPlaceholderContent>> cphCacheMap = new HashMap<String, HashMap<ContentPlaceholderType, CmsPlaceholderContent>>();

	/**
	 * 	This method changes the PlaceholderContent stored in the map.  It uses a cache which remembers
	 *  the choices of a previous PlaceholderContent for the same name map key.  This means that
	 *  when a user moves between the ContentPlaceholderTypes the information that they have entered
	 *  for one type isn't lost.
	 * @param name
	 * @param cphType
	 */
	public void setPlaceholderContentCphType( Map<String, PlaceholderContent> phcMap, String name, ContentPlaceholderType cphType ) {
		CmsPlaceholderContent oldCpc = (CmsPlaceholderContent) phcMap.get( name );
		CmsPlaceholderContent cmsPlaceholderContent;
		if( cphCacheMap.get( name ) == null ) {
			cphCacheMap.put( name ,new HashMap<ContentPlaceholderType, CmsPlaceholderContent>()  );
		}
		cphCacheMap.get( name ).put( oldCpc.getCphType(), oldCpc );
		boolean isTextArea = false;

		//  If the user is changing between an editor and code they don't want the information to use the cache but the information already in the
		//  current PlaceholderContent
		if( (cphType.equals( ContentPlaceholderType.EDITOR ) && oldCpc.getCphType().equals( ContentPlaceholderType.CODE )) ||
				(cphType.equals( ContentPlaceholderType.CODE ) && oldCpc.getCphType().equals( ContentPlaceholderType.EDITOR )) ) {
			isTextArea = true;
		}

		if( cphCacheMap.get( name ).get( cphType ) != null && !isTextArea ) {
			cmsPlaceholderContent = cphCacheMap.get( name ).get( cphType );
			phcMap.put( name, cmsPlaceholderContent );
		} else {
			cmsPlaceholderContent = oldCpc;
			cmsPlaceholderContent.setCphType(cphType);
		}
	}
}
