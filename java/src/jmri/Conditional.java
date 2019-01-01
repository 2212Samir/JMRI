package jmri;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;

/**
 * A Conditional is layout control logic, consisting of a logical expression and
 * an action.
 * <p>
 * A Conditional does not exist on its own, but is part of a Logix. The system
 * name of each Conditional is set automatically when the conditional is
 * created. It begins with the system name of its parent Logix. There is no
 * Conditional Table. Conditionals are created, editted, and deleted via the
 * Logix Table.
 * <p>
 * A Conditional has a "state", which changes depending on whether its logical
 * expression calculates to TRUE or FALSE. The "state" may not be changed by the
 * user. It only changes in response to changes in the "state variables" used in
 * its logical expression.
 * <p>
 * Listeners may be set to monitor a change in the state of a conditional.
 *
 * <hr>
 * This file is part of JMRI.
 * <p>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * <p>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * @author Dave Duchamp Copyright (C) 2007, 2008
 * @author Pete Cressman Copyright (C) 2009, 2010, 2011
 * @author Matthew Harris copyright (c) 2009
 */
public interface Conditional extends NamedBean {

    static final ResourceBundle rbx = ResourceBundle.getBundle("jmri.jmrit.conditional.ConditionalBundle");
    static final ResourceBundle rbxWarrant = ResourceBundle.getBundle("jmri.jmrit.logix.WarrantBundle");

    // states
    enum State {
        UNKNOWN(NamedBean.UNKNOWN, "StateUnknown"),
        FALSE(Conditional.FALSE, "StateFalse"),
        TRUE(Conditional.TRUE, "StateTrue");

        private final int _state;
        private final String _bundleKey;

        private State(int state, String bundleKey) {
            _state = state;
            _bundleKey = bundleKey;
        }

        public int getIntValue() {
            return _state;
        }

        public static State getOperatorFromIntValue(int stateInt) {
            for (State state : State.values()) {
                if (state.getIntValue() == stateInt) {
                    return state;
                }
            }

            throw new IllegalArgumentException("State is unknown");
        }

        @Override
        public String toString() {
            return Bundle.getMessage(_bundleKey);
        }
    }

    /**
     * @deprecated since 4.7.1; use {@link jmri.NamedBean#UNKNOWN} instead
     */
    @Deprecated // 4.7.1
    public static final int UNKNOWN = NamedBean.UNKNOWN;
    public static final int FALSE = 0x02;
    public static final int TRUE = 0x04;

    // logic operators used in antecedent
    static final int ALL_AND = 0x01;
    static final int ALL_OR = 0x02;
    static final int MIXED = 0x03;

    public enum Operator {
        NONE,
        AND,
        OR;

        // This method is used by DefaultConditionalManagerXml.store() for backward compatibility
        public int getIntValue() {
            switch (this) {
                case NONE: return OPERATOR_NONE;
                case AND: return OPERATOR_AND;
                case OR: return OPERATOR_OR;
                default: throw new IllegalArgumentException(String.format("operator %s is unknown", this.name()));
            }
        }

        // This method is used by DefaultConditionalManagerXml.loadConditionals() for backward compatibility
        public static Operator getOperatorFromIntValue(int opern) {
            switch (opern) {
                case OPERATOR_AND: return Operator.AND;
                case OPERATOR_NOT: return Operator.NONE;
                case OPERATOR_AND_NOT: return Operator.AND;
                case OPERATOR_NONE: return Operator.NONE;
                case OPERATOR_OR: return Operator.OR;
                case OPERATOR_OR_NOT: return Operator.OR;
                default: throw new IllegalArgumentException(String.format("operator %d is unknown", opern));
            }
        }
    }

    // state variable definitions. Keep these since they are needed
    // for backward compatibility in DefaultConditionalManagerXml.
    // But they are not used elsewhere.
    static final int OPERATOR_AND = 1;
    static final int OPERATOR_NONE = 4;
    static final int OPERATOR_OR = 5;
    /**
     * @deprecated since 4.13.4; It is not stored in the XML file since 4.13.4.
     */
    @Deprecated // 4.7.1
    public static final int OPERATOR_NOT = 2;
    /**
     * @deprecated since 4.13.4; It is not stored in the XML file since 4.13.4.
     */
    @Deprecated // 4.7.1
    public static final int OPERATOR_AND_NOT = 3;
    /**
     * @deprecated since 4.13.4; It is not stored in the XML file since 4.13.4.
     */
    @Deprecated // 4.7.1
    public static final int OPERATOR_OR_NOT = 6;

