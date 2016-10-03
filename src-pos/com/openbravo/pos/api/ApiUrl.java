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

import com.google.api.client.http.GenericUrl;

/**
 *
 * @author filip
 */
public class ApiUrl extends GenericUrl {

    private ApiUrl(String encodedUrl) {
        super(baseUri + encodedUrl);
    }

    private static String baseUri = "https://zargate.org/webservice/";

    public static void setBaseUri(String baseUri) {
        ApiUrl.baseUri = baseUri;
    }

    public static String getBaseUri() {
        return baseUri;
    }

    public static ApiUrl ReceiptUpload() {
        return new ApiUrl("?method=pos&action=add");
    }
}
