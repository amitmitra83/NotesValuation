CREATE TABLE borrower_type (
  BRW_TYPE_ID INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  BORROWER_TYPE VARCHAR(45) NOT NULL UNIQUE,
  DESCRIPTION VARCHAR(45) NULL,
   UPDATED_DATE_TIME DATE NULL,
  UPDATED_BY  VARCHAR(45) NULL,
  CREATED_DATE_TIME DATE NULL,
  CREATED_BY  VARCHAR(45) NULL);
  