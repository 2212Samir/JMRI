package jmri.jmrit.operations.automation.actions;

import jmri.jmrit.operations.automation.Automation;

public class ResumeAutomationAction extends Action {

    private static final int _code = ActionCodes.RESUME_AUTOMATION;

    @Override
    public int getCode() {
        return _code;
    }

    @Override
    public String toString() {
        return Bundle.getMessage("ResumeAutomation");
    }

    @Override
    public void doAction() {
        if (getAutomationItem() != null) {
            Automation automation = getAutomationItem().getAutomation();
            if (automation != null) {
                automation.resume();
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
