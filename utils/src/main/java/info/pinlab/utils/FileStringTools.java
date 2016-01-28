package info.pinlab.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileStringTools {
	public static final Logger LOG = LoggerFactory.getLogger(FileStringTools.class);

	public static String COMMENT = "#";
	public static char ESCAPE = 92;
	public static String SEP = System.getProperty("file.separator");
	
	private static Pattern patUnderline = Pattern.compile("__(.*?)__");
	private static Pattern patBold = Pattern.compile("\\*\\*(.*?)\\*\\*");
	private static Pattern patItalic = Pattern.compile("//(.*?)//");
	
	public static String trim(String l){
//		l = l.trim();
		int comAt = l.indexOf(COMMENT);
		if(comAt==0){
			return "";
		}
		if (comAt < 0 ){
			return l.trim();
		}
		if(l.charAt(comAt-1) == ESCAPE){
			//TODO: look for other
			return l.trim();
		}else{
			return l.substring(0, comAt).trim();
		}
	}
	public static String getFileAsString(String path) throws FileNotFoundException, IOException{
		return getFileAsString(new File(path));
	}
	
	/**
	 * 
	 * @param file
	 * @return the contents of the file as string.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String getFileAsString(File file) throws FileNotFoundException, IOException{
	  FileInputStream stream = new FileInputStream(file);
	  try {	
		  FileChannel fc = stream.getChannel();
		  MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
		  /* Instead of using default, pass in a decoder. */
		  return Charset.forName("UTF-8").decode(mbb).toString();
	  }catch(IOException e){
		  throw e;
	  }finally {
	    stream.close();
	  }
	}
	
	static public byte[] getFileAsByteArray(String path) throws FileNotFoundException{
		File file = new File(path);
		InputStream is = new FileInputStream(file);
		long len = file.length();
		if(len > Integer.MAX_VALUE){
			try {
				is.close();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
			throw new IllegalArgumentException("The file is too big! '" + file.getAbsolutePath()+"'");
		}

		byte[] bytes = new byte[(int)len];
		
		 // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    try{
		    while (offset < bytes.length
		           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
		        offset += numRead;
		    }
	
		    // Ensure all the bytes have been read in
		    if (offset < bytes.length) {
				try {
					is.close();
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
		        throw new IOException("Could not completely read file "+file.getName());
		    }
		    // Close the input stream and return bytes
		    is.close();
		    return bytes;
	    }catch(IOException e){
	    	throw new IllegalArgumentException("IOException while reading '" + file.getAbsolutePath()+"'");
	    }
	}
	

	/**
	 * 
	 * @param ext   extension, such as '.txt'
	 * @param path
	 * @return a flat list of files that matches the extension
	 */
	public static List<File> findFilesRecursively(String ext, File path){
		if(!path.isDirectory())
			return null;
		
		if(!ext.startsWith("."))
			ext = "." + ext;
		ext = ext.toLowerCase();
		
		List<File> files = new ArrayList<File>();
		for(String file : path.list()){
			File f = new File(path+System.getProperty("file.separator") + file);
			if(f.isDirectory()){
				files.addAll(findFilesRecursively(ext, f));
			}else{
				if(file.toLowerCase().endsWith(ext))
					files.add(f);
			}
		}
		return files;
	}
	

	public static File cwd(){
		return  new File(".").getAbsoluteFile();
	}
	
	/**
	 * 
	 * a b  c        => "a" , "b" , "c" 
	 * "a"  "b"  "c" => "a" , "b" , "c" 
	 * "a  b"  "c"   => "a b" , "c" 
	 * a"b c"d       => "a" , "b c" , "d" 
	 * 
	 * 
	 * 
	 * @param line
	 * @return the chunks
	 */
	public static List<String> split(String line){
		List<String> chunks = new ArrayList<String>();
		int end = 0;
		int start = 0;
//		boolean isOpenQuote = false;
		while(end < line.length()){
//			System.out.println("START " + start+"-" +end);
			if(line.charAt(end)=='\"'){
				int endQ = -1;
				int origEnd = end;
				while (end < line.length()){
					endQ = line.indexOf('\"', end+1);
					if(endQ < 0 )
						break;
					//-- escaped quote  "this is escaped \" quote"
					if(line.charAt((endQ-1)) == '\\' ){ 
						System.out.println("MATCH");
						end = endQ+1;
						endQ = -1;
						continue;
					}else{
						break;
					}
				}
				if(end > line.length())
					end = origEnd;
				
				if(endQ < 0){
					LOG.error("Runaway quote at " + start +"  '" + line +"'");
					end++;
					continue;
				}else{
					if(start != origEnd){
						System.out.println("'" + line.substring(start, origEnd) + "' "  +start + "-" + origEnd);
						chunks.add(line.substring(start,origEnd).trim());
						start = origEnd;
					}
					
					end = endQ;
					chunks.add(line.substring(start+1,end).trim());
					start = end = end+1; 
//					start = end+1; //-- start right after quote
					continue;
//					System.out.println(line.substring(start,end));
				}
			}
			
			if(line.charAt(end)==' ' || line.charAt(end)=='\t'){
				if(start==end){
					start=end = end+1;
					continue;
				}
				chunks.add(line.substring(start,end));
				end = start = end+1;
				continue;
			}
			end++;
		}
		if(end != start){
			chunks.add(line.substring(start));
		}
			
		return chunks;
	}
	
	public static Boolean getBoolean(String s){
		if (s == null)
			return false;
		s = s.toLowerCase().trim();
		if("1".equals(s) || "yes".equals(s) || "true".equals(s))
			return true;
		if("0".equals(s) || "no".equals(s) || "false".equals(s))
			return false;
		throw new IllegalArgumentException("Illegal boolean string '" + s +"'");
	}
	
	
	public static String[] getKeyVal(String s, char sep){
		int sepIx = s.indexOf(sep);
		if(sepIx < 0)
			return new String[]{s,null};
		return new String[]{
				s.substring(0, sepIx),
				s.substring(sepIx+1)
		};
	}
	public static String[] getKeyVal(String s){
		s = s.trim();
		int spcAt = s.indexOf(' ') ;
		int tabAt = s.indexOf('\t');
		int at = 0;
		if(tabAt>=0 && spcAt >=0){
			at = tabAt < spcAt ? tabAt : spcAt;
		}else	{
			at = tabAt > 0 ? tabAt : (spcAt > 0 ) ? spcAt : 0;  
		}
//		System.out.println(spcAt);
//		System.out.println(tabAt);
//		System.out.println(at);
		
		String kv[] = new String[]{null,null};
		if(at>0){
			kv[0] = s.substring(0,at);
			kv[1] = s.substring(at+1).trim();
		}else{
			kv[1] = s;
		}
		return kv;
	}
	
	public static String getStreamAsString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

//            try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
//            } finally {
//                is.close();
//            }
            return sb.toString();
        } else {        
            return "";
        }
    }
	

	/**
	 * 
	 * @param dur  a duration can end with units : 'sec', 's', 'ms', 'min', 'm'
	 * @return  duration in milliseconds
	 * @throws NumberFormatException
	 */
	public static int getDur(String dur) throws NumberFormatException{
		int multiplier = 1000;
		String numb = dur;
		if(dur.endsWith("sec")){
			numb = dur.substring(0,dur.length()-"sec".length());
		}
		if(dur.endsWith("s")){
			numb = dur.substring(0,dur.length()-"s".length());
		}
		if(dur.endsWith("ms")){
			numb = dur.substring(0,dur.length()-"ms".length());
			multiplier = 1;
		}
		if(dur.endsWith("min")){
			numb = dur.substring(0,dur.length()-"min".length());
			multiplier = 60*1000;
		}
		if(dur.endsWith("m")){
			numb = dur.substring(0,dur.length()-"m".length());
			multiplier = 60*1000;
		}
		return Integer.parseInt(numb)*multiplier;
	}
	
	public static byte[] zip(String s){
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final GZIPOutputStream gzip = new GZIPOutputStream(baos);
			gzip.write(s.getBytes("UTF8"));
			gzip.close();
			final byte[] bytes = baos.toByteArray();
			baos.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e.getCause());
		}
	}
	
	public static String unzip(byte [] bytes) throws IllegalStateException{
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			GZIPInputStream gzis = new GZIPInputStream(bais);
//			InputStreamReader reader = new InputStreamReader(gzis);
//			BufferedReader in = new BufferedReader(reader);
	
			byte[] buf = new byte[1024];
			int len;
			while ((len = gzis.read(buf)) > 0) {
				baos.write(buf, 0, len);
			}
			gzis.close();
			bais.close();
			
			return new String(baos.toByteArray(), "UTF8");
		}catch(IOException e){
			e.printStackTrace();
			throw new IllegalStateException(e.getCause());
		}
	}
	

	
	public static String convertMarkDownToHtml(String str){
		String returnStr = str;
		Matcher match = patUnderline.matcher(returnStr);
		returnStr = match.replaceAll("<u>$1</u>");

		match = patItalic.matcher(returnStr);
		returnStr = match.replaceAll("<i>$1</i>");

		match = patBold.matcher(returnStr);
		returnStr = match.replaceAll("<b>$1</b>");

		return returnStr;
	}
	
	
	
