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
package com.openbravo.pos.payment.mcash;

import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.util.ArrayList;
import java.util.List;
import net.brennheit.mcashapi.resource.PaymentRequestLink;

/**
 *
 * @author filip
 */
public class McashHelper {

    public static List<PaymentRequestLink> createLinks(TicketInfo ticketInfo, String uri) {
        List<PaymentRequestLink> links = new ArrayList<>();
        PaymentRequestLink link = new PaymentRequestLink();
        link.show_on = new ArrayList<>();
        link.show_on.add("ok");
        link.caption = "Kvittering";
        link.uri = uri + ticketInfo.getId();
        links.add(link);
        return links;
    }

    public static String buildTempReceiptForPaymentRequest(TicketInfo ticketInfo) {
        StringBuilder builder = new StringBuilder();
        if (ticketInfo != null && ticketInfo.getLines() != null && ticketInfo.getLines().size() > 0) {
            for (TicketLineInfo l : ticketInfo.getLines()) {
                builder.append(l.getMultiply());
                builder.append(" x ");
                builder.append(l.getProductName());
                builder.append(" = kr ");
                builder.append(l.getMultiply() * l.getPrice());
                builder.append(System.getProperty("line.separator"));
            }
        }
        return builder.toString();
    }

}
