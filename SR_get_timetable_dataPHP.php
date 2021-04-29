<?php
require "conn.php";
$studentid = $_POST["login_userRegisterNumber"];
$givenday = $_POST["selected_day"];

//$studentid = "1";
//$givenday = "Wednesday";

//$sqlquery1 = "select studentid from student where studentid like '$user_name';";
$sqlquery = "SELECT student.studentmobile, student.studentname, timeschedule.day, timeschedule.subject, timeschedule.hour, timeschedule.department, timeschedule.semester, timeschedule.section, timeschedule.degreetype FROM student, timeschedule WHERE timeschedule.day like '$givenday' AND student.studentid like '$studentid' and student.degreetype = timeschedule.degreetype AND student.department = timeschedule.department AND student.Semester = timeschedule.semester and student.Section = timeschedule.section;";


$result = mysqli_query($con,$sqlquery);

if(mysqli_num_rows($result)>0)
{
	$response = array();
	
	while ($row = mysqli_fetch_array($result))
	{
		array_push($response, array("mobile" => $row[0], "studentname" => $row[1], "day" => $row[2], "subject" => $row[3], "hour" => $row[4], "department" => $row[5], "semester" => $row[6], "section" => $row[7], "degreetype" => $row[8]));
	}
	
	echo json_encode(array("server_response" => $response));
}
else
{
	echo "No Class Today";
}

mysqli_close($con);
?>