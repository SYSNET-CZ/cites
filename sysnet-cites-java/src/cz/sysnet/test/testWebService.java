package cz.sysnet.test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cz.sysnet.cites.WebService;
import static org.junit.Assert.*;


public class testWebService {
	private static final String FROM_LOCATION = "http://cites.env.cz/Cites/cites-req.nsf/xsp/.ibmmodres/domino/OpenAttachment/Cites/cites-req.nsf/4332CF6BFF3AEB13C12578A0004F4B31/Attachment/cites_registrace_osob_20110412.pdf";
	private static final String FROM_LOCATION1 = "http://cites.env.cz/Cites/cites-req.nsf/index.xsp";
	private static final String TO_LOCATION = "";
	private static final String FILE_NAME = "test.pdf";
	private static final String TMPDIR = System.getProperty("java.io.tmpdir");
	 
	
	  
	@Test
	  public void testDownloadFile() {
		  WebService ws = new WebService();
		  String f = ws.downloadFile(FROM_LOCATION, TO_LOCATION, FILE_NAME);
		  assertTrue(f.equals(TMPDIR + FILE_NAME));
	  }
	
	@Test
	  public void testDownloadText() {
		  WebService ws = new WebService();
		  String f = ws.downloadText(FROM_LOCATION1);
		  assertTrue(f.startsWith("<!DOCTYPE HTML PUBLIC"));
	  }
	
	@Test
	  public void testRemoveFile() {
		  WebService ws = new WebService();
		  assertTrue(ws.removeFile(TMPDIR + FILE_NAME));
	  }
	

	  @Before
	   public void beforeRun() {}
	   
	   @After
	   public void afterRun() {}
}
