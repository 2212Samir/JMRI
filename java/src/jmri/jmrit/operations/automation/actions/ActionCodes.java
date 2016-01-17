package jmri.jmrit.operations.automation.actions;

/**
 * Action codes for automation
 *
 * @author Daniel Boudreau Copyright (C) 2016
 * @version $Revision: 22156 $
 *
 */
public class ActionCodes {

    // lower byte used in the construction of action codes
    public static final int ENABLE_TRAINS = 0x1;
    public static final int ENABLE_ROUTES = 0x2;
    public static final int ENABLE_OK_MESSAGE = 0x4;
    public static final int ENABLE_FAIL_MESSAGE = 0x8;
    public static final int ENABLE_AUTOMATION_LIST = 0x10;
    public static final int ENABLE_GOTO_LIST = 0x20;

    // codes use upper byte   
    public static final int CODE_MASK = 0xFF00; // upper byte only
    
    public static final int NO_ACTION = 0x0000 + ENABLE_OK_MESSAGE;
    
    public static final int BUILD_TRAIN = 0x0100 + ENABLE_TRAINS + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE;
    public static final int BUILD_TRAIN_IF_SELECTED = 0x0200 + ENABLE_TRAINS + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE;
    public static final int PRINT_TRAIN_MANIFEST = 0x0300 + ENABLE_TRAINS + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE;
    public static final int PRINT_TRAIN_MANIFEST_IF_SELECTED = 0x0400 + ENABLE_TRAINS + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE;
    public static final int MOVE_TRAIN = 0x0500 + ENABLE_TRAINS + ENABLE_ROUTES + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE;
    public static final int TERMINATE_TRAIN = 0x0600 + ENABLE_TRAINS + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE;
    public static final int WAIT_FOR_TRAIN = 0x0700 + ENABLE_TRAINS + ENABLE_ROUTES + ENABLE_OK_MESSAGE;
    
    public static final int UPDATE_SWITCHLIST = 0x1000 + ENABLE_OK_MESSAGE;
    public static final int PRINT_SWITCHLIST = 0x1100 + ENABLE_OK_MESSAGE;
    
    public static final int STEP_AUTOMATION = 0x3000 + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE + ENABLE_AUTOMATION_LIST;
    public static final int RUN_AUTOMATION = 0x3100 + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE + ENABLE_AUTOMATION_LIST;
    public static final int STOP_AUTOMATION = 0x3200 + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE + ENABLE_AUTOMATION_LIST;
    public static final int RESUME_AUTOMATION = 0x3300 + ENABLE_OK_MESSAGE + ENABLE_FAIL_MESSAGE + ENABLE_AUTOMATION_LIST;
    
    public static final int MESSAGE = 0x4000 + ENABLE_OK_MESSAGE;
    public static final int WAIT_MESSAGE_OK = 0x4100 + ENABLE_OK_MESSAGE;
    public static final int WAIT_MESSAGE_YES_NO = 0x4200 + ENABLE_OK_MESSAGE;
    public static final int IF_MESSAGE_NO = 0x4300 + ENABLE_OK_MESSAGE + ENABLE_GOTO_LIST;
    
    public static final int HALT_ACTION = 0xFF00 + ENABLE_OK_MESSAGE;

}
