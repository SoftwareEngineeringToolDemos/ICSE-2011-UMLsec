package tum.umlsec.viki.tools.riskFinder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import tum.umlsec.viki.framework.ExceptionRuntimeError;
import tum.umlsec.viki.framework.ITextOutput;
/**
 * 
 * @author Marc Peschke
 * marc.peschke@isst.fraunhofer.de <br>
 * 
 * beinhaltet Mehoden zur Verarbeitung einer Zargo-Datei
 *
 */
public class ZargoHelper {
	
	private String zipFileName;
	private ZipFile zipFile;
	private byte[] buffer = new byte[1024];
	private static Logger logger=Logger.getLogger("riskFinder");

	public ZargoHelper(){
		
	}
	
	/**
	 * läd aus einer Zargo-Datei, die Datei mit .pgml Endung
	 * @param File zargoFile
	 * @param ITextOutput
	 * @return File 
	 */
	public File loadPgml(File zargoFile, ITextOutput textOutput){
			
		if ( zargoFile != null && getSuffix(zargoFile).equalsIgnoreCase("zargo")){
			
			try{
				// get pgml-File from zargo-File
				zipFile = new ZipFile(zargoFile);
				@SuppressWarnings("rawtypes")
				Enumeration zipEntries = zipFile.entries();
				ZipEntry zipEntry;
				String pgmlEntryName = "";
				
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						pgmlEntryName = zipEntry.getName();
					}
				}
				while (!pgmlEntryName.endsWith(".pgml"));
			
				ZipEntry pgmlzipEntry = zipFile.getEntry(pgmlEntryName);
				BufferedInputStream is = new BufferedInputStream( zipFile.getInputStream(pgmlzipEntry));
				int count;

				File tempFile = File.createTempFile("UMLSEC", "pgml");
				tempFile.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());

