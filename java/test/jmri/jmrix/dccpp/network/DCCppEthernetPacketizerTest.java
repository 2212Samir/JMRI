package jmri.jmrix.dccpp.network;

import org.junit.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Title: DCCppEthernetPacketizerTest </p>
 * <p>
 *
 * @author Paul Bender Copyright (C) 2009
 */
public class DCCppEthernetPacketizerTest extends jmri.jmrix.dccpp.DCCppPacketizerTest {

    // The minimal setup for log4J
    @Before
    @Override
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
        tc = new DCCppEthernetPacketizer(new jmri.jmrix.dccpp.DCCppCommandStation());
    }

    @After
    @Override
    public void tearDown() {
        apps.tests.Log4JFixture.tearDown();
    }

}
