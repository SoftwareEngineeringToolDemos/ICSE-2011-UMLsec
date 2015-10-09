package tum.umlsec.viki.framework.mdr;

import java.io.File;
import java.util.EventListener;
import java.util.EventObject;

import javax.jmi.reflect.RefPackage;

import org.omg.uml.UmlPackage;

/**
 * @author Pasha Shabalin
 */
public interface IMdrContainer {

	/**
	 * base class for all events fired by the MdrContainer
	 * 
	 * @author Pasha Shabalin
	 * 
	 */
	public abstract class MdrContainerEvent extends EventObject {
		public MdrContainerEvent(Object _source) {
			super(_source);
		}
	}

	/**
	 * this event is fired when a new file is loaded into repository
	 * 
	 * @author Pasha Shabalin
	 * 
	 */
	public class LoadEvent extends MdrContainerEvent {
		public LoadEvent(Object _source) {
			super(_source);
		}
	}

	/**
	 * this event is fired when the repository is cleared
	 * 
	 * @author Pasha Shabalin
	 * 
	 */
	public class ClearEvent extends MdrContainerEvent {
		public ClearEvent(Object _source) {
			super(_source);
		}
	}

	/**
	 * classes, interested in events from the MdrContainer, must implement this
	 * interface
	 * 
	 * @author Pasha Shabalin
	 * 
	 */
	public interface IMdrContainerListener extends EventListener {
		void onClearMdr(ClearEvent e);

		void onLoadMdr(LoadEvent e);
	}

	void addListener(IMdrContainerListener l);

	void removeListener(IMdrContainerListener l);

	boolean isEmpty();

	boolean isModified();

	UmlPackage getUmlPackage();

	public void write(RefPackage ref, File file);
}
