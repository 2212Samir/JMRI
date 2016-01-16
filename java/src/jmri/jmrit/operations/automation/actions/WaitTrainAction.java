package jmri.jmrit.operations.automation.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import jmri.jmrit.operations.routes.RouteLocation;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.operations.trains.Train;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitTrainAction extends Action implements PropertyChangeListener {

    private static final int _code = ActionCodes.WAIT_FOR_TRAIN;

    @Override
    public int getCode() {
        return _code;
    }

    @Override
    public String getName() {
        return Bundle.getMessage("WaitForTrain");
    }

    @Override
    public void doAction() {
        if (getAutomationItem() != null) {
            Train train = getAutomationItem().getTrain();
            if (train != null) {
                train.addPropertyChangeListener(this);
            }
        }
    }
    
    private void trainUpdate() {
        if (getAutomationItem() != null) {
            Train train = getAutomationItem().getTrain();
            RouteLocation rl = getAutomationItem().getRouteLocation();
            if (rl != null && rl != train.getCurrentLocation()) {
                    return; // haven't reached this location continue waiting
            }
            // now show message if there's one
            train.removePropertyChangeListener(this);
            finishAction();
        }
    }

    @Override
    public void cancelAction() {
        if (getAutomationItem() != null) {
            Train train = getAutomationItem().getTrain();
            train.removePropertyChangeListener(this);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Control.showProperty)
            log.debug("Property change: ({}) old: ({}) new: ({})", evt.getPropertyName(), evt.getOldValue(), evt
                    .getNewValue());
        if (evt.getPropertyName().equals(Train.TRAIN_MOVE_COMPLETE_CHANGED_PROPERTY)) {
            trainUpdate();
        }   
    }
    
    static Logger log = LoggerFactory.getLogger(WaitTrainAction.class.getName());

}
