<?php
require "conn.php";
$user_name= $_POST["user"];
$user_pass = $_POST["pass"];

//$user_name= "1";
//$user_pass = "1111";

$sqlquery1 = "select studentid from student where studentid like '$user_name';";
$result = mysqli_query($con,$sqlquery1);

if(mysqli_num_rows($result)>0)
{
	$sqllogininfoqry = "select username from logininfo where username like '$user_name';";
	$numrows = mysqli_query($con, $sqllogininfoqry);
	
	if(mysqli_num_rows($numrows)>0)
	{
		echo "You are a Registered User already";
	}
	else
	{
		$sqlquery2 = "insert into logininfo values('$user_name','$user_pass');";
		if(mysqli_query($con,$sqlquery2))
		{
			echo "Successfully Registered";
		}
		else
		{
			echo "Error".mysqli_error($con);
		}
	}
}
else
{
	echo "Not a valid Register Number...Try again.";
}

?>