package com.saerix.cms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saerix.cms.util.HttpError;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ResourceHandler implements HttpHandler {

	private SaerixHttpServer server;
	
	private Map<String, String> mimes = new MimeTypes();
	
	private Map<File, byte[]> chachedFiles = Collections.synchronizedMap(new HashMap<File, byte[]>());
	
	public ResourceHandler(SaerixHttpServer server) {
		this.server = server;
	}
	
	@Override
	public void handle(HttpExchange handle) throws IOException {
		if(!handle.getRequestMethod().equals("POST") && !handle.getRequestMethod().equals("GET")) {
			HttpError.send404(handle);
			return;
		}
		
		List<String> ahost = handle.getRequestHeaders().get("Host");
		if(ahost == null) {
			HttpError.send404(handle);
			return;
		}
		if(ahost.size() == 0) {
			HttpError.send404(handle);
			return;
		}
		
		String uriRequest = handle.getRequestURI().toString();
		String[] segmentsAndPara = uriRequest.split("\\?");
		String segments = segmentsAndPara[0];
		String[] segmentsArray = segments.split("/");
		String hostValue = ahost.get(0).split(":")[0];
		
		//CMS res
		if(segmentsArray.length > 1 ? segmentsArray[1].equalsIgnoreCase("admin") : false) {
			StringBuilder path = new StringBuilder();
			for(int i = 3; i < segmentsArray.length;i++) {
				path.append("/"+segmentsArray[i]);
			}
			
			String path2 = path.toString();
			
			InputStream is = getClass().getResourceAsStream("/com/saerix/cms/cms/resources"+path2);
			if(is == null) {
				HttpError.send404(handle);
				return;
			}
			
			String[] fileEnd = path2.split("\\.");
			if(fileEnd.length == 2) {
				String mime = mimes.get(fileEnd[1]);
				if(mime  != null)
					handle.getResponseHeaders().add("Content-Type", mime);
			}
			
			handle.sendResponseHeaders(200, 0);
			
			byte[] buffer = new byte[1];
			OutputStream os = handle.getResponseBody();
			while(is.read(buffer) != -1) {
				os.write(buffer);
			}
			is.close();
			os.flush();
			os.close();
			return;
		}
		
		File dir = new File("resources"+File.separator+hostValue);
	
		
		if(!dir.exists()) {
			HttpError.send404(handle);
			return;
		}
		
		StringBuilder path = new StringBuilder();
		for(int i = 2; i < segmentsArray.length;i++) {
			path.append(File.separator+segmentsArray[i]);
		}
		
		File file = new File(dir.getAbsolutePath()+path.toString());
		
		if(!file.exists() || file.isDirectory()){
			HttpError.send404(handle);
			return;
		}
		
		handle.sendResponseHeaders(200, 0);
		
		byte[] cache;
		synchronized (chachedFiles) {
			cache = chachedFiles.get(file);
		}
		
		if(cache != null && !server.getInstance().isInDevMode()) {
			OutputStream os = handle.getResponseBody();
			os.write(cache);
			os.flush();
			os.close();
		}
		else if(file.length() < 5242880 && !server.getInstance().isInDevMode()) {
			InputStream is = new FileInputStream(file);
			cache = new byte[(int) file.length()];
			is.read(cache);
			is.close();
			synchronized (chachedFiles) {
				cache = chachedFiles.put(file, cache);
			}
			OutputStream os = handle.getResponseBody();
			os.write(cache);
			os.flush();
			os.close();
		}
		else {
			byte[] buffer = new byte[1];
			InputStream is = new FileInputStream(file);
			OutputStream os = handle.getResponseBody();
			while(is.read(buffer) != -1) {
				os.write(buffer);
			}
			is.close();
			os.flush();
			os.close();
		}
	}
	
	
	private static class MimeTypes extends HashMap<String, String> {
		private static final long serialVersionUID = 1L;
		
		public MimeTypes() {
			String types = "ai=application/postscript:aif=audio/x-aiff:aifc=audio/x-aiff:aiff=audio/x-aiff:asc=text/plain:atom=application/atom+xml:au=audio/basic:avi=video/x-msvideo:bcpio=application/x-bcpio:bin=application/octet-stream:bmp=image/bmp:cdf=application/x-netcdf:cgm=image/cgm:class=application/octet-stream:cpio=application/x-cpio:cpt=application/mac-compactpro:csh=application/x-csh:css=text/css:dcr=application/x-director:dif=video/x-dv:dir=application/x-director:djv=image/vnd.djvu:djvu=image/vnd.djvu:dll=application/octet-stream:dmg=application/octet-stream:dms=application/octet-stream:doc=application/msword:dtd=application/xml-dtd:dv=video/x-dv:dvi=application/x-dvi:dxr=application/x-director:eps=application/postscript:etx=text/x-setext:exe=application/octet-stream:ez=application/andrew-inset:gif=image/gif:gram=application/srgs:grxml=application/srgs+xml:gtar=application/x-gtar:hdf=application/x-hdf:hqx=application/mac-binhex40:htm=text/html:html=text/html:ice=x-conference/x-cooltalk:ico=image/x-icon:ics=text/calendar:ief=image/ief:ifb=text/calendar:iges=model/iges:igs=model/iges:jnlp=application/x-java-jnlp-file:jp2=image/jp2:jpe=image/jpeg:jpeg=image/jpeg:jpg=image/jpeg:js=application/x-javascript:kar=audio/midi:latex=application/x-latex:lha=application/octet-stream:lzh=application/octet-stream:m3u=audio/x-mpegurl:m4a=audio/mp4a-latm:m4b=audio/mp4a-latm:m4p=audio/mp4a-latm:m4u=video/vnd.mpegurl:m4v=video/x-m4v:mac=image/x-macpaint:man=application/x-troff-man:mathml=application/mathml+xml:me=application/x-troff-me:mesh=model/mesh:mid=audio/midi:midi=audio/midi:mif=application/vnd.mif:mov=video/quicktime:movie=video/x-sgi-movie:mp2=audio/mpeg:mp3=audio/mpeg:mp4=video/mp4:mpe=video/mpeg:mpeg=video/mpeg:mpg=video/mpeg:mpga=audio/mpeg:ms=application/x-troff-ms:msh=model/mesh:mxu=video/vnd.mpegurl:nc=application/x-netcdf:oda=application/oda:ogg=application/ogg:pbm=image/x-portable-bitmap:pct=image/pict:pdb=chemical/x-pdb:pdf=application/pdf:pgm=image/x-portable-graymap:pgn=application/x-chess-pgn:pic=image/pict:pict=image/pict:png=image/png:pnm=image/x-portable-anymap:pnt=image/x-macpaint:pntg=image/x-macpaint:ppm=image/x-portable-pixmap:ppt=application/vnd.ms-powerpoint:ps=application/postscript:qt=video/quicktime:qti=image/x-quicktime:qtif=image/x-quicktime:ra=audio/x-pn-realaudio:ram=audio/x-pn-realaudio:ras=image/x-cmu-raster:rdf=application/rdf+xml:rgb=image/x-rgb:rm=application/vnd.rn-realmedia:roff=application/x-troff:rtf=text/rtf:rtx=text/richtext:sgm=text/sgml:sgml=text/sgml:sh=application/x-sh:shar=application/x-shar:silo=model/mesh:sit=application/x-stuffit:skd=application/x-koan:skm=application/x-koan:skp=application/x-koan:skt=application/x-koan:smi=application/smil:smil=application/smil:snd=audio/basic:so=application/octet-stream:spl=application/x-futuresplash:src=application/x-wais-source:sv4cpio=application/x-sv4cpio:sv4crc=application/x-sv4crc:svg=image/svg+xml:swf=application/x-shockwave-flash:t=application/x-troff:tar=application/x-tar:tcl=application/x-tcl:tex=application/x-tex:texi=application/x-texinfo:texinfo=application/x-texinfo:tif=image/tiff:tiff=image/tiff:tr=application/x-troff:tsv=text/tab-separated-values:txt=text/plain:ustar=application/x-ustar:vcd=application/x-cdlink:vrml=model/vrml:vxml=application/voicexml+xml:wav=audio/x-wav:wbmp=image/vnd.wap.wbmp:wbmxl=application/vnd.wap.wbxml:wml=text/vnd.wap.wml:wmlc=application/vnd.wap.wmlc:wmls=text/vnd.wap.wmlscript:wmlsc=application/vnd.wap.wmlscriptc:wrl=model/vrml:xbm=image/x-xbitmap:xht=application/xhtml+xml:xhtml=application/xhtml+xml:xls=application/vnd.ms-excel:xml=application/xml:xpm=image/x-xpixmap:xsl=application/xml:xslt=application/xslt+xml:xul=application/vnd.mozilla.xul+xml:xwd=image/x-xwindowdump:xyz=chemical/x-xyz:zip=application/zip";
			String[] mimes = types.split(":");
			for(String m : mimes) {
				String[] p = m.split("=");
				put(p[0], p[1]);
			}
		}
	}
}
