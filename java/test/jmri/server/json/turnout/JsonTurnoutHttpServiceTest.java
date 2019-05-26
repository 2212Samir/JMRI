package jmri.server.json.turnout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;

import javax.servlet.http.HttpServletResponse;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.Turnout;
import jmri.TurnoutManager;
import jmri.server.json.JSON;
import jmri.server.json.JsonException;
import jmri.server.json.JsonNamedBeanHttpServiceTestBase;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender
 * @author Randall Wood
 */
public class JsonTurnoutHttpServiceTest extends JsonNamedBeanHttpServiceTestBase<Turnout, JsonTurnoutHttpService> {

    @Test
    public void testDoGet() throws JmriException, JsonException {
        TurnoutManager manager = InstanceManager.getDefault(TurnoutManager.class);
        Turnout turnout1 = manager.provideTurnout("IT1");
        JsonNode result;
        turnout1.setState(Turnout.UNKNOWN);
        result = service.doGet(JsonTurnoutServiceFactory.TURNOUT, "IT1",
                NullNode.getInstance(), locale, 42);
        validate(result);
        assertEquals(JsonTurnoutServiceFactory.TURNOUT, result.path(JSON.TYPE).asText());
        assertEquals("IT1", result.path(JSON.DATA).path(JSON.NAME).asText());
        assertEquals(JSON.UNKNOWN, result.path(JSON.DATA).path(JSON.STATE).asInt());
        turnout1.setState(Turnout.CLOSED);
        result = service.doGet(JsonTurnoutServiceFactory.TURNOUT, "IT1",
                NullNode.getInstance(), locale, 42);
        validate(result);
        assertEquals(JSON.CLOSED, result.path(JSON.DATA).path(JSON.STATE).asInt());
        turnout1.setState(Turnout.THROWN);
        result = service.doGet(JsonTurnoutServiceFactory.TURNOUT, "IT1",
                NullNode.getInstance(), locale, 42);
        validate(result);
        assertEquals(JSON.THROWN, result.path(JSON.DATA).path(JSON.STATE).asInt());
        turnout1.setState(Turnout.INCONSISTENT);
        result = service.doGet(JsonTurnoutServiceFactory.TURNOUT, "IT1",
                NullNode.getInstance(), locale, 42);
        validate(result);
        assertEquals(JSON.INCONSISTENT, result.path(JSON.DATA).path(JSON.STATE).asInt());
    }

    @Test
    public void testDoPost() throws JmriException, JsonException {
        TurnoutManager manager = InstanceManager.getDefault(TurnoutManager.class);
        Turnout turnout1 = manager.provideTurnout("IT1");
        JsonNode result;
        JsonNode message;
        turnout1.setState(Turnout.UNKNOWN);
        // set closed
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.STATE, JSON.CLOSED);
        result = service.doPost(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
        assertEquals(Turnout.CLOSED, turnout1.getState());
        validate(result);
        assertEquals(JSON.CLOSED, result.path(JSON.DATA).path(JSON.STATE).asInt());
        // set thrown
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.STATE, JSON.THROWN);
        result = service.doPost(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
        assertEquals(Turnout.THROWN, turnout1.getState());
        validate(result);
        assertEquals(JSON.THROWN, result.path(JSON.DATA).path(JSON.STATE).asInt());
        // set unknown - remains thrown
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.STATE, JSON.UNKNOWN);
        result = service.doPost(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
        assertEquals(Turnout.THROWN, turnout1.getState());
        assertEquals(JSON.THROWN, result.path(JSON.DATA).path(JSON.STATE).asInt());
        // set inverted - becomes closed
        assertFalse(turnout1.getInverted());
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.INVERTED, true);
        result = service.doPost(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
        assertTrue("Turnout is inverted", turnout1.getInverted());
        assertEquals(JSON.CLOSED, result.path(JSON.DATA).path(JSON.STATE).asInt());
        assertEquals(true, result.path(JSON.DATA).path(JSON.INVERTED).asBoolean());
        // reset inverted - becomes thrown
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.INVERTED, false);
        result = service.doPost(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
        assertFalse("Turnout is not inverted", turnout1.getInverted());
        assertEquals(JSON.THROWN, result.path(JSON.DATA).path(JSON.STATE).asInt());
        // set invalid state
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.STATE, 42); // Invalid value
        try {
            service.doPost(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
            fail("Expected exception not thrown");
        } catch (JsonException ex) {
            assertEquals(HttpServletResponse.SC_BAD_REQUEST, ex.getCode());
        }
        assertEquals(Turnout.THROWN, turnout1.getState());
    }