				BufferedOutputStream dest = new BufferedOutputStream(fos, buffer.length);
				while ((count = is.read(buffer, 0, buffer.length)) != -1) {
					dest.write(buffer, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
				return tempFile;
				
			} catch (IOException x) {
				throw new ExceptionRuntimeError("Error while loading ArgoUML .pgml File!");
			}
		}
		else{
			logger.error("\n no File in Framework found!");
		}
		return null;
		
	}
	
	/**
	 * speichert eine Zargo-Datei
	 * @param File zargoFile
	 * @param File checkedZargoFile
	 * @param File pgmlFile
	 * @param File xmiFile
	 * @return boolean
	 */
	public boolean saveZargo(File zargoFile, File checkedZargoFile, File pgmlF, File xmiFile){
		
		int count;
		ZipEntry zipEntry;
		@SuppressWarnings("rawtypes")
		Enumeration zipEntries;
		int bytesRead;
		
		if (pgmlF != null){
			try{
				//save results in new zargo-File
				zipFile = new ZipFile(zargoFile);
				//argo
				String argoEntryName = "";
				File argoTempFile = File.createTempFile("UMLSEC", "argo");
				argoTempFile.deleteOnExit();
				zipEntries = zipFile.entries();
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						argoEntryName = zipEntry.getName();
					}
				}
				while (!argoEntryName.endsWith(".argo"));
				ZipEntry argozipEntry = zipFile.getEntry(argoEntryName);
				BufferedInputStream is = new BufferedInputStream( zipFile.getInputStream(argozipEntry));
				FileOutputStream fos = new FileOutputStream(argoTempFile.getAbsolutePath());
				BufferedOutputStream dest = new BufferedOutputStream(fos, buffer.length);
				while ((count = is.read(buffer, 0, buffer.length)) != -1) {
					dest.write(buffer, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
							
				//todo
				String todoEntryName = "";
				File todoTempFile = File.createTempFile("UMLSEC", "todo");
				todoTempFile.deleteOnExit();
				zipEntries = zipFile.entries();
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						todoEntryName = zipEntry.getName();
					}
				}
				while (!todoEntryName.endsWith(".todo"));
				ZipEntry todozipEntry = zipFile.getEntry(todoEntryName);
				is = new BufferedInputStream( zipFile.getInputStream(todozipEntry));
				fos = new FileOutputStream(todoTempFile.getAbsolutePath());
				dest = new BufferedOutputStream(fos, buffer.length);
				while ((count = is.read(buffer, 0, buffer.length)) != -1) {
					dest.write(buffer, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
				
				//xmi only get name
				String xmiEntryName = "";
				File xmiTempFile = File.createTempFile("UMLSEC", "xmi");
				xmiTempFile.deleteOnExit();
				zipEntries = zipFile.entries();
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						xmiEntryName = zipEntry.getName();
					}
				}
				while (!xmiEntryName.endsWith(".xmi"));
				
				//profile
				String profileEntryName = "";
				File profileTempFile = File.createTempFile("UMLSEC", "profile");
				profileTempFile.deleteOnExit();
				zipEntries = zipFile.entries();
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						profileEntryName = zipEntry.getName();
					}
				}
				while (!profileEntryName.endsWith(".profile"));
				ZipEntry profilezipEntry = zipFile.getEntry(profileEntryName);
				is = new BufferedInputStream( zipFile.getInputStream(profilezipEntry));
				fos = new FileOutputStream(profileTempFile.getAbsolutePath());
				dest = new BufferedOutputStream(fos, buffer.length);
				while ((count = is.read(buffer, 0, buffer.length)) != -1) {
					dest.write(buffer, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
	
				//pgml only get name
				String pgmlEntryName = "";
				zipEntries = zipFile.entries();
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						pgmlEntryName = zipEntry.getName();
					}
				}
				while (!pgmlEntryName.endsWith(".pgml"));
				
				//make zargo-File
				zipFileName = zargoFile.getParent() + File.separator + "checked_" + zargoFile.getName();
				
				File fileTest = new File(zipFileName);
			    if (fileTest.exists() && !fileTest.isDirectory()) {
			    	fileTest.delete();
			    }
				
				ZipOutputStream outStream = new ZipOutputStream(new FileOutputStream(zipFileName));
	
				//argo
	            FileInputStream inStream = new FileInputStream(argoTempFile);
	            outStream.putNextEntry(new ZipEntry("checked_" +argoEntryName));
	            while ((bytesRead = inStream.read(buffer)) > 0) {
	                outStream.write(buffer, 0, bytesRead);
	            }
	            outStream.closeEntry();
	            inStream.close();
	
				//todo
	            inStream = new FileInputStream(todoTempFile);
	            outStream.putNextEntry(new ZipEntry("checked_" +todoEntryName));
	            while ((bytesRead = inStream.read(buffer)) > 0) {
	                outStream.write(buffer, 0, bytesRead);
	            }
	            outStream.closeEntry();
	            inStream.close();
	
				//xmi
	            inStream = new FileInputStream(xmiFile);
	            outStream.putNextEntry(new ZipEntry("checked_" +xmiEntryName));
	            while ((bytesRead = inStream.read(buffer)) > 0) {
	                outStream.write(buffer, 0, bytesRead);
	            }
	            outStream.closeEntry();
	            inStream.close();
	
				//pgml
	            inStream = new FileInputStream(pgmlF);
	            outStream.putNextEntry(new ZipEntry("checked_" + pgmlEntryName));
	            while ((bytesRead = inStream.read(buffer)) > 0) {
	                outStream.write(buffer, 0, bytesRead);
	            }
	            outStream.closeEntry();
	            inStream.close();
	
				//profile
	            inStream = new FileInputStream(profileTempFile);
	            outStream.putNextEntry(new ZipEntry("checked_" +profileEntryName));
	            while ((bytesRead = inStream.read(buffer)) > 0) {
	                outStream.write(buffer, 0, bytesRead);
	            }
	            outStream.closeEntry();
	            inStream.close();
	            
	            checkedZargoFile = fileTest;
	            outStream.close();
			
			} catch (IOException x) {
				throw new ExceptionRuntimeError("Error while saving ArgoUML File!");
			}
		} else{
			// nothing to do
			return false;
		}
		
		return true;
	}
	
	/**
	 * gibt das Suffix einer Datei
	 * @param File
	 * @return String suffix
	 * 
	 */
	private static String getSuffix(File f) {
		
		String s = f.getPath();
		String suffix = null;

		int i = s.lastIndexOf('.');
		if(i > 0 && i < s.length() - 1) {
			suffix = s.substring(i + 1).toLowerCase();
		}
		return suffix;
	}

	/**
	 * liefert zipFileName
	 * @return String zipFileName
	 */
	public String getzipFileName() {

		return zipFileName;
	}

	/**
	 * läd die xmi-Datei aus dem Zargo-File
	 * @param File zargoFile
	 * @param ITextOutput
	 * @return File xmiFile
	 */
	public File loadXMI(File zargoFile, ITextOutput textOutput) {
		
		if ( zargoFile != null && getSuffix(zargoFile).equalsIgnoreCase("zargo")){
			
			try{
				// get xmi-File from zargo-File
				zipFile = new ZipFile(zargoFile);
				@SuppressWarnings("rawtypes")
				Enumeration zipEntries = zipFile.entries();
				ZipEntry zipEntry;
				String xmiEntryName = "";
				
				do {
					if (zipEntries.hasMoreElements()) {
						zipEntry = (ZipEntry)(zipEntries.nextElement());
						xmiEntryName = zipEntry.getName();
					}
				}
				while (!xmiEntryName.endsWith(".xmi"));
			
				ZipEntry xmizipEntry = zipFile.getEntry(xmiEntryName);
				BufferedInputStream is = new BufferedInputStream( zipFile.getInputStream(xmizipEntry));
				int count;

				File tempFile = File.createTempFile("UMLSEC", "xmi");
				tempFile.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());

				BufferedOutputStream dest = new BufferedOutputStream(fos, buffer.length);
				while ((count = is.read(buffer, 0, buffer.length)) != -1) {
					dest.write(buffer, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
				return tempFile;
				
			} catch (IOException x) {
				throw new ExceptionRuntimeError("Error while loading ArgoUML .xmi File!");
			}
		}
		else{
			logger.error("\n no File in Framework found!");
		}
		return null;
	}
	
}