    // items
    enum Action {
        NONE(ACTION_NONE, ItemType.NONE, ""), // NOI18N
        SET_TURNOUT(ACTION_SET_TURNOUT, ItemType.TURNOUT, rbx.getString("ActionSetTurnout")), // NOI18N
        // allowed settings for turnout are Thrown and Closed (in data)
        SET_SIGNAL_APPEARANCE(ACTION_SET_SIGNAL_APPEARANCE, ItemType.SIGNALHEAD, rbx.getString("ActionSetSignal")), // NOI18N
        // allowed settings for signal head are the seven Appearances (in data)
        SET_SIGNAL_HELD(ACTION_SET_SIGNAL_HELD, ItemType.SIGNALHEAD, rbx.getString("ActionSetSignalHeld")), // NOI18N
        CLEAR_SIGNAL_HELD(ACTION_CLEAR_SIGNAL_HELD, ItemType.SIGNALHEAD, rbx.getString("ActionClearSignalHeld")), // NOI18N
        SET_SIGNAL_DARK(ACTION_SET_SIGNAL_DARK, ItemType.SIGNALHEAD, rbx.getString("ActionSetSignalDark")), // NOI18N
        SET_SIGNAL_LIT(ACTION_SET_SIGNAL_LIT, ItemType.SIGNALHEAD, rbx.getString("ActionSetSignalLit")), // NOI18N
        TRIGGER_ROUTE(ACTION_TRIGGER_ROUTE, ItemType.OTHER, rbx.getString("ActionTriggerRoute")), // NOI18N
        SET_SENSOR(ACTION_SET_SENSOR, ItemType.SENSOR, rbx.getString("ActionSetSensor")), // NOI18N
        // allowed settings for sensor are active and inactive (in data)
        DELAYED_SENSOR(ACTION_DELAYED_SENSOR, ItemType.SENSOR, rbx.getString("ActionDelayedSensor")), // NOI18N
        // allowed settings for timed sensor are active and inactive (in data)
        //   time in seconds before setting sensor should be in delay
        SET_LIGHT(ACTION_SET_LIGHT, ItemType.LIGHT, rbx.getString("ActionSetLight")), // NOI18N
        // allowed settings for light are ON and OFF (in data)
        SET_MEMORY(ACTION_SET_MEMORY, ItemType.MEMORY, rbx.getString("ActionSetMemory")), // NOI18N
        // text to set into the memory variable should be in string
        ENABLE_LOGIX(ACTION_ENABLE_LOGIX, ItemType.LOGIX, rbx.getString("ActionEnableLogix")), // NOI18N
        DISABLE_LOGIX(ACTION_DISABLE_LOGIX, ItemType.LOGIX, rbx.getString("ActionDisableLogix")), // NOI18N
        PLAY_SOUND(ACTION_PLAY_SOUND, ItemType.AUDIO, rbx.getString("ActionPlaySound")), // NOI18N
        // reference to sound should be in string
        RUN_SCRIPT(ACTION_RUN_SCRIPT, ItemType.SCRIPT, rbx.getString("ActionRunScript")), // NOI18N
        // reference to script should be in string
        DELAYED_TURNOUT(ACTION_DELAYED_TURNOUT, ItemType.TURNOUT, rbx.getString("ActionDelayedTurnout")), // NOI18N
        // allowed settings for timed turnout are Thrown and Closed (in data)
        //   time in seconds before setting turnout should be in delay
        LOCK_TURNOUT(ACTION_LOCK_TURNOUT, ItemType.TURNOUT, rbx.getString("ActionTurnoutLock")), // NOI18N
        RESET_DELAYED_SENSOR(ACTION_RESET_DELAYED_SENSOR, ItemType.SENSOR, rbx.getString("ActionResetDelayedSensor")), // NOI18N
        // allowed settings for timed sensor are active and inactive (in data)
        //   time in seconds before setting sensor should be in delay
        CANCEL_SENSOR_TIMERS(ACTION_CANCEL_SENSOR_TIMERS, ItemType.SENSOR, rbx.getString("ActionCancelSensorTimers")), // NOI18N
        // cancels all timers delaying setting of specified sensor
        RESET_DELAYED_TURNOUT(ACTION_RESET_DELAYED_TURNOUT, ItemType.TURNOUT, rbx.getString("ActionResetDelayedTurnout")), // NOI18N
        // allowed settings for timed sensor are active and inactive (in data)
        //   time in seconds before setting sensor should be in delay
        CANCEL_TURNOUT_TIMERS(ACTION_CANCEL_TURNOUT_TIMERS, ItemType.TURNOUT, rbx.getString("ActionCancelTurnoutTimers")), // NOI18N
        // cancels all timers delaying setting of specified sensor
        SET_FAST_CLOCK_TIME(ACTION_SET_FAST_CLOCK_TIME, ItemType.CLOCK, rbx.getString("ActionSetFastClockTime")), // NOI18N
        // sets the fast clock time to the time specified
        START_FAST_CLOCK(ACTION_START_FAST_CLOCK, ItemType.CLOCK, rbx.getString("ActionStartFastClock")), // NOI18N
        // starts the fast clock
        STOP_FAST_CLOCK(ACTION_STOP_FAST_CLOCK, ItemType.CLOCK, rbx.getString("ActionStopFastClock")), // NOI18N
        // stops the fast clock
        COPY_MEMORY(ACTION_COPY_MEMORY, ItemType.MEMORY, rbx.getString("ActionCopyMemory")), // NOI18N
        // copies value from memory variable (in name) to memory variable (in string)
        SET_LIGHT_INTENSITY(ACTION_SET_LIGHT_INTENSITY, ItemType.LIGHT, rbx.getString("ActionSetLightIntensity")), // NOI18N
        SET_LIGHT_TRANSITION_TIME(ACTION_SET_LIGHT_TRANSITION_TIME, ItemType.LIGHT, rbx.getString("ActionSetLightTransitionTime")), // NOI18N
        // control the specified audio object
        CONTROL_AUDIO(ACTION_CONTROL_AUDIO, ItemType.AUDIO, rbx.getString("ActionControlAudio")), // NOI18N
        // execute a jython command
        JYTHON_COMMAND(ACTION_JYTHON_COMMAND, ItemType.SCRIPT, rbx.getString("ActionJythonCommand")), // NOI18N
        // Warrant actions
        ALLOCATE_WARRANT_ROUTE(ACTION_ALLOCATE_WARRANT_ROUTE, ItemType.WARRANT, rbx.getString("ActionAllocateWarrant")), // NOI18N
        DEALLOCATE_WARRANT_ROUTE(ACTION_DEALLOCATE_WARRANT_ROUTE, ItemType.WARRANT, rbx.getString("ActionDeallocateWarrant")), // NOI18N
        SET_ROUTE_TURNOUTS(ACTION_SET_ROUTE_TURNOUTS, ItemType.WARRANT, rbx.getString("ActionSetWarrantTurnouts")), // NOI18N
        AUTO_RUN_WARRANT(ACTION_AUTO_RUN_WARRANT, ItemType.WARRANT, rbx.getString("ActionAutoRunWarrant")), // NOI18N
        MANUAL_RUN_WARRANT(ACTION_MANUAL_RUN_WARRANT, ItemType.WARRANT, rbx.getString("ActionManualRunWarrant")), // NOI18N
        CONTROL_TRAIN(ACTION_CONTROL_TRAIN, ItemType.WARRANT, rbx.getString("ActionControlTrain")), // NOI18N
        SET_TRAIN_ID(ACTION_SET_TRAIN_ID, ItemType.WARRANT, rbx.getString("ActionSetTrainId")), // NOI18N
        SET_TRAIN_NAME(ACTION_SET_TRAIN_NAME, ItemType.WARRANT, rbx.getString("ActionSetTrainName")), // NOI18N
        SET_SIGNALMAST_ASPECT(ACTION_SET_SIGNALMAST_ASPECT, ItemType.SIGNALMAST, rbx.getString("ActionSetSignalMastAspect")), // NOI18N
        THROTTLE_FACTOR(ACTION_THROTTLE_FACTOR, ItemType.WARRANT, rbx.getString("ActionSetThrottleFactor")), // NOI18N
        SET_SIGNALMAST_HELD(ACTION_SET_SIGNALMAST_HELD, ItemType.SIGNALMAST, rbx.getString("ActionSetSignalMastHeld")), // NOI18N
        CLEAR_SIGNALMAST_HELD(ACTION_CLEAR_SIGNALMAST_HELD, ItemType.SIGNALMAST, rbx.getString("ActionClearSignalMastHeld")), // NOI18N
        SET_SIGNALMAST_DARK(ACTION_SET_SIGNALMAST_DARK, ItemType.SIGNALMAST, rbx.getString("ActionSetSignalMastDark")), // NOI18N
        SET_SIGNALMAST_LIT(ACTION_SET_SIGNALMAST_LIT, ItemType.SIGNALMAST, rbx.getString("ActionClearSignalMastDark")), // NOI18N
        SET_BLOCK_VALUE(ACTION_SET_BLOCK_VALUE, ItemType.OBLOCK, rbx.getString("ActionSetBlockValue")), // NOI18N
        SET_BLOCK_ERROR(ACTION_SET_BLOCK_ERROR, ItemType.OBLOCK, rbx.getString("ActionSetBlockError")), // NOI18N
        CLEAR_BLOCK_ERROR(ACTION_CLEAR_BLOCK_ERROR, ItemType.OBLOCK, rbx.getString("ActionClearBlockError")), // NOI18N
        DEALLOCATE_BLOCK(ACTION_DEALLOCATE_BLOCK, ItemType.OBLOCK, rbx.getString("ActionDeallocateBlock")), // NOI18N
        SET_BLOCK_OUT_OF_SERVICE(ACTION_SET_BLOCK_OUT_OF_SERVICE, ItemType.OBLOCK, rbx.getString("ActionSetBlockOutOfService")), // NOI18N
        SET_BLOCK_IN_SERVICE(ACTION_SET_BLOCK_IN_SERVICE, ItemType.OBLOCK, rbx.getString("ActionBlockInService")), // NOI18N
        // EntryExit Actions
        SET_NXPAIR_ENABLED(ACTION_SET_NXPAIR_ENABLED, ItemType.ENTRYEXIT, rbx.getString("ActionNXPairEnabled")), // NOI18N
        SET_NXPAIR_DISABLED(ACTION_SET_NXPAIR_DISABLED, ItemType.ENTRYEXIT, rbx.getString("ActionNXPairDisabled")), // NOI18N
        SET_NXPAIR_SEGMENT(ACTION_SET_NXPAIR_SEGMENT, ItemType.ENTRYEXIT, rbx.getString("ActionNXPairSegment")); // NOI18N


