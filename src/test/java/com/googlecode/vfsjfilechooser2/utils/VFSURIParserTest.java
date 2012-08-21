/*
 * A helper class to deal with commons-vfs file abstractions
 *
 * Copyright (C) 2008-2009 Yves Zoundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package com.googlecode.vfsjfilechooser2.utils;

import junit.framework.TestCase;


import java.util.logging.Logger;

import com.googlecode.vfsjfilechooser2.accessories.connection.Protocol;
import com.googlecode.vfsjfilechooser2.utils.VFSURIParser;


/**
 * Unit test for the VFS URI parser
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public class VFSURIParserTest extends TestCase
{
    private static final Logger logger = Logger.getLogger(VFSURIParserTest.class.getName());

    public VFSURIParserTest(String testName)
    {
        super(testName);
    }

    /**
     * Parsing local files
     */
    public void testParseLocalFiles()
    {
        logger.info("Testing VFSURIParser for local files\n");

        // testing local files

        VFSURIParser parser;
        
            parser = new VFSURIParser("file:///C:/home/birdman");
            assertEquals(Protocol.FILE, parser.getProtocol());
            assertEquals("C:/home/birdman", parser.getPath());
            assertNull(parser.getHostname());
            assertNull(parser.getPortnumber());
            assertNull(parser.getUsername());
            assertNull(parser.getPassword());
       
            parser = new VFSURIParser("file:///home/birdman");
            assertEquals(Protocol.FILE, parser.getProtocol());
            assertEquals("/home/birdman", parser.getPath());
            assertNull(parser.getHostname());
            assertNull(parser.getPortnumber());
            assertNull(parser.getUsername());
            assertNull(parser.getPassword());
        
    }

    /**
     * Parsing with a default port number
     */
    public void testParseDefaultPort()
    {
        logger.info("Testing VFSURIParser for remote files(default port)\n");

        VFSURIParser parser;

        try
        {
            //////////////////////////////////////////////////////////
            //  TESTING WITH DEFAULT PORT
            //////////////////////////////////////////////////////////
            // test no path, no username, no password, no port
            parser = new VFSURIParser("sftp://shell.sf.net");
            assertEquals(Protocol.SFTP, parser.getProtocol());
            assertEquals("/", parser.getPath());
            assertEquals("shell.sf.net", parser.getHostname());
            assertEquals(Protocol.SFTP.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertNull(parser.getUsername());
            assertNull(parser.getPassword());

            // test with root path, no username, no password, no port
            parser = new VFSURIParser("ftp://ftp.ca.freebsd.org/");
            assertEquals(Protocol.FTP, parser.getProtocol());
            assertEquals("/", parser.getPath());
            assertEquals("ftp.ca.freebsd.org", parser.getHostname());
            assertEquals(Protocol.FTP.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertNull(parser.getUsername());
            assertNull(parser.getPassword());

            // testing uri with hostname and path only
            parser = new VFSURIParser("webdav://myserver.net/home/yves");
            assertEquals(Protocol.WEBDAV, parser.getProtocol());
            assertEquals("/home/yves", parser.getPath());
            assertEquals("myserver.net", parser.getHostname());
            assertEquals(Protocol.WEBDAV.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertNull(parser.getUsername());
            assertNull(parser.getPassword());

            // test with username, hostname, no path
            parser = new VFSURIParser("sftp://yves@shell.sf.net");
            assertEquals(Protocol.SFTP, parser.getProtocol());
            assertEquals("/", parser.getPath());
            assertEquals("shell.sf.net", parser.getHostname());
            assertEquals(Protocol.SFTP.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertEquals("yves", parser.getUsername());
            assertNull(parser.getPassword());

            // test with username, password, hostname, root path  
            parser = new VFSURIParser("sftp://yves:yves@shell.sf.net/");
            assertEquals(Protocol.SFTP, parser.getProtocol());
            assertEquals("/", parser.getPath());
            assertEquals("shell.sf.net", parser.getHostname());
            assertEquals(Protocol.SFTP.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertEquals("yves", parser.getUsername());
            assertEquals("yves", parser.getPassword());

            // test with common values - password missing
            parser = new VFSURIParser("sftp://yves@shell.sf.net/home/yves");
            assertEquals(Protocol.SFTP, parser.getProtocol());
            assertEquals("/home/yves", parser.getPath());
            assertEquals("shell.sf.net", parser.getHostname());
            assertEquals(Protocol.SFTP.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertEquals("yves", parser.getUsername());
            assertNull(parser.getPassword());

            // test with common values 
            parser = new VFSURIParser("sftp://yves:yves@shell.sf.net/home/yves");
            assertEquals(Protocol.SFTP, parser.getProtocol());
            assertEquals("/home/yves", parser.getPath());
            assertEquals("shell.sf.net", parser.getHostname());
            assertEquals(Protocol.SFTP.getPort(),
                Integer.parseInt(parser.getPortnumber()));
            assertEquals("yves", parser.getUsername());
            assertEquals("yves", parser.getPassword());
        }
        catch (Exception ex)
        {
            fail("Error in the parser\n" + ex.getMessage());
        }
    }

    /**
     * parsing with a custom port number
     */
    public void testParseCustomPort()
    {
        logger.info("Testing VFSURIParser for remote files(custom port)\n");

        VFSURIParser parser;

        // test without username,password and path (custom port)
        parser = new VFSURIParser("sftp://shell.sf.net:28");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertNull(parser.getUsername());
        assertNull(parser.getPassword());

        // test without username and password (custom port)
        parser = new VFSURIParser("sftp://shell.sf.net:28/");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertNull(parser.getUsername());
        assertNull(parser.getPassword());

        // test without username and password (custom port)
        parser = new VFSURIParser("sftp://shell.sf.net:28/home/yves");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/home/yves", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertNull(parser.getUsername());
        assertNull(parser.getPassword());

        // test with common values but no password and no path specified
        parser = new VFSURIParser("sftp://yves@shell.sf.net:28");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertEquals("yves", parser.getUsername());
        assertNull(parser.getPassword());

        // test with common values and the root filesystem path
        parser = new VFSURIParser("sftp://yves:yves@shell.sf.net:28/");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertEquals("yves", parser.getUsername());
        assertEquals("yves", parser.getPassword());

        // test with common values but no password
        parser = new VFSURIParser("sftp://yves@shell.sf.net:28/home/yves");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/home/yves", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertEquals("yves", parser.getUsername());
        assertNull(parser.getPassword());

        // test with what the usual uri
        parser = new VFSURIParser("sftp://yves:yves@shell.sf.net:28/home/yves");
        assertEquals(Protocol.SFTP, parser.getProtocol());
        assertEquals("/home/yves", parser.getPath());
        assertEquals("shell.sf.net", parser.getHostname());
        assertEquals("28", parser.getPortnumber());
        assertEquals("yves", parser.getUsername());
        assertEquals("yves", parser.getPassword());
    }
}
