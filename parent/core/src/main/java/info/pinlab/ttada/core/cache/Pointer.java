package info.pinlab.ttada.core.cache;

/**
 * A pointer can be an int, a long or a string, or a byte array.
 * Normally, pointers are integers equal to the hash of the object
 * 
 * @author Gabor PINTER
 *
 */
public class Pointer {
	private final Object ref;
		
	public Pointer(Object p){
		if(p instanceof Pointer){
			this.ref = ((Pointer)p).ref;
		}else{
			ref = p;
		}
	}
	
	public int getInt(){
		return (Integer)ref;
//		if(pointer instanceof String){
//			byte[] b = ((String) pointer).getBytes();
//			final int len = b.length; 
//			int l = 0;
//			l |= b[0] & 0xFF;
//		    l <<= 8;
//		    l |= b[1] & 0xFF;
//		    l <<= 8;
//		    l |= b[2] & 0xFF;
//		    l <<= 8;
//		    l |= b[3] & 0xFF;
//			return l;
//		}else{
//		}
	}
	public String getString(){
		return (String)ref;
	}
	
	@Override
	public String toString(){
		return ref.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Pointer))
				return false;
		Pointer other = (Pointer)obj;
		if(this.ref.equals(other.ref))
			return true;
		return false;
	}
}