        private final int _item;
        private final ItemType _itemType;
        private final String _string;

        private static final List<Action> sensorItemsList;
        private static final List<Action> turnoutItemsList;
        private static final List<Action> lightItemsList;
        private static final List<Action> warrantItemsList;
        private static final List<Action> memoryItemsList;
        private static final List<Action> oblockItemsList;
        private static final List<Action> entryExitItemsList;
        private static final List<Action> signalHeadItemsList;
        private static final List<Action> signalMastItemsList;
        private static final List<Action> clockItemsList;
        private static final List<Action> logixItemsList;
        private static final List<Action> audioItemsList;
        private static final List<Action> scriptItemsList;
        private static final List<Action> otherItemsList;


        static
        {
            Action[] typeArray1 = {SET_SENSOR, DELAYED_SENSOR,
                RESET_DELAYED_SENSOR, CANCEL_SENSOR_TIMERS};
            sensorItemsList = Collections.unmodifiableList(Arrays.asList(typeArray1));

            Action[] typeArray2 = {SET_TURNOUT, DELAYED_TURNOUT, LOCK_TURNOUT,
                CANCEL_TURNOUT_TIMERS, RESET_DELAYED_TURNOUT};
            turnoutItemsList = Collections.unmodifiableList(Arrays.asList(typeArray2));

            Action[] typeArray3 = {SET_LIGHT, SET_LIGHT_INTENSITY,
                SET_LIGHT_TRANSITION_TIME};
            lightItemsList = Collections.unmodifiableList(Arrays.asList(typeArray3));

            Action[] typeArray4 = {ALLOCATE_WARRANT_ROUTE, DEALLOCATE_WARRANT_ROUTE,
                SET_ROUTE_TURNOUTS, AUTO_RUN_WARRANT, MANUAL_RUN_WARRANT, CONTROL_TRAIN,
                SET_TRAIN_ID, SET_TRAIN_NAME, THROTTLE_FACTOR};
            warrantItemsList = Collections.unmodifiableList(Arrays.asList(typeArray4));

            Action[] typeArray5 = {SET_MEMORY, COPY_MEMORY};
            memoryItemsList = Collections.unmodifiableList(Arrays.asList(typeArray5));

            Action[] typeArray6 = {SET_NXPAIR_ENABLED, SET_NXPAIR_DISABLED,
                SET_NXPAIR_SEGMENT};
            entryExitItemsList = Collections.unmodifiableList(Arrays.asList(typeArray6));

            Action[] typeArray7 = {SET_SIGNAL_APPEARANCE, SET_SIGNAL_HELD,
                CLEAR_SIGNAL_HELD, SET_SIGNAL_DARK, SET_SIGNAL_LIT};
            signalHeadItemsList = Collections.unmodifiableList(Arrays.asList(typeArray7));

            Action[] typeArray8 = {SET_SIGNALMAST_ASPECT, SET_SIGNALMAST_HELD,
                CLEAR_SIGNALMAST_HELD, SET_SIGNALMAST_DARK, SET_SIGNALMAST_LIT};
            signalMastItemsList = Collections.unmodifiableList(Arrays.asList(typeArray8));

            Action[] typeArray9 = {SET_FAST_CLOCK_TIME, START_FAST_CLOCK,
                STOP_FAST_CLOCK};
            clockItemsList = Collections.unmodifiableList(Arrays.asList(typeArray9));

            Action[] typeArray10 = {ENABLE_LOGIX, DISABLE_LOGIX};
            logixItemsList = Collections.unmodifiableList(Arrays.asList(typeArray10));

            Action[] typeArray11 = {DEALLOCATE_BLOCK, SET_BLOCK_VALUE,
                SET_BLOCK_ERROR, CLEAR_BLOCK_ERROR, SET_BLOCK_OUT_OF_SERVICE,
                SET_BLOCK_IN_SERVICE};
            oblockItemsList = Collections.unmodifiableList(Arrays.asList(typeArray11));

            Action[] typeArray12 = {PLAY_SOUND, CONTROL_AUDIO};
            audioItemsList = Collections.unmodifiableList(Arrays.asList(typeArray12));

            Action[] typeArray13 = {RUN_SCRIPT, JYTHON_COMMAND};
            scriptItemsList = Collections.unmodifiableList(Arrays.asList(typeArray13));

            Action[] typeArray14 = {TRIGGER_ROUTE};
            otherItemsList = Collections.unmodifiableList(Arrays.asList(typeArray14));
        }

