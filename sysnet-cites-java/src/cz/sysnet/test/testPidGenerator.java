package cz.sysnet.test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import cz.sysnet.cites.PidGenerator;

public class testPidGenerator {
	   @Test
	    public void testCodeLength() {
		   PidGenerator pidGenerator = new PidGenerator();
		   String id = pidGenerator.getId();
		   assertTrue(id.length() == 12);	
	    }

	   @Test
	    public void testPrefix() {
		   PidGenerator pidGenerator = new PidGenerator();
		   String id = pidGenerator.generateId("TEST");
		   assertTrue(id.startsWith("TES"));		   
	    }
	   
	   @Before
	   public void beforeRun() {}
	   
	   @After
	   public void afterRun() {}
	   
	   
}
