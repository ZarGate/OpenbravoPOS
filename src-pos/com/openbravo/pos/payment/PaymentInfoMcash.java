//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2008-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.payment;

import net.brennheit.mcashapi.resource.PaymentRequestOutcome;

public class PaymentInfoMcash extends PaymentInfo {
     
    protected PaymentRequestOutcome m_paymentRequestOutcome;
    
    /** Creates a new instance of PaymentInfoMcash */
    public PaymentInfoMcash(PaymentRequestOutcome paymentRequestOutcome) {
        m_paymentRequestOutcome = paymentRequestOutcome;
    }
    
    @Override
    public PaymentInfo copyPayment(){
        PaymentInfoMcash p = new PaymentInfoMcash(m_paymentRequestOutcome);
        return p;
    }    
    
    @Override
    public String getName() {
        return "mcash";
    }

    @Override
    public double getTotal() {
        return Double.parseDouble(m_paymentRequestOutcome.amount);
    }

    @Override
    public String getTransactionID() {
        return m_paymentRequestOutcome.tid;
    }
    
    public String printTicketID(){
        if(m_paymentRequestOutcome != null){
            return m_paymentRequestOutcome.tid;
        }
        return "NaN";
    }
}