//	
//	public static String getRandomLowerCaseString(int len){
//		return UUID.randomUUID().toString().substring(0, len);
////		Random rand = new Random(System.currentTimeMillis());
////		StringBuffer buff = new StringBuffer();
////		for(int i = 0 ; i < len ; i++){
////			int ix = rand.nextInt(ALPHANUM.length());
////			buff.append(ALPHANUM.charAt(ix));
////		}
////		return buff.toString();
//	}
//	
//	
	
	/**
	 * 
	 * @param dir  Absolute path to the directory to be deleted.
	 * @throws IOException  If the argument is not a DIR or its path is not absolute.
	 */
	public static void removeDir(File dir) throws IOException{
		if(! (dir.getPath().startsWith(SEP) || dir.getPath().charAt(1)==':')){
			throw new IllegalArgumentException("Wrong arg, path must be ABSOLUTE, it's not: '" + dir.getPath() +"'");
		}
		if(!dir.exists()){
			LOG.info("DIR doesn't exists '" + dir.getAbsolutePath() +"'");
			return;
		}
		if(dir.isDirectory()){
			String [] files = dir.list();
			if(files!=null){
				for(String file : files){
					File f = new File(dir.getAbsoluteFile().getAbsolutePath()
							+ SEP + file);
					if(f.isFile()){
						boolean isDeleted = f.delete();
						LOG.info("Deleting FILE '" + f.getAbsolutePath() +"'");
						if(!isDeleted)
							throw new IOException("Can't delete '" + f.getAbsolutePath() +"'");
					}else{ //-- it's a dir
						removeDir(f);
					}
				}
			}
			LOG.info("Deleting DIR '" + dir.getAbsolutePath() +"'");
			dir.delete();
		}else{
			throw new IllegalArgumentException("Wrong arg, this must be a dir: '" + dir.getAbsolutePath() +"'");
		}
	}

	public static void main(String[] args) throws Exception {
		
//		FileStringTools.removeDir(new File(".pina"));
//		char c = 134;
//		System.out.println(trim("  a and " + c + "# after comment  "));
		
//		for(File f : findFilesRecursively(".wav", new File("data")))
//			System.out.println(f.getAbsolutePath());
		
//		for(String s : toChunks("This is fun    "))
//			System.out.println(s);
	}

}