    @Test
    public void testDoPut() throws JsonException {
        TurnoutManager manager = InstanceManager.getDefault(TurnoutManager.class);
        JsonNode message;
        // add a turnout
        assertNull(manager.getTurnout("IT1"));
        message = mapper.createObjectNode().put(JSON.NAME, "IT1").put(JSON.STATE, Turnout.CLOSED);
        service.doPut(JsonTurnoutServiceFactory.TURNOUT, "IT1", message, locale, 42);
        assertNotNull(manager.getTurnout("IT1"));
    }

    @Test
    public void testDoGetList() throws JsonException {
        TurnoutManager manager = InstanceManager.getDefault(TurnoutManager.class);
        JsonNode result;
        result = service.doGetList(JsonTurnoutServiceFactory.TURNOUT, mapper.createObjectNode(), locale, 0);
        validate(result);
        assertEquals(0, result.size());
        manager.provideTurnout("IT1");
        result = service.doGetList(JsonTurnoutServiceFactory.TURNOUT, mapper.createObjectNode(), locale, 0);
        validate(result);
        assertEquals(1, result.size());
        manager.provideTurnout("IT2");
        result = service.doGetList(JsonTurnoutServiceFactory.TURNOUT, mapper.createObjectNode(), locale, 0);
        validate(result);
        assertEquals(2, result.size());
    }

    @Test
    @Override
    public void testDoDelete() throws JsonException {
        TurnoutManager manager = InstanceManager.getDefault(TurnoutManager.class);
        ObjectNode message = mapper.createObjectNode();
        assumeNotNull(service); // protect against JUnit tests in Eclipse that test this class directly
        // delete non-existant bean
        try {
            service.doDelete(service.getType(), "non-existant", message, locale, 42);
            fail("Expected exception not thrown.");
        } catch (JsonException ex) {
            assertEquals("Code is HTTP NOT FOUND", 404, ex.getCode());
            assertEquals("Message", "Object type turnout named \"non-existant\" not found.", ex.getMessage());
            assertEquals("ID is 42", 42, ex.getId());
        }
        manager.newTurnout("IT1", null);
        // delete existing bean (no named listener)
        assertNotNull(manager.getBeanBySystemName("IT1"));
        service.doDelete(service.getType(), "IT1", message, locale, 42);
        assertNull(manager.getBeanBySystemName("IT1"));
        Turnout turnout = manager.newTurnout("IT1", null);
        assertNotNull(turnout);
        turnout.addPropertyChangeListener(evt -> {
            // do nothing
        }, "IT1", "Test Listener");
        // delete existing bean (with named listener)
        try {
            service.doDelete(service.getType(), "IT1", message, locale, 42);
            fail("Expected exception not thrown.");
        } catch (JsonException ex) {
            assertEquals(409, ex.getCode());
            assertEquals(1, ex.getAdditionalData().path(JSON.CONFLICT).size());
            assertEquals("Test Listener", ex.getAdditionalData().path(JSON.CONFLICT).path(0).asText());
            message = message.put(JSON.FORCE_DELETE, ex.getAdditionalData().path(JSON.FORCE_DELETE).asText());
        }
        assertNotNull(manager.getBeanBySystemName("IT1"));
        // will throw if prior catch failed
        service.doDelete(service.getType(), "IT1", message, locale, 0);
        assertNull(manager.getBeanBySystemName("IT1"));
        try {
            // deleting again should throw an exception
            service.doDelete(service.getType(), "IT1", NullNode.getInstance(), locale, 0);
            fail("Expected exception not thrown.");
        } catch (JsonException ex) {
            assertEquals(404, ex.getCode());
        }
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        service = new JsonTurnoutHttpService(mapper);
        JUnitUtil.initInternalTurnoutManager();
        JUnitUtil.initInternalLightManager();
        JUnitUtil.initInternalSensorManager();
        JUnitUtil.initDebugThrottleManager();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        JUnitUtil.tearDown();
    }

}
