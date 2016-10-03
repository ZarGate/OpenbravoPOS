/*
 * Copyright (C) 2016 filip
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
package com.openbravo.pos.api;

import com.openbravo.pos.api.model.OrderReceipt;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.util.ArrayList;

/**
 *
 * @author filip
 */
public class ReceiptHelper {

    public static OrderReceipt buildOrderReceipt(TicketInfo ticketInfo) {
        StringBuilder builder = new StringBuilder();
        OrderReceipt orderReceipt = null;
        if (ticketInfo != null && ticketInfo.getLines() != null && ticketInfo.getLines().size() > 0) {
            orderReceipt = new OrderReceipt();
            orderReceipt.id = Integer.toString(ticketInfo.getTicketId());
            orderReceipt.total = Double.toString(ticketInfo.getTotal()) + "kr";
            orderReceipt.lines = new ArrayList<>();
            for (TicketLineInfo l : ticketInfo.getLines()) {
                builder.append(l.getMultiply());
                builder.append(" x ");
                builder.append(l.getProductName());
                builder.append(" = kr ");
                builder.append(l.getMultiply() * l.getPrice());
                orderReceipt.lines.add(builder.toString());
                builder.setLength(0); // clear
            }
        }
        return orderReceipt;
    }

}
