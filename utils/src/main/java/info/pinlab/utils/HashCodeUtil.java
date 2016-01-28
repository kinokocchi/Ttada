package info.pinlab.utils;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

/**
* Collected methods which allow easy implementation of <code>hashCode</code>.
*
* Example use case:
* <pre>
*  public int hashCode(){
*    int result = HashCodeUtil.SEED;
*    //collect the contributions of various fields
*    result = HashCodeUtil.hash(result, fPrimitive);
*    result = HashCodeUtil.hash(result, fObject);
*    result = HashCodeUtil.hash(result, fArray);
*    return result;
*  }
* </pre>
*/
public final class HashCodeUtil {

	private static MessageDigest sha1Digest; 
	private static MessageDigest sha256Digest; 
	static {
		try {
			sha1Digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			sha1Digest = null;
		}
		
		try {
			sha256Digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			sha256Digest = null;
		}
	}

	
  /**
  * An initial value for a <code>hashCode</code>, to which is added contributions
  * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
  * values.
  */
  public static final int SEED = 23;

  /**
  * booleans.
  */
  public static int hash( int aSeed, boolean aBoolean ) {
//    System.out.println("boolean...");
    return firstTerm( aSeed ) + ( aBoolean ? 1 : 0 );
  }

  /**
  * chars.
  */
  public static int hash( int aSeed, char aChar ) {
//    System.out.println("char...");
    return firstTerm( aSeed ) + (int)aChar;
  }

  /**
  * ints.
  */
  public static int hash( int aSeed , int aInt ) {
    /*
    * Implementation Note
    * Note that byte and short are handled by this method, through
    * implicit conversion.
    */
//    System.out.println("int...");
    return firstTerm( aSeed ) + aInt;
  }

  /**
  * longs.
  */
  public static int hash( int aSeed , long aLong ) {
//    System.out.println("long...");
    return firstTerm(aSeed)  + (int)( aLong ^ (aLong >>> 32) );
  }

  /**
  * floats.
  */
  public static int hash( int aSeed , float aFloat ) {
    return hash( aSeed, Float.floatToIntBits(aFloat) );
  }

  /**
  * doubles.
  */
  public static int hash( int aSeed , double aDouble ) {
    return hash( aSeed, Double.doubleToLongBits(aDouble) );
  }

  /**
  * <code>aObject</code> is a possibly-null object field, and possibly an array.
  *
  * If <code>aObject</code> is an array, then each element may be a primitive
  * or a possibly-null object.
  */
  public static int hash( int aSeed , Object aObject ) {
    int result = aSeed;
    if ( aObject == null) {
      result = hash(result, 0);
    }
    else if ( ! isArray(aObject) ) {
      result = hash(result, aObject.hashCode());
    }
    else {
      int length = Array.getLength(aObject);
      for ( int idx = 0; idx < length; ++idx ) {
        Object item = Array.get(aObject, idx);
        //recursive call!
        result = hash(result, item);
      }
    }
    return result;
  }

  public static String getSHA256(byte [] raw){
	  final byte[] digested = sha256Digest.digest(raw);
	  String sha = null;
	  try {
		  sha = new String(Base64.encodeBase64(digested), "UTF8");
	  } catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
	  }
	  return sha;
  }

  
  public static byte[] getSHA1(String s){
	  try {
		return sha1Digest.digest(s.getBytes("UTF8"));
	} catch (UnsupportedEncodingException ignore) {
		return null;
	}
  }
  public static String getSHA1(byte [] raw){
	  final byte[] digested = sha1Digest.digest(raw);
	  String sha = null;
	  try {
		  sha = new String(Base64.encodeBase64(digested), "UTF8");
	  } catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
	  }
	  return sha;
  }

  public static String encodeBase64(byte [] bytes){
	  try {
		  return  new String(Base64.encodeBase64(bytes), "UTF8");
	  } catch (UnsupportedEncodingException ignore) {
		  ignore.printStackTrace();
		  throw new IllegalStateException("Platform doesn't suppoert encoding: 'UTF8'");
	  }
  }
  

  /// PRIVATE ///
  private static final int fODD_PRIME_NUMBER = 37;

  private static int firstTerm( int aSeed ){
    return fODD_PRIME_NUMBER * aSeed;
  }

  private static boolean isArray(Object aObject){
    return aObject.getClass().isArray();
  }
  
  
  
  
} 