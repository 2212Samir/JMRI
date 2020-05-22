package jmri.jmrix.srcp.swing.packetgen;

import java.awt.event.ActionEvent;
import jmri.InstanceManager;
import jmri.jmrix.srcp.SRCPSystemConnectionMemo;
import jmri.jmrix.swing.AbstractSystemConnectionAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing action to create and register a PacketGenFrame object
 *
 * @author Bob Jacobsen Copyright (C) 2008
 */
public class PacketGenAction extends AbstractSystemConnectionAction<SRCPSystemConnectionMemo> {

    public PacketGenAction(String s, SRCPSystemConnectionMemo memo) {
        super(s, memo);
    }

    public PacketGenAction(SRCPSystemConnectionMemo memo) {
        this(Bundle.getMessage("MenuItemSendSRCPCommand"), memo);
    }

    public PacketGenAction() {
        this(InstanceManager.getDefault(SRCPSystemConnectionMemo.class));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PacketGenFrame f = new PacketGenFrame(getSystemConnectionMemo());
        try {
            f.initComponents();
        } catch (Exception ex) {
            log.error("Exception: {}", ex.toString());
        }
        f.setVisible(true);
    }
    private final static Logger log = LoggerFactory.getLogger(PacketGenAction.class);
}