        private Action(int state, ItemType itemType, String string) {
            _item = state;
            _itemType = itemType;
            _string = string;
        }

        public ItemType getItemType() {
            return _itemType;
        }

        public int getIntValue() {
            return _item;
        }

        public static List<Action> getSensorItems() {
            return sensorItemsList;
        }

        public static List<Action> getTurnoutItems() {
            return turnoutItemsList;
        }

        public static List<Action> getLightItems() {
            return lightItemsList;
        }

        public static List<Action> getWarrantItems() {
            return warrantItemsList;
        }

        public static List<Action> getMemoryItems() {
            return memoryItemsList;
        }

        public static List<Action> getOBlockItems() {
            return oblockItemsList;
        }

        public static List<Action> getEntryExitItems() {
            return entryExitItemsList;
        }

        public static List<Action> getSignalHeadItems() {
            return signalHeadItemsList;
        }

        public static List<Action> getSignalMastItems() {
            return signalMastItemsList;
        }

        public static List<Action> getClockItems() {
            return clockItemsList;
        }

        public static List<Action> getLogixItems() {
            return logixItemsList;
        }

        public static List<Action> getAudioItems() {
            return audioItemsList;
        }

        public static List<Action> getScriptItems() {
            return scriptItemsList;
        }

        public static List<Action> getOtherItems() {
            return otherItemsList;
        }

