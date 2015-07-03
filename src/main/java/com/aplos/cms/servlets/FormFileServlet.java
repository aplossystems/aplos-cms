package com.aplos.cms.servlets;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.utils.ImageUtil;

public class FormFileServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -6427770666934733529L;
	private static Logger logger = Logger.getLogger( FormFileServlet.class );

	@Override
	protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
		if (request.getSession().getAttribute( "currentUser" ) == null) {
			return;
		}

		String filename = request.getPathInfo().substring( 1 );
		filename = CommonWorkingDirectory.UPLOAD_DIR.getDirectoryPath(true) + URLDecoder.decode(filename);
		logger.info("form file: " + filename);

		InputStream stream = new FileInputStream(filename);

		response.setContentType(getServletContext().getMimeType(filename));
		response.setHeader("Content-Disposition", "inline;");

		// TODO Refactor this mess
		if (request.getParameter( "thumb" ) != null) {
			BufferedImage image = javax.imageio.ImageIO.read( stream );
			image = ImageUtil.resizeImage( image, 200, 200 );
			ImageIO.write( image, "jpeg", response.getOutputStream() );
		} else {
			BufferedInputStream input = null;
			BufferedOutputStream output = null;

			try {
			    input = new BufferedInputStream(stream);
			    output = new BufferedOutputStream(response.getOutputStream());
			    byte[] buffer = new byte[8192];
			    int length;
			    while ((length = input.read(buffer)) > 0) {
			        output.write(buffer, 0, length);
			    }
			} finally {
			    if (output != null) {
					try { output.close(); } catch (IOException logOrIgnore) {}
				}
			    if (input != null) {
					try { input.close(); } catch (IOException logOrIgnore) {}
				}
			}

		}
	}

}