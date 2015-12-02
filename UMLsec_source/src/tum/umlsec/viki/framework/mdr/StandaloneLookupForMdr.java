package tum.umlsec.viki.framework.mdr;

import java.util.ArrayList;
import java.util.Iterator;

import org.netbeans.lib.jmi.mapping.JMIMapperImpl;
import org.netbeans.lib.jmi.xmi.XMISaxReaderImpl;
import org.netbeans.lib.jmi.xmi.XMIWriterImpl;
import org.netbeans.lib.jmi.xmi.XmiSAXReader;
import org.netbeans.mdr.NBMDRManagerImpl;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;



public class StandaloneLookupForMdr extends Lookup {

    private void init() {
        instances = new ArrayList();

        instances.add(new NBMDRManagerImpl());
        instances.add(new XMISaxReaderImpl());
        instances.add(new JMIMapperImpl());
        instances.add(new XMIWriterImpl());
        instances.add(new XmiSAXReader());
    }

    public StandaloneLookupForMdr() {
    }

    public Object lookup(Class cls) {
        if(instances == null) {
            init();
        }

        for (Iterator it = instances.iterator(); it.hasNext();) {
            Object instance = it.next();
            if (cls.isAssignableFrom(instance.getClass())) {
                return instance;
            }
        }
        return null;
    }

    public Lookup.Result lookup(Lookup.Template template) {
        if(instances == null) {
            init();
        }

        return new Result(lookup(template.getType()));
    }

    private static class Result extends Lookup.Result {
        private final ArrayList result = new ArrayList();

        public Result(Object o) {
            if (o != null) result.add(o);
        }

        /** Registers a listener that is invoked when there is a possible
         * change in this result.
         *
         * @param l the listener to add
         */
        public void addLookupListener(LookupListener l) {
            // this lookup never changes so nothing need to be registered
        }

        /** Get all instances in the result.
         * @return collection of all instances
         */
        public java.util.Collection allInstances() {
            return result;
        }

        /** Unregisters a listener previously added.
         * @param l the listener to remove
         */
        public void removeLookupListener(LookupListener l) {
            // nothing was registered
        }
    }

    private ArrayList instances = null;
}