        public static Action getOperatorFromIntValue(int actionInt) {
            for (Action action : Action.values()) {
                if (action.getIntValue() == actionInt) {
                    return action;
                }
            }

            throw new IllegalArgumentException("Action is unknown");
        }

        // Some items uses Bundle.getString() and some items uses rbx.getString()
        // and therefore the items must call getString() in the call to the constructor.
        @Override
        public String toString() {
            return _string;
        }

    }

    // state variable types
    static final int TYPE_ERROR = -1;
    static final int TYPE_NONE = 0;
    static final int TYPE_SENSOR_ACTIVE = 1;
    static final int TYPE_SENSOR_INACTIVE = 2;
    static final int TYPE_TURNOUT_THROWN = 3;
    static final int TYPE_TURNOUT_CLOSED = 4;
    static final int TYPE_CONDITIONAL_TRUE = 5;
    static final int TYPE_CONDITIONAL_FALSE = 6;
    static final int TYPE_LIGHT_ON = 7;
    static final int TYPE_LIGHT_OFF = 8;
    static final int TYPE_MEMORY_EQUALS = 9;
    static final int TYPE_FAST_CLOCK_RANGE = 10;
    // Note - within the TYPE_SIGNAL_HEAD definitions, all must be together,
    //  RED must be first, and HELD must be last
    static final int TYPE_SIGNAL_HEAD_RED = 11;
    static final int TYPE_SIGNAL_HEAD_YELLOW = 12;
    static final int TYPE_SIGNAL_HEAD_GREEN = 13;
    static final int TYPE_SIGNAL_HEAD_DARK = 14;
    static final int TYPE_SIGNAL_HEAD_FLASHRED = 15;
    static final int TYPE_SIGNAL_HEAD_FLASHYELLOW = 16;
    static final int TYPE_SIGNAL_HEAD_FLASHGREEN = 17;
    static final int TYPE_SIGNAL_HEAD_LIT = 18;
    static final int TYPE_SIGNAL_HEAD_HELD = 19;
    static final int TYPE_MEMORY_COMPARE = 20;
    static final int TYPE_SIGNAL_HEAD_LUNAR = 21;
    static final int TYPE_SIGNAL_HEAD_FLASHLUNAR = 22;
    static final int TYPE_MEMORY_EQUALS_INSENSITIVE = 23;
    static final int TYPE_MEMORY_COMPARE_INSENSITIVE = 24;
    // Warrant variables
    static final int TYPE_ROUTE_FREE = 25;
    static final int TYPE_ROUTE_OCCUPIED = 26;
    static final int TYPE_ROUTE_ALLOCATED = 27;
    static final int TYPE_ROUTE_SET = 28;
    static final int TYPE_TRAIN_RUNNING = 29;
    static final int TYPE_SIGNAL_MAST_ASPECT_EQUALS = 30;
    static final int TYPE_SIGNAL_MAST_LIT = 31;
    static final int TYPE_SIGNAL_MAST_HELD = 32;
    static final int TYPE_SIGNAL_HEAD_APPEARANCE_EQUALS = 33;

