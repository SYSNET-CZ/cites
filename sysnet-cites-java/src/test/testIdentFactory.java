package test;
import org.junit.Test;
import cz.sysnet.cites.IdentFactory;
import static org.junit.Assert.*;

public class testIdentFactory {
	   @Test
	    public void testCodeLength() {
		   IdentFactory identFactory = new IdentFactory();
		   String id = identFactory.getId();
		   assertTrue(id.length() == 12);	
	    }

	   @Test
	    public void testPrefix() {
		   IdentFactory identFactory = new IdentFactory();
		   String id = identFactory.generateId("TEST");
		   assertTrue(id.startsWith("TES"));		   
	    }
}
