/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.ws;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import io.jsonwebtoken.Jwts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class DPPPTAuthzControllerTest extends BaseControllerTest {
	@Test
	public void testHello() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(get("/v1"))
				.andExpect(status().is2xxSuccessful()).andReturn().getResponse();

		assertNotNull(response);
		assertEquals("Hello from DP3T Authz WS", response.getContentAsString());
	}

	@Test
	public void testGetCodes() throws Exception {
		MockHttpServletResponse response200 = mockMvc.perform(get("/v1/codes?start=0&size=10&sort=specimen_no&order=ASC&all=Y"))
				.andExpect(status().is2xxSuccessful()).andReturn().getResponse();

		assertNotNull(response200);
		
	}

	@Test
	public void testSqlInjectOnQuery() throws Exception {
		
		MockHttpServletResponse response400 = mockMvc.perform(get("/v1/codes?all=false&q=1%27%20UNION%20SELECT%201%2C2%2C3--%2B"))
				.andExpect(status().is4xxClientError()).andReturn().getResponse();

		assertNotNull(response400);
		assertEquals("Invalid arguments", response400.getContentAsString());
	}
	
	@Test
	public void testSqlInjectOnSort() throws Exception {
		
		MockHttpServletResponse response400 = mockMvc.perform(get("/v1/codes?all=false&sort=1%27%20UNION%20SELECT%201%2C2%2C3--%2B"))
				.andExpect(status().is4xxClientError()).andReturn().getResponse();

		assertNotNull(response400);
		assertEquals("Invalid arguments", response400.getContentAsString());

	}

	@Test
	public void testSqlInjectOnOrder() throws Exception {
		
		MockHttpServletResponse response400 = mockMvc.perform(get("/v1/codes?all=false&order=1%27%20UNION%20SELECT%201%2C2%2C3--%2B"))
				.andExpect(status().is4xxClientError()).andReturn().getResponse();

		assertNotNull(response400);
		assertEquals("Invalid arguments", response400.getContentAsString());

	}

}
