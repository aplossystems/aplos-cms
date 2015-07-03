package com.aplos.cms.beans;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
public class HostedVideo extends AplosBean {
	private static final long serialVersionUID = -55667695839634021L;
	private String title="";
	private String url="";
	private String videoId="";
	private String width = "100%";
	private String height; 
	private String styleClass;
	@Column(columnDefinition="LONGTEXT")
	private String pastedCode="";
	@Column(columnDefinition="LONGTEXT")
	private String description;
	private int numberOfRelatedVideos = 0;
	private SslProtocolEnum sslProtocolEnum;
	

	public HostedVideo() {}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String getDisplayName() {
		return title;
	}
	
	public void parseCode( String pastedCode ) {
		if( pastedCode.startsWith( "http://" ) ) {
			setSslProtocolEnum(SslProtocolEnum.FORCE_HTTP);
			pastedCode = pastedCode.replace( "http://", "" );
		} else if( pastedCode.startsWith( "https://" ) ) {
			setSslProtocolEnum(SslProtocolEnum.FORCE_SSL);
			pastedCode = pastedCode.replace( "https://", "" );
		} else {
			setSslProtocolEnum(null);
		}
		if (pastedCode.contains("youtube.com/watch?v=")) {
			if (pastedCode.indexOf('&') != -1) {
				setVideoId( pastedCode.substring(pastedCode.indexOf('=')+1, pastedCode.indexOf('&')) );
			}
			else {
				setVideoId( pastedCode.substring(pastedCode.indexOf('=')+1) );
			}
			setUrl( pastedCode );
			return;
		}
		
		if (pastedCode.contains("youtu.be/")) {
			if (pastedCode.indexOf('&') != -1) {
				setVideoId( pastedCode.substring(pastedCode.indexOf("be/")+3, pastedCode.indexOf('&')) );
			}
			else {
				setVideoId( pastedCode.substring(pastedCode.indexOf("be/")+3) );
			}
			setUrl( pastedCode );
			return;
		}

		Pattern pattern = Pattern.compile("www.youtube.com/v/(.*?)\\?");
		String match = "";
		Matcher m = pattern.matcher(pastedCode);
		if (m.find()) {
			match = m.group(1);
			setUrl( "www.youtube.com/v/" + match + "?" );
			setVideoId( match );
			return;
		}


		pattern = Pattern.compile("www.youtube.com/embed/(.*?)\"");
		match = "";
		m = pattern.matcher(pastedCode);
		if (m.find()) {
			match = m.group(1);
			setUrl( "www.youtube.com/embed/" + match );
			setVideoId( match );
			return;
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoId() {
		return videoId;
	}

	public String getEmbedCode() {
		return getEmbedCode( getWidth(), getHeight(), getStyleClass() );
	}

	public String getEmbedCode(String width, String height) {
		return getEmbedCode( width, height, null );
	}

	public String getEmbedCode(String width, String height, String styleClass) {
		StringBuffer embedUrl = new StringBuffer();
		if( SslProtocolEnum.FORCE_HTTP.equals( getSslProtocolEnum() ) || JSFUtil.isLocalHost() ) {
			embedUrl.append( "http://" );
		} else if( SslProtocolEnum.FORCE_SSL.equals( getSslProtocolEnum() ) ) {
			embedUrl.append( "https://" );
		} else {
			if( JSFUtil.getRequest().getScheme().contains("https") ) {
				embedUrl.append( "https://" );
			} else {
				embedUrl.append( "http://" );
			}
		}
		
		embedUrl.append( "www.youtube.com/embed/" ).append( getVideoId() );
		embedUrl.append( "?rel=" ).append( getNumberOfRelatedVideos() ).append( "&VQ=HD720" );
		
		StringBuffer iframeUrl = new StringBuffer( "<iframe title='YouTube video player' " );
		if( !CommonUtil.isNullOrEmpty( width ) ) {
			iframeUrl.append( " width='" );
			iframeUrl.append( width ).append( "'" );
		}
		if( !CommonUtil.isNullOrEmpty( height ) ) {
			iframeUrl.append( " height='" );
			iframeUrl.append( height ).append( "'" );
		}
		if( !CommonUtil.isNullOrEmpty( styleClass ) ) {
			iframeUrl.append( " class='" );
			iframeUrl.append( styleClass ).append( "'" );
		}
		iframeUrl.append( " src='" ).append( embedUrl.toString() );
		iframeUrl.append( "' frameborder='0' allowfullscreen></iframe>" );
			
		return iframeUrl.toString();
	}

	public String getPastedCode() {
		return pastedCode;
	}

	public void setPastedCode(String pastedCode) {
		this.pastedCode = pastedCode;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public int getNumberOfRelatedVideos() {
		return numberOfRelatedVideos;
	}

	public void setNumberOfRelatedVideos(int numberOfRelatedVideos) {
		this.numberOfRelatedVideos = numberOfRelatedVideos;
	}

	public SslProtocolEnum getSslProtocolEnum() {
		return sslProtocolEnum;
	}

	public void setSslProtocolEnum(SslProtocolEnum sslProtocolEnum) {
		this.sslProtocolEnum = sslProtocolEnum;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}



}
