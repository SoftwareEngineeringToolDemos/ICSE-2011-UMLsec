package tum.umlsec.viki.framework.mdr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.jmi.model.ModelElement;
import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.XmiWriter;
import javax.swing.event.EventListenerList;

import org.jdom.JDOMException;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.netbeans.lib.jmi.xmi.XMISaxReaderImpl;
import org.netbeans.lib.jmi.xmi.XMIWriterImpl;
import org.netbeans.lib.jmi.xmi.XmiSAXReader;
import org.openide.ErrorManager;
import org.openide.util.Lookup;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tum.umlsec.viki.framework.ExceptionRuntimeError;

/**
 * @author pasha
 */
public class MdrContainer implements IMdrContainer {

	private final int BUFFERSIZE = 1024 * 1128;
	public final String LOOKUPIMPL = StandaloneLookupForMdr.class.getName();
	// standard extent name for all MOF instance
	public final String METAMODEL = "tum.mof";
	// extent name for instance of my metamodel
	public final String MODEL = "tum.umlsec";
	// name of my metamodel package
	public final String PACKAGE = "UML";
	public File zargoFile = null;

	public MdrContainer(String _resourceRoot, String _repositoryRoot) {
		resourceRoot = _resourceRoot;
		repositoryPath = _repositoryRoot + "/mdrStorage";
		// @bugfix buerger
		// old:
		// umlDefinitionPath = "umlsec/umlMetamodel/01-02-15_Diff.xml";
		// this seems to be the wrong path, since the path is
		// UMLSEC_HOME/umlsec/bin/ during runtime.
		// As the _diff.xml is located in UMLSEC_HOME/umlMetamodel/,
		// we try this:

		umlDefinitionPath = "umlMetamodel/01-02-15_Diff.xml";

		umlExtentCount = new Integer(0);

		System.setProperty("org.openide.util.Lookup", LOOKUPIMPL);
		System.setProperty("org.netbeans.mdr.persistence.Dir", repositoryPath);
		System.setProperty("tum.umlsec.viki.resourceRoot", resourceRoot);
	}

	// private MdrContainer() { }
	//
	// public synchronized static MdrContainer getInstance() {
	// if (instance == null) {
	// instance = new MdrContainer();
	// }
	// return instance;
	// }

	public void addListener(IMdrContainerListener l) {
		listenerList.add(IMdrContainerListener.class, l);
	}

	public void removeListener(IMdrContainerListener l) {
		listenerList.remove(IMdrContainerListener.class, l);
	}

	protected void fireClearEvent() {
		Object[] _listeners = listenerList.getListenerList();
		ClearEvent _e = new ClearEvent(this);

		for (int i = _listeners.length - 2; i >= 0; i -= 2) {
			((IMdrContainerListener) _listeners[i + 1]).onClearMdr(_e);
		}
	}

	protected void fireLoadEvent() {
		Object[] _listeners = listenerList.getListenerList();
		LoadEvent _e = new LoadEvent(this);

		for (int i = _listeners.length - 2; i >= 0; i -= 2) {
			((IMdrContainerListener) _listeners[i + 1]).onLoadMdr(_e);
		}
	}

	public void Empty() {
		setEmpty(true);
		umlPackage = null;
		IdNameList.getInstance().clear();
		fireClearEvent();
	}

