package com.aplos.cms.backingpage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@GlobalAccess
public class SitemapPage extends BackingPage {
	private static final long serialVersionUID = 6575681344761251670L;
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public boolean responsePageLoad() {
		boolean continueLoad = super.responsePageLoad();

		File file = new File( CommonWorkingDirectory.PROCESSED_RESOURCES_DIR.getDirectoryPath(true) + "sitemap.xml" );
    	if( !file.exists() || ApplicationUtil.getAplosContextListener().isDebugMode() ) {
    		CmsWebsite cmsWebsite = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
    		cmsWebsite.generateSiteMap(file);
    	}
    	
    	try {
    		HttpServletResponse response = JSFUtil.getResponse();
    		InputStream is = new FileInputStream(file);
		    String mimeType = Files.probeContentType(Paths.get(".xml"));
		    response.setContentType(mimeType);
		    response.setStatus(200);

		    // Copy the contents of the file to the output stream
		    byte[] buf = new byte[1024];
		    int count = 0;
		    while ((count = is.read(buf)) >= 0) {
		        response.getOutputStream().write(buf, 0, count);
		    }
		    response.getOutputStream().close();
		    JSFUtil.getFacesContext().responseComplete();
		    return false;
		} catch ( SocketException cae ) {
			logger.debug("Aborting transfer.  it happens, no problem.", cae);
		} catch (Exception e) {
			ApplicationUtil.handleError(e);
		}
    	
		return continueLoad;
	}
}
