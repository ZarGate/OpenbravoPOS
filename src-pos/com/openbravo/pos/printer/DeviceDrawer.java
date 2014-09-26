/*
 * Copyright (C) 2014 fiLLLip <filip@tomren.it>
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
package com.openbravo.pos.printer;

import com.openbravo.pos.forms.AppProperties;
import com.openbravo.pos.util.StringParser;
import java.awt.Component;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fiLLLip <filip@tomren.it>
 */
public class DeviceDrawer {

    private final Component m_parent;
    private final AppProperties m_props;
    private final String m_sType;
    private final String m_sExecFile;

    public DeviceDrawer(Component parent, AppProperties props) {
        this.m_parent = parent;
        this.m_props = props;
        StringParser p = new StringParser(m_props.getProperty("machine.drawer"));
        m_sType = p.nextToken(':');
        if ("executable".equals(m_sType)) {
            m_sExecFile = p.nextToken(',');
        } else {
            m_sExecFile = null;
        }
    }

    public String getType() {
        return m_sType;
    }

    public void openDrawer(String key) {
        if("executable".equals(m_sType)){
            //m_printer.getDevicePrinter(readString(attributes.getValue("printer"), "1")).openDrawer();
                String[] cmd = { m_sExecFile };
                try {
                    Process p = Runtime.getRuntime().exec(cmd);
                    p.waitFor();
                } catch (IOException ex) {
                    Logger.getLogger(TicketParser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(TicketParser.class.getName()).log(Level.SEVERE, null, ex);
                }
        } else if ("printer".equals(m_sType)){
            // Do nothing here yet...      
        }
    }
}
