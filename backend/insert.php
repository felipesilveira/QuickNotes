<?php

/* DB Structure:


CREATE TABLE IF NOT EXISTS `notes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nota` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=104 ;


*/

if($_POST['nota']) {
	$db_host = '';
	$db_user = '';
	$db_pwd = '';

	$database = 'android_core';
	$table = 'notes';

	if (!mysql_connect($db_host, $db_user, $db_pwd))
	    die("0");

	if (!mysql_select_db($database))
	    die("0");
	
	if(mysql_query("INSERT INTO notes (nota) VALUES ('" . $_POST['nota'] ."')")) {
		echo "1";
	} else {
		//echo mysql_error();
		echo "0";
	}
	
} else {
	echo "0";
}

    


 ?>