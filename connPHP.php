<?php

$host = "localhost";
$user = "root";
$password = "";
$dbname = "callforclass";

$con = mysqli_connect($host,$user,$password,$dbname);

if(!$con)
{
die("Error".mysqli_connect_error());
}
else
{
//echo "<h3>Database connection Success.....";
}

?>