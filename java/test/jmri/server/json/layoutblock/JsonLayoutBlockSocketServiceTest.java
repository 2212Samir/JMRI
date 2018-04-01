package jmri.server.json.layoutblock;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.DataOutputStream;
import java.util.Locale;
import jmri.InstanceManager;
import jmri.jmrit.display.layoutEditor.LayoutBlock;
import jmri.jmrit.display.layoutEditor.LayoutBlockManager;
import jmri.server.json.JSON;
import jmri.server.json.JsonMockConnection;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Randall Wood Copyright 2018
 */
public class JsonLayoutBlockSocketServiceTest {

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.initLayoutBlockManager();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    /**
     * Test of onMessage method, of class JsonLayoutBlockSocketService.
     *
     * @throws java.lang.Exception for unexpected errors
     */
    @Test
    public void testOnMessage() throws Exception {
        LayoutBlock lb = InstanceManager.getDefault(LayoutBlockManager.class).createNewLayoutBlock(null, "LayoutBlock1");
        Assert.assertNotNull("LayoutBlock is created", lb);
        Assert.assertEquals("LayoutBlock has 1 listener", 1, lb.getPropertyChangeListeners().length);
        // test GETs
        JsonLayoutBlockSocketService instance = new JsonLayoutBlockSocketService(new JsonMockConnection((DataOutputStream) null));
        instance.onMessage(JsonLayoutBlock.LAYOUTBLOCK,
                instance.getConnection().getObjectMapper().readTree("{\"name\":\"" + lb.getSystemName() + "\"}"),
                JSON.GET, Locale.ENGLISH);
        // onMessage causes a listener to be added to requested LayoutBlocks if not already listening
        Assert.assertEquals("LayoutBlock has 2 listeners", 2, lb.getPropertyChangeListeners().length);
        // test POSTs
        instance.onMessage(JsonLayoutBlock.LAYOUTBLOCK,
                instance.getConnection().getObjectMapper().readTree("{\"name\":\"" + lb.getSystemName() + "\", \"userName\":\"LayoutBlock2\"}"),
                JSON.GET, Locale.ENGLISH);
        // onMessage causes a listener to be added to requested LayoutBlocks if not already listening
        Assert.assertEquals("LayoutBlock has 2 listeners", 2, lb.getPropertyChangeListeners().length);
        Assert.assertEquals("LayoutBlock user name is changed", "LayoutBlock2", lb.getUserName());
        instance.onMessage(JsonLayoutBlock.LAYOUTBLOCK,
                instance.getConnection().getObjectMapper().readTree("{\"name\":\"" + lb.getSystemName() + "\", \"comment\":\"this is a comment\"}"),
                JSON.GET, Locale.ENGLISH);
        Assert.assertEquals("LayoutBlock has comment", "this is a comment", lb.getComment());
        instance.onMessage(JsonLayoutBlock.LAYOUTBLOCK,
                instance.getConnection().getObjectMapper().readTree("{\"name\":\"" + lb.getSystemName() + "\", \"comment\":null}"),
                JSON.GET, Locale.ENGLISH);
        Assert.assertNull("LayoutBlock has no comment", lb.getComment());
    }

    /**
     * Test of onList method, of class JsonLayoutBlockSocketService.
     *
     * @throws java.lang.Exception on unexpected errors
     */
    @Test
    public void testOnList() throws Exception {
        LayoutBlockManager manager = InstanceManager.getDefault(LayoutBlockManager.class);
        JsonMockConnection connection = new JsonMockConnection((DataOutputStream) null);
        LayoutBlock lb1 = manager.createNewLayoutBlock(null, "LayoutBlock1");
        LayoutBlock lb2 = manager.createNewLayoutBlock(null, "LayoutBlock2");
        Assert.assertNotNull("LayoutBlock1 is created", lb1);
        Assert.assertNotNull("LayoutBlock2 is created", lb2);
        Assert.assertEquals("LayoutBlock1 has 1 listener", 1, lb1.getPropertyChangeListeners().length);
        JsonLayoutBlockSocketService instance = new JsonLayoutBlockSocketService(connection);
        instance.onList(JsonLayoutBlock.LAYOUTBLOCK, null, Locale.ENGLISH);
        // onList does not cause a listener to be added to requested LayoutBlocks if not already listening
        Assert.assertEquals("LayoutBlock1 has 1 listeners", 1, lb1.getPropertyChangeListeners().length);
        JsonNode message = connection.getMessage();
        Assert.assertTrue("Message is an array", message.isArray());
        Assert.assertEquals("All LayoutBlocks are listed", manager.getNamedBeanList().size(), message.size());
    }

    /**
     * Test of onClose method, of class JsonLayoutBlockSocketService.
     *
     * @throws java.lang.Exception for unexpected errors
     */
    @Test
    public void testOnClose() throws Exception {
        LayoutBlock lb = InstanceManager.getDefault(LayoutBlockManager.class).createNewLayoutBlock(null, "LayoutBlock1");
        Assert.assertNotNull("LayoutBlock is created", lb);
        Assert.assertEquals("LayoutBlock has 1 listener", 1, lb.getPropertyChangeListeners().length);
        JsonLayoutBlockSocketService instance = new JsonLayoutBlockSocketService(new JsonMockConnection((DataOutputStream) null));
        instance.onMessage(JsonLayoutBlock.LAYOUTBLOCK,
                instance.getConnection().getObjectMapper().readTree("{\"name\":\"" + lb.getSystemName() + "\"}"),
                JSON.GET, Locale.ENGLISH);
        // onMessage causes a listener to be added to requested LayoutBlocks
        Assert.assertEquals("LayoutBlock has 2 listeners", 2, lb.getPropertyChangeListeners().length);
        instance.onClose();
        // onClose removes listeners
        Assert.assertEquals("LayoutBlock has 1 listener", 1, lb.getPropertyChangeListeners().length);
    }

}
