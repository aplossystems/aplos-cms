package com.aplos.cms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aplos.cms.beans.pages.CssResource;
import com.aplos.cms.beans.pages.JavascriptResource;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.CommonUtil;

public class ContentServlet extends HttpServlet {
	private static final long serialVersionUID = -7139691671957153877L;

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String uri = request.getRequestURI();
		//String contextRoot = request.getContextPath();

//		CommonUtil.timeTrial( uri, request.getSession().getServletContext() );

		if (uri.contains( "/css/" )) {
			Pattern pattern = Pattern.compile("/([0-9]*)\\.css");
			Matcher m = pattern.matcher( uri );
			if (m.find()) {
				String id = m.group( 1 );
				String cssContent = ((CssResource)new BeanDao(CssResource.class).get(Long.parseLong( id ))).getContent();
		     	String contextRootWithoutSlash = request.getContextPath().replace( "\\", "" ).replace( "/", "" );
		     	String newCssContent = CommonUtil.includeContextRootInPathsForCss(cssContent, contextRootWithoutSlash);
		     	response.setContentType( "text/css" );
				out.println(newCssContent);
			}
		} else if (uri.contains( "/js/" )) {
			Pattern pattern = Pattern.compile("/([0-9]*)\\.js");
			Matcher m = pattern.matcher( uri );
			if (m.find()) {
				String id = m.group( 1 );
				String javascriptContent = ((JavascriptResource)new BeanDao(JavascriptResource.class).get(Long.parseLong( id ))).getContent();
//		     	String contextRootWithoutSlash = request.getContextPath().replace( "\\", "" ).replace( "/", "" );
//		     	String newJavascriptContent = CommonUtil.includeContextRootInPathsForJavascript(javascriptContent, contextRootWithoutSlash);
		     	response.setContentType( "text/javascript" );
				out.println(javascriptContent);
			}
		}
//		CommonUtil.timeTrial( "Finish ContentServlet", request.getSession().getServletContext() );
	}

	/* This code may allow us to access the FacesContext and was found at
	 * http://ocpsoft.com/java/jsf-java/jsf-20-extension-development-accessing-facescontext-in-a-filter/
	 * I imagine the missing piece is that the viewRoot determines which scoped variables
	 * will be applied (which is what we are after).
	 */

	//  If this is finding the contextListener why can't I find the rest of the variables in the session and the request
	//  AplosContextListener contextListener = (AplosContextListener)( ( (HttpSession)request.getSession() ).getServletContext() ).getAttribute( AplosScopedBindings.CONTEXT_LISTENER );

//	public FacesContext getFacesContext(final ServletRequest request, final ServletResponse response)
//    {
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        if (facesContext != null)
//        {
//            return facesContext;
//        }
//
//        FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
//                .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
//        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
//                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
//        Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
//
//        ServletContext servletContext = ((HttpServletRequest) request).getSession().getServletContext();
//        facesContext = contextFactory.getFacesContext(servletContext, request, response, lifecycle);
//        InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);
//        if (null == facesContext.getViewRoot())
//        {
//            facesContext.setViewRoot(new UIViewRoot());
//        }
//
//        return facesContext;
//    }
//
//	private abstract static class InnerFacesContext extends FacesContext
//    {
//        protected static void setFacesContextAsCurrentInstance(final FacesContext facesContext)
//        {
//            FacesContext.setCurrentInstance(facesContext);
//        }
//    }


	@Override
	protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

	}


}
