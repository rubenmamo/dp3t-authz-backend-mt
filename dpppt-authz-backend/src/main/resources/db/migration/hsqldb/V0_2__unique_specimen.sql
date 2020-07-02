
/*
 * Created by Malta Information Technology Agency
 * https://mita.gov.mt
 * Copyright (c) 2020. All rights reserved.
 */

set database sql syntax PGS true;

ALTER TABLE t_covid_code ADD CONSTRAINT c_specimen_no UNIQUE (specimen_no);
