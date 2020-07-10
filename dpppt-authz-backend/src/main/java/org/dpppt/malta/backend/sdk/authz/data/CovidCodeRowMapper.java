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
import org.springframework.jdbc.core.RowMapper;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;

public class CovidCodeRowMapper implements RowMapper<CovidCode> {
	@Override
	public CovidCode mapRow(ResultSet rs, int rowNum) throws SQLException {
		CovidCode covidCode = new CovidCode();
		covidCode.setId(rs.getInt("pk_covid_code_id"));
		covidCode.setSpecimenNumber(rs.getString("specimen_no"));
		covidCode.setReceiveDate(rs.getDate("receive_date").toLocalDate());
		covidCode.setOnsetDate(rs.getDate("onset_date").toLocalDate());
		covidCode.setTransmissionRisk(rs.getString("transmission_risk"));
		covidCode.setAuthorisationCode(rs.getString("auth_code"));
		covidCode.setRegisteredAt(rs.getTimestamp("registered_at").toInstant());
		covidCode.setRegisteredBy(rs.getString("registered_by"));
		covidCode.setExpiresAt(rs.getTimestamp("expires_at").toInstant());
		Timestamp rvts = rs.getTimestamp("revoked_at");
		if (null != rvts) {
			covidCode.setRevokedAt(rvts.toInstant());
			covidCode.setRevokedBy(rs.getString("revoked_by"));		
		}
		Timestamp rdts = rs.getTimestamp("redeemed_at");
		if (null != rdts) {
			covidCode.setRedeemedAt(rdts.toInstant());
		}
		
		return covidCode;
	}
}