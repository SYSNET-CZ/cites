/**
 * 
 */
package cz.sysnet.cites;

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
	static IdentFactory ifact;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ifact = new IdentFactory();
		wdoc = new WorkDocument(ifact);
		pfact = new PdfFactory(wdoc);
		pfact.setDebug(true);
		System.out.println("Ahoj, swěte!");
	}

}
