package org.dpppt.malta.backend.sdk.authz.ws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import org.dpppt.malta.backend.sdk.authz.data.AuthzDataService;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCodesPage;
import org.dpppt.malta.backend.sdk.authz.ws.config.WSBaseConfig;
import org.dpppt.malta.backend.sdk.authz.ws.config.WSDevConfig;
import org.dpppt.malta.backend.sdk.authz.ws.util.CovidCodeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { WSBaseConfig.class,
		WSDevConfig.class })
@ActiveProfiles({ "cloud-dev" })
public class DataServiceTest {

	@Autowired
	private AuthzDataService authzDataService;

	private CovidCode newCovidCode() {
		CovidCode tmpKey = new CovidCode();
		String c = CovidCodeUtils.generateRandom12DigitCode();
		tmpKey.setSpecimenNumber(c);
		tmpKey.setAuthorisationCode(c);
		tmpKey.setExpiresAt(Instant.now());
		tmpKey.setOnsetDate(LocalDate.now().minusDays(1));
		tmpKey.setReceiveDate(LocalDate.now());
		tmpKey.setRegisteredBy("Test");
		tmpKey.setRegisteredAt(Instant.now());
		tmpKey.setTransmissionRisk("0");
		return tmpKey;
	}
	
	@Test
	public void insertCovidCode() throws Exception {
		CovidCode tmpKey = newCovidCode();
		
		CovidCode res = authzDataService.insertCovidCode(tmpKey);
		
		assertNotNull(res);
		assertNotNull(res.getId());
		assertNotEquals(res.getId(), 0);		
		
		CovidCode res2 = authzDataService.get(res.getId());
		
		assertNotNull(res2);
		assertEquals(res.getId(), res2.getId());
		
	}

	@Test
	public void testPagination() throws Exception {
		
		authzDataService.cleanDB(Duration.ZERO);
		for (int i = 0; i < 100; i++) {
			authzDataService.insertCovidCode(newCovidCode());
		}
		
		CovidCodesPage p = authzDataService.search(null, true, 1, 10, null, false);
		assertEquals(p.getTotal(),100);
		assertEquals(p.getCovidCodes().size(), 10);

		p = authzDataService.search(null, true, 95, 10, null, false);
		assertEquals(p.getTotal(),100);
		assertEquals(p.getCovidCodes().size(), 5);
		
	}
	
}
