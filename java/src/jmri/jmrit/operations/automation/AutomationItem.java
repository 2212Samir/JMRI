package jmri.jmrit.operations.automation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import jmri.jmrit.operations.automation.actions.Action;
import jmri.jmrit.operations.automation.actions.ActionCodes;
import jmri.jmrit.operations.automation.actions.BuildTrainAction;
import jmri.jmrit.operations.automation.actions.BuildTrainIfSelectedAction;
import jmri.jmrit.operations.automation.actions.HaltAction;
import jmri.jmrit.operations.automation.actions.MoveTrainAction;
import jmri.jmrit.operations.automation.actions.NoAction;
import jmri.jmrit.operations.automation.actions.PrintTrainManifestAction;
import jmri.jmrit.operations.automation.actions.PrintTrainManifestIfSelectedAction;
import jmri.jmrit.operations.automation.actions.ResumeAutomationAction;
import jmri.jmrit.operations.automation.actions.RunAutomationAction;
import jmri.jmrit.operations.automation.actions.StopAutomationAction;
import jmri.jmrit.operations.automation.actions.TerminateTrainAction;
import jmri.jmrit.operations.automation.actions.UpdateSwitchListAction;
import jmri.jmrit.operations.automation.actions.WaitTrainAction;
import jmri.jmrit.operations.routes.RouteLocation;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.operations.trains.Train;
import jmri.jmrit.operations.trains.TrainManager;
import jmri.jmrit.operations.trains.TrainManagerXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents one automation item of a automation
 *
 * @author Daniel Boudreau Copyright (C) 2016
 * @version $Revision$
 */
public class AutomationItem implements java.beans.PropertyChangeListener {

    public static final String NONE = ""; // NOI18N

    protected String _id = NONE;
    protected int _sequenceId = 0; // used to determine order in automation
    protected Action _action = null;
    protected Train _train = null;
    protected RouteLocation _routeLocation = null;
    protected String _automationId = NONE;
    protected String _message = NONE;

    public static final String DISPOSE = "automationItemDispose"; // NOI18N

    /**
     *
     * @param id
     */
    public AutomationItem(String id) {
        log.debug("New automation item id: {}", id);
        _id = id;
        setAction(new NoAction()); // the default
    }

    public String getId() {
        return _id;
    }

    public String toString() {
        return getId(); // for property changes
    }

    public int getSequenceId() {
        return _sequenceId;
    }

    public void setSequenceId(int sequence) {
        // property change not needed
        _sequenceId = sequence;
    }

    public void setAction(Action action) {
        Action old = _action;
        _action = action;
        if (action != null) {
            action.setAutomationItem(this); // associate action with this item
        }
        if (old != action) {
            setDirtyAndFirePropertyChange("AutomationItemActionChange", old, action); // NOI18N
        }
    }

    public Action getAction() {
        return _action;
    }

    public int getActionCode() {
        if (getAction() != null) {
            return getAction().getCode();
        }
        return ActionCodes.NO_ACTION;
    }

    public void doAction() {
        if (getAction() != null) {
            getAction().doAction();
        }
    }

    public void setTrain(Train train) {
        Train old = _train;
        _train = train;
        if (old != train) {
            setDirtyAndFirePropertyChange("AutomationItemTrainChange", old, train); // NOI18N
        }
    }

    public Train getTrain() {
        if (getAction() != null && getAction().isTrainMenuEnabled()) {
            return _train;
        }
        return null;
    }

    public void setRouteLocation(RouteLocation rl) {
        RouteLocation old = _routeLocation;
        _routeLocation = rl;
        if (old != rl) {
            setDirtyAndFirePropertyChange("AutomationItemRouteLocationChange", old, rl); // NOI18N
        }
    }

    public RouteLocation getRouteLocation() {
        if (getAction() != null && getAction().isRouteMenuEnabled()) {
            return _routeLocation;
        }
        return null;
    }

    /**
     * The automation for actions, not the automation associated with this item.
     * 
     * @param automation
     */
    public void setAutomation(Automation automation) {
        Automation old = AutomationManager.instance().getAutomationById(_automationId);
        if (automation != null)
            _automationId = automation.getId();
        else
            _automationId = NONE;
        if (old != automation) {
            setDirtyAndFirePropertyChange("AutomationItemAutomationChange", old, automation); // NOI18N
        }
    }

    /**
     * The automation for actions, not the automation associated with this item.
     * 
     * @return Automation for this action
     */
    public Automation getAutomation() {
        if (getAction() != null && getAction().isAutomationMenuEnabled()) {
            return AutomationManager.instance().getAutomationById(_automationId);
        }
        return null;
    }

