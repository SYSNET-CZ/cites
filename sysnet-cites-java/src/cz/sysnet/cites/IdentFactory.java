package cz.sysnet.cites;

import java.io.Serializable;
import java.util.UUID;

public class IdentFactory implements Serializable, IdGenerator {
	private static final long serialVersionUID = 1808848911227008508L;
	private static final String BEAN_NAME = "IdentFactory";
	
	private UUID uid;
	private String id;
	
	public IdentFactory() {
		this.generateId("");
	}
	
	public String generateId(String prefix) {
		try {	
			this.uid = UUID.randomUUID();
			long lsb = this.uid.getLeastSignificantBits();
			long msb = this.uid.getMostSignificantBits();
			String id1 = Long.toUnsignedString(lsb, 36).toUpperCase();
			String id2 = Long.toUnsignedString(msb, 36).toUpperCase();
			String id3 = id2 + id1;
			String p = prefix.toUpperCase();
			if (p.isEmpty()) p = "CITES";
			if (p.length() > 3) p = p.substring(0, 3); 
			String out = p + id3.substring(id3.length()-(12-p.length()-1));
			long idl = Long.parseLong(out, 36);
			String chs = Long.toString(idl%37, 36).toUpperCase();
			this.id = out + chs;
			
			return this.id;
			
		} catch (Exception e) {
			return "";
		}	
	}
	

	public UUID getUid() {
		return uid;
	}

	public String getId() {
		return id;
	}

	public static String getBeanName() {
		return BEAN_NAME;
	}
	
}