    static final int TYPE_BLOCK_STATUS_EQUALS = 34;

    //Entry Exit Rules
    static final int TYPE_ENTRYEXIT_ACTIVE = 35;
    static final int TYPE_ENTRYEXIT_INACTIVE = 36;

    static final int TYPE_OBLOCK_UNOCCUPIED = 37;
    static final int TYPE_OBLOCK_OCCUPIED = 38;
    static final int TYPE_OBLOCK_ALLOCATED = 39;
    static final int TYPE_OBLOCK_RUNNING = 40;
    static final int TYPE_OBLOCK_OUT_OF_SERVICE = 41;
    static final int TYPE_OBLOCK_DARK = 42;
    static final int TYPE_OBLOCK_POWER_ERROR = 43;

    static final int TYPE_XXXXXXX = 9999;

    // action definitions
    static final int ACTION_OPTION_ON_CHANGE_TO_TRUE = 1;
    static final int ACTION_OPTION_ON_CHANGE_TO_FALSE = 2;
    static final int ACTION_OPTION_ON_CHANGE = 3;
    static final int NUM_ACTION_OPTIONS = 3;

    // action types
    static final int ACTION_NONE = 1;
    static final int ACTION_SET_TURNOUT = 2;
    // allowed settings for turnout are Thrown and Closed (in data)
    static final int ACTION_SET_SIGNAL_APPEARANCE = 3;
    // allowed settings for signal head are the seven Appearances (in data)
    static final int ACTION_SET_SIGNAL_HELD = 4;
    static final int ACTION_CLEAR_SIGNAL_HELD = 5;
    static final int ACTION_SET_SIGNAL_DARK = 6;
    static final int ACTION_SET_SIGNAL_LIT = 7;
    static final int ACTION_TRIGGER_ROUTE = 8;
    static final int ACTION_SET_SENSOR = 9;
    // allowed settings for sensor are active and inactive (in data)
    static final int ACTION_DELAYED_SENSOR = 10;
    // allowed settings for timed sensor are active and inactive (in data)
    //   time in seconds before setting sensor should be in delay
    static final int ACTION_SET_LIGHT = 11;
    // allowed settings for light are ON and OFF (in data)
    static final int ACTION_SET_MEMORY = 12;
    // text to set into the memory variable should be in string
    static final int ACTION_ENABLE_LOGIX = 13;
    static final int ACTION_DISABLE_LOGIX = 14;
    static final int ACTION_PLAY_SOUND = 15;
    // reference to sound should be in string
    static final int ACTION_RUN_SCRIPT = 16;
    // reference to script should be in string
    static final int ACTION_DELAYED_TURNOUT = 17;
    // allowed settings for timed turnout are Thrown and Closed (in data)
    //   time in seconds before setting turnout should be in delay
    static final int ACTION_LOCK_TURNOUT = 18;
    static final int ACTION_RESET_DELAYED_SENSOR = 19;
    // allowed settings for timed sensor are active and inactive (in data)
    //   time in seconds before setting sensor should be in delay
    static final int ACTION_CANCEL_SENSOR_TIMERS = 20;
    // cancels all timers delaying setting of specified sensor
    static final int ACTION_RESET_DELAYED_TURNOUT = 21;
    // allowed settings for timed sensor are active and inactive (in data)
    //   time in seconds before setting sensor should be in delay
    static final int ACTION_CANCEL_TURNOUT_TIMERS = 22;
    // cancels all timers delaying setting of specified sensor
    static final int ACTION_SET_FAST_CLOCK_TIME = 23;
    // sets the fast clock time to the time specified
    static final int ACTION_START_FAST_CLOCK = 24;
    // starts the fast clock
    static final int ACTION_STOP_FAST_CLOCK = 25;
    // stops the fast clock
    static final int ACTION_COPY_MEMORY = 26;
    // copies value from memory variable (in name) to memory variable (in string)
    static final int ACTION_SET_LIGHT_INTENSITY = 27;
    static final int ACTION_SET_LIGHT_TRANSITION_TIME = 28;
    // control the specified audio object
    static final int ACTION_CONTROL_AUDIO = 29;
    // execute a jython command
    static final int ACTION_JYTHON_COMMAND = 30;
    // Warrant actions
    static final int ACTION_ALLOCATE_WARRANT_ROUTE = 31;
    static final int ACTION_DEALLOCATE_WARRANT_ROUTE = 32;
    static final int ACTION_SET_ROUTE_TURNOUTS = 33;
    static final int ACTION_AUTO_RUN_WARRANT = 34;
    static final int ACTION_CONTROL_TRAIN = 35;
    static final int ACTION_SET_TRAIN_ID = 36;
    static final int ACTION_SET_SIGNALMAST_ASPECT = 37;
    static final int ACTION_THROTTLE_FACTOR = 38;
    static final int ACTION_SET_SIGNALMAST_HELD = 39;
    static final int ACTION_CLEAR_SIGNALMAST_HELD = 40;
    static final int ACTION_SET_SIGNALMAST_DARK = 41;
    static final int ACTION_SET_SIGNALMAST_LIT = 42;
    static final int ACTION_SET_BLOCK_ERROR = 43;
    static final int ACTION_CLEAR_BLOCK_ERROR = 44;
    static final int ACTION_DEALLOCATE_BLOCK = 45;
    static final int ACTION_SET_BLOCK_OUT_OF_SERVICE = 46;
    static final int ACTION_SET_BLOCK_IN_SERVICE = 47;
    static final int ACTION_MANUAL_RUN_WARRANT = 48;
    static final int ACTION_SET_TRAIN_NAME = 49;
    static final int ACTION_SET_BLOCK_VALUE = 50;
    // EntryExit Actions
    static final int ACTION_SET_NXPAIR_ENABLED = 51;
    static final int ACTION_SET_NXPAIR_DISABLED = 52;
    static final int ACTION_SET_NXPAIR_SEGMENT = 53;

