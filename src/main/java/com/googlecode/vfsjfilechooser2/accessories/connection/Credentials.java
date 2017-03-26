/*
 * Representation of users credentials
 *
 * Copyright (C) 2008-2009 Yves Zoundi
 * Copyright (C) 2017 University of Waikato, Hamilton NZ
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
package com.googlecode.vfsjfilechooser2.accessories.connection;


/**
 * Representation of users credentials
 * @author Yves Zoundi <yveszoundi at users dot sf dot net>
 * @version 0.0.1
 */
public class Credentials
{
    private int port = -1;
    private String username = "";
    private char[] password = new char[0];
    private String hostname = "";
    private String protocol = "";
    private String defaulRemotetPath = "";

    /**
     * Create new credentials
     * @param hostname The hostname
     */
    protected Credentials(String hostname)
    {
        this.hostname = hostname;
    }

    /**
     * Create a new credentials builder
     * @param hostname The hostname
     * @return A credentials builder
     */
    public static Builder newBuilder(String hostname)
    {
        return new Builder(hostname);
    }

    /**
     * @return the complete path of the directory to browse
     */
    public String toFileObjectURL()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(protocol.toLowerCase());
        sb.append("://");

        if (!protocol.toLowerCase().equals("file"))
        {
            if (!("".equals(username.trim())))
            {
                sb.append(this.username);

                if (password.length != 0)
                {
                    sb.append(":");
		    for (int i = 0; i < this.password.length; i++)
		    {
		        if (this.password[i] == '@')
			{
			    sb.append("%40");
			}
		        else
			{
			    sb.append(this.password[i]);
			}
		    }
                }

                sb.append("@");
            }

            sb.append(this.hostname);

            if (port != -1)
            {
                sb.append(":").append(this.port);
            }
        }

        return sb.append(defaulRemotetPath).toString();
    }

    /**
     * The default remote path
     * @return the default remote path
     */
    public String getDefaulRemotetPath()
    {
        return defaulRemotetPath;
    }

    /**
     * @return the hostname
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * @return the password
     */
    public char[] getPassword()
    {
        return password.clone();
    }

    /**
     * @return the port number
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @return the server type
     */
    public String getProtocol()
    {
        return protocol;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Builder for credentials (Builder pattern - static variant)
     * @author Yves Zoundi
     * @version 0.0.1
     */
    public static final class Builder
    {
        private Credentials m_credentials;
        private String m_username;
        private char[] m_password;
        private String m_defaultRemotePath;
        private final String m_hostname;
        private int m_port;
        private String m_protocol;

        /**
         * Create a new Credentials builder</code>
         * @param m_hostname The hostname
         */
        protected Builder(String m_hostname)
        {
            this.m_hostname = m_hostname;
        }

        /**
         * Returns a <code>Credentials</code> object
         * @return a <code>Credentials</code> object
         */
        public Credentials build()
        {
            m_credentials = new Credentials(m_hostname);

            m_credentials.username = this.m_username;
            m_credentials.defaulRemotetPath = this.m_defaultRemotePath;
            m_credentials.password = this.m_password;
            m_credentials.protocol = this.m_protocol;
            m_credentials.port = this.m_port;

            return m_credentials;
        }

        /**
         * Set the password
         * @param m_password The password char array
         * @return this builder
         */
        public Builder password(char[] m_password)
        {
            this.m_password = m_password.clone();

            return this;
        }

        /**
         * Set the server type
         * @param m_protocol The server type
         * @return this builder
         */
        public Builder protocol(String m_protocol)
        {
            this.m_protocol = m_protocol;

            return this;
        }

        /**
         * Set the port number
         * @param m_portNumber The port number
         * @return this builder
         */
        public Builder port(int m_portNumber)
        {
            this.m_port = m_portNumber;

            return this;
        }

        /**
         * Set the default remote path
         * @param m_defaultRemotePath The default remote path
         * @return this builder
         */
        public Builder defaultRemotePath(String m_defaultRemotePath)
        {
            this.m_defaultRemotePath = m_defaultRemotePath;

            return this;
        }

        /**
         * Set the username
         * @param m_username The username
         * @return this builder
         */
        public Builder username(String m_username)
        {
            this.m_username = m_username;

            return this;
        }
    }
}
