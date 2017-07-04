/*
 * Copyright (c) 2008, Institute for Software Integrated Systems, Vanderbilt University
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 *
 * IN NO EVENT SHALL THE VANDERBILT UNIVERSITY BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE VANDERBILT
 * UNIVERSITY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE VANDERBILT UNIVERSITY SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE VANDERBILT UNIVERSITY HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * 
 * @author Himanshu Neema
 */

package org.cpswt.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Logger;

/**
 * A replacement for
 * {@link java.beans.PropertyChangeSupport PropertyChangeSupport} for using weak
 * references to listeners in order to avoid memory leaks.
 * 
 * @author Himanshu Neema
 */
public class WeakPropertyChangeSupport {
    private static Logger log = Logger
            .getLogger(WeakPropertyChangeSupport.class.getName());

    private Object eventsGenerator;

    private ArrayList<WeakReference<PropertyChangeListener>> listeners;

    private ReferenceQueue<PropertyChangeListener> queue = new ReferenceQueue<PropertyChangeListener>();

    private HashMap<String, WeakPropertyChangeSupport> children = new HashMap<String, WeakPropertyChangeSupport>();

    public WeakPropertyChangeSupport(Object eventGenerator) {
        if (eventGenerator == null) {
            throw new NullPointerException("Event generator object is null");
        }
        this.eventsGenerator = eventGenerator;
    }

    /**
     * Add a PropertyChangeListener (for all properties)
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener li) {
        if (li == null) {
            throw new NullPointerException("null listener");
        }
        if (listeners == null) {
            listeners = new ArrayList<WeakReference<PropertyChangeListener>>();
        } else if (listeners.contains(li)) {
            // Already subscribed
            return;
        }

        WeakReference<PropertyChangeListener> wr = new WeakReference<PropertyChangeListener>(
                li, queue);
        listeners.add(wr);

        log.finer("" + li.getClass() + " is listening to "
                + eventsGenerator.getClass());
    }

    public synchronized void addPropertyChangeListener(String propertyName,
            PropertyChangeListener li) {
        if (propertyName == null) {
            throw new NullPointerException("null propertyName");
        }

        WeakPropertyChangeSupport child;
        if (children.containsKey(propertyName)) {
            child = (WeakPropertyChangeSupport) children.get(propertyName);
        } else {
            child = new WeakPropertyChangeSupport(eventsGenerator);
            children.put(propertyName, child);
        }
        child.addPropertyChangeListener(li);
        log.finer("" + li.getClass() + " is listening to "
                + eventsGenerator.getClass() + " for " + propertyName);
    }

    /**
     * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     */
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener li) {
        if ((listeners == null) || (li == null)) {
            return;
        }
        cleanUp();

        ListIterator<WeakReference<PropertyChangeListener>> it = listeners
                .listIterator();
        while (it.hasNext()) {
            WeakReference<PropertyChangeListener> wr = it.next();
            if (li.equals(wr.get())) {
                it.remove();
            }
        }
    }

    public synchronized void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        WeakPropertyChangeSupport child = (WeakPropertyChangeSupport) children
                .get(propertyName);
        if (child == null) {
            // No listener registered for the given propertyName
            return;
        }
        child.removePropertyChangeListener(listener);
    }

    /**
     * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners()
     */
    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        ArrayList<PropertyChangeListener> returnList = new ArrayList<PropertyChangeListener>();

        // Add all the PropertyChangeListeners
        if (listeners != null) {
            returnList.addAll(createListenerList());
        }

        // Add the children's listeners, too
        for (Iterator<WeakPropertyChangeSupport> it = children.values()
                .iterator(); it.hasNext();) {
            WeakPropertyChangeSupport child = it.next();
            PropertyChangeListener[] childListeners = child
                    .getPropertyChangeListeners();
            for (int index = childListeners.length - 1; index >= 0; index--) {
                returnList.add(childListeners[index]);
            }
        }

        return (PropertyChangeListener[]) (returnList
                .toArray(new PropertyChangeListener[0]));
    }

    public void firePropertyChange(PropertyChangeEvent evt) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        String propertyName = evt.getPropertyName();
        if ((oldValue != null) && (newValue != null)
                && oldValue.equals(newValue)) {
            return;
        }

        ArrayList<PropertyChangeListener> liLocal = createListenerList();

        for (Iterator<PropertyChangeListener> it = liLocal.iterator(); it
                .hasNext();) {
            PropertyChangeListener li = it.next();
            li.propertyChange(evt);
        }

        // Notify listeners subscribed to this specific event
        if (children.containsKey(propertyName)) {
            WeakPropertyChangeSupport child = (WeakPropertyChangeSupport) children
                    .get(propertyName);
            child.firePropertyChange(evt);
        }
    }

    /**
     * Creates a listener list from the weak reference listener list (which is a
     * field). Used for local copies and return values.
     */
    private synchronized ArrayList<PropertyChangeListener> createListenerList() {
        cleanUp();

        ArrayList<PropertyChangeListener> liLocal = new ArrayList<PropertyChangeListener>();
        if (listeners == null) {
            return liLocal;
        }

        Iterator<WeakReference<PropertyChangeListener>> it = listeners
                .iterator();
        while (it.hasNext()) {
            WeakReference<PropertyChangeListener> wr = it.next();
            PropertyChangeListener li = (PropertyChangeListener) wr.get();
            if (li == null) {
                // Should have been cleaned up
                continue;
            }
            liLocal.add(li);
        }
        return liLocal;
    }

    public synchronized boolean hasListeners(String propertyName) {
        if ((listeners != null) && !listeners.isEmpty()) {
            // there is a generic listener
            return true;
        }

        WeakPropertyChangeSupport child = (WeakPropertyChangeSupport) children
                .get(propertyName);
        if ((child != null) && (child.listeners != null)) {
            return !child.listeners.isEmpty();
        }
        return false;
    }

    /**
     * Clean up broken weak references.
     */
    private void cleanUp() {
        WeakReference<PropertyChangeListener> wr = (WeakReference<PropertyChangeListener>) queue
                .poll();
        while (wr != null) {
            listeners.remove(wr);
            wr = (WeakReference<PropertyChangeListener>) queue.poll();
        }
    }

    public void firePropertyChange(String propertyName, int oldValue,
            int newValue) {
        if (oldValue == newValue) {
            return;
        }
        firePropertyChange(propertyName, new Integer(oldValue), new Integer(
                newValue));
    }

    public void firePropertyChange(String propertyName, boolean oldValue,
            boolean newValue) {
        if (oldValue == newValue) {
            return;
        }
        firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean
                .valueOf(newValue));
    }

    public void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        if ((oldValue != null) && (newValue != null)
                && oldValue.equals(newValue)) {
            return;
        }

        PropertyChangeEvent evt = new PropertyChangeEvent(eventsGenerator,
                propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }
}
