package jmri.jmrit.operations.automation.actions;

import jmri.jmrit.operations.trains.Train;

public class TerminateTrainAction extends Action {

    private static final int _code = ActionCodes.TERMINATE_TRAIN;

    @Override
    public int getCode() {
        return _code;
    }

    @Override
    public String getName() {
        return Bundle.getMessage("TerminateTrain");
    }

    @Override
    public void doAction() {
        if (getAutomationItem() != null) {
            Train train = getAutomationItem().getTrain();
            if (train != null && train.isBuilt()) {
                train.terminate();
            }
            // now show message if there's one
            finishAction();
        }
    }

    @Override
    public void cancelAction() {
        // no cancel for this action     
    }

}
