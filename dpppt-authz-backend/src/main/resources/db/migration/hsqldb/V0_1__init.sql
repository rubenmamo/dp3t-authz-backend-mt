
/*
 * Created by Malta Information Technology Agency
 * https://mita.gov.mt
 * Copyright (c) 2020. All rights reserved.
 */

set database sql syntax PGS true;

CREATE TABLE t_covid_code(
 pk_covid_code_id Serial NOT NULL,
 specimen_no Character varying(50) NOT NULL, 
 receive_date Date NOT NULL, 
 onset_date Date NOT NULL, 
 transmission_risk character(1) NOT NULL, 
 auth_code character(12) NOT NULL, 
 registered_at Timestamp with time zone DEFAULT now() NOT NULL,
 registered_by Character varying(30) NOT NULL, 
 expires_at Timestamp with time zone NULL,
 revoked_at Timestamp with time zone NULL,
 revoked_by Character varying(30) NULL,
 redeemed_at Timestamp with time zone NULL
);

ALTER TABLE t_covid_code ADD CONSTRAINT PK_t_covid_code PRIMARY KEY (pk_covid_code_id);

ALTER TABLE t_covid_code ADD CONSTRAINT key UNIQUE (auth_code);

CREATE TABLE t_token_log (
 pk_token_id Serial NOT NULL,
 fk_covid_code_id  NUMERIC NOT NULL, 
 uuid Character varying(50) NOT NULL, 
 issued_at Timestamp with time zone NOT NULL,
 PRIMARY KEY (pk_token_id),
 FOREIGN KEY (fk_covid_code_id) REFERENCES t_covid_code (pk_covid_code_id) ON DELETE CASCADE
);
