/**
 * 
 */
package cz.sysnet.cites;
import java.io.IOException;



/**
 * @author Radim
 *
 */
public class test {

	/**
	 * @param args
	 */
	static PdfFactory pfact;
	static WorkDocument wdoc;
	static PidGenerator ifact;
	static final String EOL = System.getProperty("line.separator");
	static final String RESTURL = "http://athos.sysnet.cz/clients/env/cites/v3/cites-admin300.nsf/api/data/collections/name/pdf-forms";
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ifact = new PidGenerator();
		wdoc = new WorkDocument(ifact);
		pfact = new PdfFactory(wdoc);
		pfact.setDebug(true);
		
		
		System.out.println("Ahoj, swěte2! ");
		System.out.println(pfact.getDoc().getTmpDir());
        
		
		pfact.loadTemplates(RESTURL);
		pfact.storeTemplateFiles();
		
		String out = pfact.create1FlatPdf("aths9csjl9zo", true, "11re");
		
        System.out.println(out);
        //System.out.println(pfact.getTemplate());
        
	}
}
