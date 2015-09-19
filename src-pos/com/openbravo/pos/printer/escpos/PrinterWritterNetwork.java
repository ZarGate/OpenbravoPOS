/*
 * Copyright (C) 2015 filip
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.printer.escpos;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author filip
 */
public class PrinterWritterNetwork extends PrinterWritter {

    private final String m_sHost;
    private final int m_port;
    private OutputStream m_out;
    private Socket m_clientSocket;
    private int m_count = 0;

    public PrinterWritterNetwork(String host) {
        this(host, 9100);
    }

    public PrinterWritterNetwork(String sHost, int port) {
        m_sHost = sHost;
        m_port = port;
        m_out = null;
        m_clientSocket = null;
    }

    private void createSocketConnection() {
        try {
            if (m_clientSocket == null) {
                m_clientSocket = new Socket(m_sHost, m_port);
            }
            m_out = m_clientSocket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(PrinterWritterNetwork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(PrinterWritterNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void internalWrite(byte[] data) {
        if (m_out == null) {
            createSocketConnection();
        }
        try {
            m_count++;
            m_out.write(data);
        } catch (IOException ex) {
            Logger.getLogger(PrinterWritterNetwork.class.getName()).log(Level.SEVERE, "Tried to print data " + (new String(data)), ex);
            if (m_count < 10) {
                internalClose();
                createSocketConnection();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(PrinterWritterNetwork.class.getName()).log(Level.SEVERE, null, ex1);
                }
                internalWrite(data);
            }
        } catch (Exception ex) {
            Logger.getLogger(PrinterWritterNetwork.class.getName()).log(Level.SEVERE, null, ex);
            if (m_count < 10) {
                internalWrite(data);
            }
        }
        m_count = 0;
    }

    @Override
    protected void internalFlush() {
        try {
            if (m_out != null) {
                m_out.flush();
            }
            internalClose();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    protected void internalClose() {
        try {
            if (m_out != null) {
                m_out.flush();
                m_out.close();
                m_out = null;
                if (m_clientSocket != null) {
                    m_clientSocket.close();
                    m_clientSocket = null;
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