    public void setMessage(String message) {
        String old = _message;
        if (!old.equals(message)) {
            setDirtyAndFirePropertyChange("AutomationItemMessageChange", old, message); // NOI18N
        }
        _message = message;
    }

    public String getMessage() {
        return _message;
    }

    public void copyItem(AutomationItem item) {
        setMessage(item.getMessage());
    }

    /**
     * Gets a list of all known automation actions
     * 
     * @return list of automation actions
     */
    public List<Action> getActionList() {
        List<Action> list = new ArrayList<Action>();
        list.add(new NoAction());
        list.add(new BuildTrainAction());
        list.add(new BuildTrainIfSelectedAction());
        list.add(new PrintTrainManifestAction());
        list.add(new PrintTrainManifestIfSelectedAction());
        list.add(new MoveTrainAction());
        list.add(new TerminateTrainAction());
        list.add(new WaitTrainAction());
        list.add(new UpdateSwitchListAction());
        list.add(new RunAutomationAction());
        list.add(new ResumeAutomationAction());
        list.add(new StopAutomationAction());
        list.add(new HaltAction());
        return list;
    }

    public JComboBox<Action> getActionComboBox() {
        JComboBox<Action> box = new JComboBox<>();
        for (Action action : getActionList())
            box.addItem(action);
        return box;
    }

    public Action getActionByCode(int code) {
        for (Action action : getActionList()) {
            if (action.getCode() == code)
                return action;
        }
        return null;
    }

    public void dispose() {
        setDirtyAndFirePropertyChange(DISPOSE, null, DISPOSE);
    }

    /**
     * Construct this Entry from XML. This member has to remain synchronized
     * with the detailed DTD in operations-config.xml
     *
     * @param e Consist XML element
     */
    public AutomationItem(org.jdom2.Element e) {
        // if (log.isDebugEnabled()) log.debug("ctor from element "+e);
        org.jdom2.Attribute a;
        if ((a = e.getAttribute(Xml.ID)) != null) {
            _id = a.getValue();
        } else {
            log.warn("no id attribute in Automation Item element when reading operations");
        }
        if ((a = e.getAttribute(Xml.SEQUENCE_ID)) != null) {
            _sequenceId = Integer.parseInt(a.getValue());
        }
        if ((a = e.getAttribute(Xml.ACTION_CODE)) != null) {
            setAction(getActionByCode(Integer.decode(a.getValue())));
        }
        if ((a = e.getAttribute(Xml.TRAIN_ID)) != null) {
            _train = TrainManager.instance().getTrainById(a.getValue());
        }
        if ((a = e.getAttribute(Xml.ROUTE_LOCATION_ID)) != null && getTrain() != null) {
            _routeLocation = getTrain().getRoute().getLocationById(a.getValue());
        }
        if ((a = e.getAttribute(Xml.AUTOMATION_ID)) != null) {
            // in the process of loading automations, so we can't get them now, save id and get later.
            _automationId = a.getValue();
        }
        if ((a = e.getAttribute(Xml.MESSAGE)) != null) {
            _message = a.getValue();
        }
    }

    /**
     * Create an XML element to represent this Entry. This member has to remain
     * synchronized with the detailed DTD in operations-trains.dtd.
     *
     * @return Contents in a JDOM Element
     */
    public org.jdom2.Element store() {
        org.jdom2.Element e = new org.jdom2.Element(Xml.ITEM);
        e.setAttribute(Xml.ID, getId());
        e.setAttribute(Xml.SEQUENCE_ID, Integer.toString(getSequenceId()));
        e.setAttribute(Xml.ACTION_CODE, "0x" + Integer.toHexString(getActionCode()));
        if (getTrain() != null) {
            e.setAttribute(Xml.TRAIN_ID, getTrain().getId());
            if (getRouteLocation() != null) {
                e.setAttribute(Xml.ROUTE_LOCATION_ID, getRouteLocation().getId());
            }
        }
        if (getAutomation() != null) {
            e.setAttribute(Xml.AUTOMATION_ID, getAutomation().getId());
        }
        e.setAttribute(Xml.MESSAGE, getMessage());
        return e;
    }

    public void propertyChange(java.beans.PropertyChangeEvent e) {
        if (Control.showProperty) {
            log.debug("AutomationItem id ({}) sees property change: ({}) old: ({}) new: ({})",
                    getId(), e.getPropertyName(), e.getOldValue(), e.getNewValue()); // NOI18N
        }
    }

    java.beans.PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this);

    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    protected void setDirtyAndFirePropertyChange(String p, Object old, Object n) {
        // set dirty
        TrainManagerXml.instance().setDirty(true);
        pcs.firePropertyChange(p, old, n);
    }

    static Logger log = LoggerFactory.getLogger(AutomationItem.class.getName());

}
