CREATE USER 'sinf'@'localhost' IDENTIFIED BY 'classT3';

GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,DROP,INDEX,ALTER
ON phplab.* TO 'sinf'@'localhost' IDENTIFIED BY 'classT3'
WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0 ;

CREATE DATABASE `phplab` ;

CREATE TABLE `phplab`.`employee` (
`ssn` INT( 3 ) NOT NULL ,
`lastname` VARCHAR( 30 ) NOT NULL ,
`firstname` VARCHAR( 30 ) NOT NULL ,
`email` VARCHAR( 50 ) NOT NULL
) ENGINE = MYISAM;

ALTER TABLE `phplab`.`employee` ADD PRIMARY KEY ( `ssn` );

INSERT INTO `phplab`.`employee` (
`ssn` ,
`lastname` ,
`firstname` ,
`email`
)
VALUES
('9', 'Einstein', 'Albert', 'einstein@company.com'),
('99', 'Newton', 'Isaac', 'newton@company.com'),
('999', 'Doe', 'John', 'doe@company.com');