	public static String getSuffix(File f) {
		String s = f.getPath();
		String suffix = null;

		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			suffix = s.substring(i + 1).toLowerCase();
		}
		return suffix;
	}

	public void loadFromFile(File _file) {
		setEmpty(true);
		setZipFileLoaded(false);

		File _fileToOpen;

		if (getSuffix(_file).equalsIgnoreCase("zargo")
				|| getSuffix(_file).equalsIgnoreCase("zuml")) {
			try {
				String _zargoFileName = _file.getName();
				// String _xmiEntryName = _zargoFileName.substring(0,
				// _zargoFileName.lastIndexOf("zargo")) + "xmi";
				String _xmiEntryName = "";

				// Now we copy the archive into the tmp folder
				zargoFile = File.createTempFile(_zargoFileName,
						getSuffix(_file));
				zargoFile.deleteOnExit();
				BufferedInputStream _zInputStream = new BufferedInputStream(
						new FileInputStream(_file));
				byte zData[] = new byte[BUFFERSIZE];
				int zcount;
				FileOutputStream _zOutputStream = new FileOutputStream(
						zargoFile.getAbsolutePath());
				BufferedOutputStream _zDest = new BufferedOutputStream(
						_zOutputStream, BUFFERSIZE);
				while ((zcount = _zInputStream.read(zData, 0, BUFFERSIZE)) != -1) {
					_zDest.write(zData, 0, zcount);
				}
				_zDest.flush();
				_zDest.close();
				_zInputStream.close();
				_zOutputStream.close();
				// done copying the file into the tmp folder

				ZipFile _zipFile = new ZipFile(_file);
				Enumeration _zipEntries = _zipFile.entries();
				ZipEntry _zipEntry;
				do {
					if (_zipEntries.hasMoreElements()) {
						_zipEntry = (ZipEntry) (_zipEntries.nextElement());
						_xmiEntryName = _zipEntry.getName();
					}
				} while (!_xmiEntryName.endsWith(".xmi"));

				ZipEntry _xmizipEntry = _zipFile.getEntry(_xmiEntryName);
				BufferedInputStream _is = new BufferedInputStream(
						_zipFile.getInputStream(_xmizipEntry));
				int count;
				byte data[] = new byte[BUFFERSIZE];

				File _tempFile = File.createTempFile("UMLSEC", "xmi");
				_tempFile.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(
						_tempFile.getAbsolutePath());

				BufferedOutputStream _dest = new BufferedOutputStream(fos,
						BUFFERSIZE);
				while ((count = _is.read(data, 0, BUFFERSIZE)) != -1) {
					_dest.write(data, 0, count);
				}
				_dest.flush();
				_dest.close();
				_is.close();

				_fileToOpen = _tempFile;
				setZipFileLoaded(true);
			} catch (IOException x) {
				throw new ExceptionRuntimeError(
						"Can't process the ZARGO or ZUML archive. Possibly missing or corrupted file:"
								+ _file.getName());
			}
		} else {
			_fileToOpen = _file;
		}

		loadFromXmiFile(_fileToOpen);
		loadedFileName = _file.getAbsolutePath();
		// try {
		// loadedFileName = _file.getCanonicalPath();
		// } catch (IOException e) {
		// }
	}

	private void loadFromXmiFile(File f) {
		File __repositoryDirectory = new File(repositoryPath + "/pppasha");
		try {
			__repositoryDirectory.mkdirs();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		/*
		 * Removes any "argouml.org/profiles"-References from the temporary XMI
		 * to prevent error on model-load in models created with ArgoUML 0.28+
		 */
		if (f != null)
			removeURLReferencesFromXMI(f);

		try {

			/*
			 * // clean up XMI-file generated by Poseidon 2.*
			 * DocumentBuilderFactory factory =
			 * DocumentBuilderFactory.newInstance();
			 * factory.setValidating(false); Document doc =
			 * factory.newDocumentBuilder().parse(f); removeAll(doc,
			 * Node.ELEMENT_NODE, "UML:Diagram"); removeAll(doc,
			 * Node.ELEMENT_NODE, "UML:Property"); Source source = new
			 * DOMSource(doc); Result result = new StreamResult(f); Transformer
			 * xformer = TransformerFactory.newInstance().newTransformer();
			 * xformer.transform(source, result);
			 */
			clearRepository();

			// find repository implementation using lookup and MDRManager
			if (manager == null) {
				manager = (MDRManager) Lookup.getDefault().lookup(
						MDRManager.class);
			}
			if (repository == null) {
				repository = manager.getDefaultRepository();
			}
			repository.beginTrans(true);

			// create MOF metapackage
			if (extentMetaModel == null) {
				extentMetaModel = (ModelPackage) repository
						.getExtent(METAMODEL);
				if (extentMetaModel == null) {
					extentMetaModel = (ModelPackage) repository
							.createExtent(METAMODEL);
				}
			}

			if (packageMetaUml == null) {
				// load the UML metamodel
				XmiSAXReader xmiReader = (XmiSAXReader) Lookup.getDefault()
						.lookup(XmiSAXReader.class);
				File umlMetamodelFile = new File(umlDefinitionPath);
				RefPackage[] list = { extentMetaModel };
				xmiReader.read(umlMetamodelFile.toURL(), list, "UTF-8");

				// find the main UML package
				packageMetaUml = findUmlPackage(extentMetaModel);
			}

			// instantiate the UML model
			umlPackage = (org.omg.uml.UmlPackage) repository.getExtent(MODEL
					+ umlExtentCount.toString());
			while (umlPackage != null) {
				umlExtentCount = new Integer(umlExtentCount.intValue() + 1);
				umlPackage = (org.omg.uml.UmlPackage) repository
						.getExtent(MODEL + umlExtentCount.toString());
			}
			try {
				umlPackage = (org.omg.uml.UmlPackage) repository.createExtent(
						MODEL + umlExtentCount.toString(), packageMetaUml);
			} catch (Exception e) {
				ErrorManager.getDefault().notify(e);
			}
			umlExtentCount = new Integer(umlExtentCount.intValue() + 1);

			// load the XMI file
			XMISaxReaderImpl xmisaxreaderimpl = (XMISaxReaderImpl) Lookup
					.getDefault().lookup(XMISaxReaderImpl.class);
			String xmiFileName = f.toURL().toString();
			new XmiSAXParser(f.toURI().toString());
			xmisaxreaderimpl.read(xmiFileName, umlPackage);

			setEmpty(false);
			setModified(false);

		} catch (Exception ex) {
			if (repository != null) {
				repository.endTrans(true);
			}
			umlPackage = null;
			ex.printStackTrace();

			throw new ExceptionRuntimeError("MDR Exception: " + ex.getMessage());
		}
		fireLoadEvent();
	}

	/**
	 * Removes any URL references which contain the string
	 * "argouml.org/profiles" from the temporary xmi-file. This prevents fail of
	 * loading a model in offline-mode or restricted networks, because the
	 * refences won't be resolved. These references are accidently included
	 * during model creation with ArgoUML version 0.28+ (Known bug).
	 * 
	 * Procedure: The given file is searched line by line and lines are deleted
	 * if a match is found. A tempory file is writen and replaces the original
	 * file as result.
	 */
	public void removeURLReferencesFromXMI(File file) {

		try {
			File inFile = new File(file.getAbsolutePath());

			if (!inFile.isFile()) {
				System.out.println("Given file does not exist (Parameter)!");
				return;
			}

			// The tempFile
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

			BufferedReader reader = new BufferedReader(new FileReader(file));
			PrintWriter writer = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			// Read line by line. Remove lines with matching string. IndexOf
			// returns -1 in case of mismatch.
			while ((line = reader.readLine()) != null) {
				// @bugfix buerger
				// old
				// if ((line.indexOf("UML") != -1) &&
				// (line.indexOf("href = 'http://argouml.org/profiles/uml14") !=
				// -1) )
				// {
				// }
				// Apparantly we can remove every http-reference. For safety, we
				// let the argouml-string in here.
				// It should make no problems if we would remove every
				// href-tags.

				if ((line.indexOf("UML") != -1)
						&& (line.indexOf("href = 'http://argouml.org/") != -1)) {
				} else {
					writer.println(line);
					writer.flush();
				}
			}
			writer.close();
			reader.close();

			// Deleting the given file
			if (!inFile.delete()) {
				System.out.println("File was not deleted!");
				return;
			}

			// Renaming the tempFile to given file's name
			if (!tempFile.renameTo(inFile))
				System.out.println("File was not renamed!");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This method walks the document and removes all nodes of the specified
	 * type and specified name. If name is null, then the node is removed if the
	 * type matches.
	 * 
	 * @param node
	 * @param nodeType
	 * @param name
	 */
	public static void removeAll(Node node, short nodeType, String name) {
		if (node.getNodeType() == nodeType
				&& (name == null || node.getNodeName().equals(name))) {
			node.getParentNode().removeChild(node);
		} else {
			// Visit the children
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				removeAll(list.item(i), nodeType, name);
			}
		}
	}

	public org.omg.uml.UmlPackage getUmlPackage() {
		return umlPackage;
	}

	private void clearRepository() {
		if (manager == null) {
			File repositoryDirectory = new File(repositoryPath);
			repositoryDirectory.mkdirs();
			File f1 = new File(repositoryPath + ".btd");
			File f2 = new File(repositoryPath + ".btx");
			f1.delete();
			f2.delete();
		}
	}

	private MofPackage findUmlPackage(ModelPackage extent) {
		for (Iterator it = extent.getMofPackage().refAllOfClass().iterator(); it
				.hasNext();) {
			ModelElement temp = (ModelElement) it.next();
			String packageName = temp.getName();
			if (packageName.equals(PACKAGE)) {
				return (MofPackage) temp;
			}
		}
		return null;
	}

	/**
	 * Writes the current model into a specified file. If the model was
	 * extracted from an archive, the archive is recreated with the new .xmi
	 * file. Otherwise, a simple .xmi file is outputted.
	 * 
	 * @param ref
	 *            the model to be outputted
	 * @param file
	 *            the file where the model should be written.
	 */
	public void write(RefPackage ref, File file) {
		File xmiFile = file;
		String xmiFileName = null;
		// If the user provided an archive, we should return him... an archive.
		if (zargoFile != null) {
			try {
				xmiFileName = zargoFile.getName().split(".zargo")[0] + ".xmi";
				xmiFile = File.createTempFile(zargoFile.getName(), "xmi");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writeToXmi(ref, xmiFile);
		// Now we need to put the generated xmi file into the archive, if the
		// user loaded it.
		if (zargoFile != null) {
			byte[] buf = new byte[BUFFERSIZE];
			ZipInputStream zin;
			ZipOutputStream zout;
			BufferedInputStream bin;
			try {
				zin = new ZipInputStream(new FileInputStream(zargoFile));
				zout = new ZipOutputStream(new FileOutputStream(file));
				bin = new BufferedInputStream(new FileInputStream(xmiFile));
			} catch (FileNotFoundException e) {
				System.out.println("Can't find file : " + e.getMessage());
				return;
			}
			try {
				ZipEntry entry = zin.getNextEntry();
				while (entry != null) {
					String name = entry.getName();
					if (name.equals(xmiFileName)) {
						zout.putNextEntry(new ZipEntry(name));
						System.out.println("writing new xmi file");
						// Transfer bytes from the xmi file to the output file
						int len;
						while ((len = bin.read(buf)) > 0) {
							zout.write(buf, 0, len);
						}
					} else {
						System.out.println("writing old file " + name);
						zout.putNextEntry(new ZipEntry(name));
						int len;
						while ((len = zin.read(buf)) > 0) {
							// Transfer bytes from the ZIP file to the output
							// (zip) file
							zout.write(buf, 0, len);
						}
					}
					zout.closeEntry();
					entry = zin.getNextEntry();
				}
				zin.close();
				bin.close();
				xmiFile.delete();
				zout.flush();
				zout.close();
			} catch (IOException e) {
				System.out.println("Problem while writing archive : "
						+ e.getMessage());
				return;
			}
		}
		return;
	}

	/**
	 * Exports a model to the xmi format and saves it in a file.
	 * 
	 * @param ref
	 *            the model to be exported
	 * @param file
	 *            the file the xmi export should be saved to.
	 */
	public void writeToXmi(RefPackage ref, File file) {
		OutputStream output = null;
		try {
			output = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmiWriter writer = new XMIWriterImpl();
		try {
			writer.write(output, ref, "1.4");
		} catch (IOException e) {
			throw new ExceptionRuntimeError("Can't write output to the file "
					+ file.getAbsolutePath() + file.getName());
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				throw new ExceptionRuntimeError("Can't close the file "
						+ file.getAbsolutePath() + file.getName());
			}
		}
		if (zargoFile != null) {
			// now we need to update the xmi.ids so that they match the ones
			// used by ArgoUML in the .pgml files
			try {
				XmiIDSwitch.swapIds(file);
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setZipFileLoaded(boolean zipFileLoaded) {
		this.zipFileLoaded = zipFileLoaded;
	}

	public boolean isZipFileLoaded() {
		return zipFileLoaded;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public boolean isModified() {
		return modified;
	}

	private String resourceRoot;
	private String repositoryPath;
	private String umlDefinitionPath;

	private boolean empty = true;
	private boolean modified = false;

	private EventListenerList listenerList = new EventListenerList();
	private org.omg.uml.UmlPackage umlPackage = null;

	private Integer umlExtentCount;

	private MDRManager manager = null;
	private MDRepository repository = null;
	private ModelPackage extentMetaModel = null;
	private MofPackage packageMetaUml = null;

	private boolean zipFileLoaded = false;
	private String loadedFileName;
}
