<?php
require_once 'connection.php';
header('Content-Type: application/json ');
	class User {
		private $db;
		private $connection;

		function __construct(){
			$this->db = new DB_Connection();
			$this->connection = $this->db->get_connection();
		}

		public function does_user_exist($username, $password, $first, $last, $orgname, $address, $phoneNumber, $email, $dorr){
			$query = "Select * from users where username = '$username'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$json['error'] = 'User already exists.';
				echo json_encode($json);
				mysqli_close($this->connection);
			}else{
				$phoneNumber = intval($phoneNumber);
				$query = "Insert into users(first, last, email, dorr, username, password, orgname, address, phoneNumber) values ('$first', '$last', '$email', '$dorr', '$username', '$password', '$orgname', '$address', '$phoneNumber')";
				$is_inserted = mysqli_query($this->connection, $query);
				if($is_inserted == 1) {
					$json['success'] = 'Account created';
				}else{
					$json['error'] = ' Wrong password ';
				}

				echo json_encode($json);
				mysqli_close($this->connection);
			}
		}
	}

	$user = new User();
	

	if(isset($_POST['password'], $_POST['username'], $_POST['first'], $_POST['last'], $_POST['orgname'], $_POST['address'], $_POST['phoneNumber'], $_POST['email'], $_POST['dorr'])) {

		$password = $_POST['password'];
		$username = $_POST['username'];
		$first = $_POST['first'];
		$last = $_POST['last'];
		$orgname = $_POST['orgname'];
		$address = $_POST['address'];
		$phoneNumber = $_POST['phoneNumber'];
		$email = $_POST['email'];
		$dorr = $_POST['dorr'];
                
        


		if(!empty($password)&&!empty($username)&&!empty($first)&&!empty($last)&&!empty($orgname)&&!empty($address)&&!empty($phoneNumber)&&!empty($email)&&!empty($dorr)) {
			$encrypted_password = md5($password);
			$encrypted_first = md5($first);
			$encrypted_last = md5($last);
			$encrypted_email = md5($email);
			$user -> does_user_exist($username, $encrypted_password, $first, $last, $orgname, $address, $phoneNumber, $email, $dorr);
		}else {
			$json['error'] = 'You must fill in all fields';
			echo json_encode($json);
		}
	}

	else {
		$json['fail'] = 'You did not pass the first if statement :(';
		echo json_encode($json);
	}

 	
 ?>