    /**
     * ***********************************************************************************
     */
    /* New Variable and Action type scheme for Logix UI
     * State Variables and actions are grouped according to type.  Variable and action
     * types share the following group categories:
     */
    // state variable and action items used by logix.
    static final int ITEM_TYPE_SENSOR = 1;
    static final int ITEM_TYPE_TURNOUT = 2;
    static final int ITEM_TYPE_LIGHT = 3;
    static final int ITEM_TYPE_SIGNALHEAD = 4;
    static final int ITEM_TYPE_SIGNALMAST = 5;
    static final int ITEM_TYPE_MEMORY = 6;
    static final int ITEM_TYPE_CONDITIONAL = 7;  // used only by ConditionalVariable
    static final int ITEM_TYPE_LOGIX = 7;        // used only by ConditionalAction
    static final int ITEM_TYPE_WARRANT = 8;
    static final int ITEM_TYPE_CLOCK = 9;
    static final int ITEM_TYPE_OBLOCK = 10;
    static final int ITEM_TYPE_ENTRYEXIT = 11;

    static final int ITEM_TYPE_AUDIO = 12;
    static final int ITEM_TYPE_SCRIPT = 13;
    static final int ITEM_TYPE_OTHER = 14;

    /**
     * set the logic type (all AND's all OR's or mixed AND's and OR's set the
     * antecedent expression - should be a well formed boolean statement with
     * parenthesis indicating the order of evaluation
     *
     * @param type       the type
     * @param antecedent the expression
     */
    public void setLogicType(Conditional.AntecedentOperator type, String antecedent);

