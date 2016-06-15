<?php

//request :http://localhost/HTTPParamDelete.php

$userName = $_GET['username'];
$passWord = $_GET['password'];
checkInput($userName, $passWord);

function checkInput($username, $password) {
    if ($username == '' || $password == '') {
        header('HTTP/1.1 400 Bad Request', true, 400);
    } else {
        $json = array("status" => TRUE, "msg" => "delete data succeed");
        header('HTTP/1.1 200 OK', true, 200);
        response($json);
    }
}

function response($json) {
    header('Content-type: application/json');
    echo json_encode($json);
}
?>
