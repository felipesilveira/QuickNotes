<?php

if($_POST['nota']) {
	$db_host = 'android-core.felipesilveira.com.br';
	$db_user = 'android_core';
	$db_pwd = 'android12';

	$database = 'android_core';
	$table = 'notes';

	if (!mysql_connect($db_host, $db_user, $db_pwd))
	    die("0");

	if (!mysql_select_db($database))
	    die("0");
	
	if(mysql_query("INSERT INTO `notes` (nota) VALUES '" . $_POST['nota'] ."'")) {
		echo "1";
	} else {
		echo "0";
	}
	
} else {
	echo "0";
}

    


 ?>