    /**
     * Get antecedent (boolean expression) of Conditional
     *
     * @return the expression
     */
    public String getAntecedentExpression();

    /**
     * Get type of operators in the antecedent statement
     *
     * @return the type
     */
    public Conditional.AntecedentOperator getLogicType();

    /**
     * @return true if action list is executed only when state changes, false if
     *         action list is executed on every calculation of state
     */
    public boolean getTriggerOnChange();

    /**
     * Set policy for execution of action list
     *
     * @param trigger true execute only on change of state
     */
    public void setTriggerOnChange(boolean trigger);

    /**
     * Set list of actions
     *
     * @param arrayList the actions
     */
    public void setAction(List<ConditionalAction> arrayList);

    /**
     * Make deep clone of actions
     *
     * @return a list of copies of actionss
     */
    public List<ConditionalAction> getCopyOfActions();

    /**
     * Set State Variables for this Conditional. Each state variable will
     * evaluate either True or False when this Conditional is calculated.
     * <p>
     * This method assumes that all information has been validated.
     *
     * @param arrayList the list of variables
     */
    public void setStateVariables(List<ConditionalVariable> arrayList);

    /**
     * Make deep clone of variables
     *
     * @return a list containing copies of variables
     */
    public List<ConditionalVariable> getCopyOfStateVariables();

    /**
     * Calculate this Conditional, triggering either or both actions if the user
     * specified conditions are met, and the Logix is enabled. Sets the state of
     * the conditional. Returns the calculated state of this Conditional.
     *
     * @param enabled true if Logix should be enabled; false otherwise
     * @param evt     event to trigger if true
     * @return the new state
     */
    public int calculate(boolean enabled, PropertyChangeEvent evt);

    /**
     * Check that an antecedent is well formed. If not, returns an error
     * message. Otherwise returns null.
     *
     * @param ant          the expression
     * @param variableList list of variables
     * @return true if well formed; false otherwise
     */
    public String validateAntecedent(String ant, List<ConditionalVariable> variableList);

    /**
     * Stop a sensor timer if one is actively delaying setting of the specified
     * sensor
     *
     * @param sname the name of the timer
     */
    public void cancelSensorTimer(String sname);

    /**
     * Stop a turnout timer if one is actively delaying setting of the specified
     * turnout
     *
     * @param sname the name of the timer
     */
    public void cancelTurnoutTimer(String sname);

    /**
     * State of the Conditional is returned.
     *
     * @return state value
     */
    @Override
    public int getState();

    /**
     * Request a call-back when the bound KnownState property changes.
     *
     * @param l the listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Remove a request for a call-back when a bound property changes.
     *
     * @param l the listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Remove references to and from this object, so that it can eventually be
     * garbage-collected.
     */
    @Override
    public void dispose();  // remove _all_ connections!

}
