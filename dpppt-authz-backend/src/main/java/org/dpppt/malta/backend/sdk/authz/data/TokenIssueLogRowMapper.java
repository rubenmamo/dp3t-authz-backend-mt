/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.TokenIssueLog;

public class TokenIssueLogRowMapper implements RowMapper<TokenIssueLog> {
	@Override
	public TokenIssueLog mapRow(ResultSet rs, int rowNum) throws SQLException {
		TokenIssueLog log = new TokenIssueLog();
		log.setUuid(UUID.fromString(rs.getString("uuid")));
		log.setIssuedAt(rs.getTimestamp("issued_at").toInstant());
		
		return log;
	}
}