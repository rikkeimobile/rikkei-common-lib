<?php

//request : http://localhost/HTTPStringParamsRequestTest.php?username=tuyen && password=123
$userName = $_GET['username'];
$password = $_GET['password'];

$headers = parseRequestHeaders();

foreach ($headers as $header => $value) {
    echo "header = " . $header . " : value = " . $value . "</br>";
}


if ($userName == '' || $password == '') {
    echo 'HTTP request string is not working';
    $json = array("status" => false, "msg" => "params is in valid");
    header("HTTP/1.1 400 Bad Request");
    response($json);
} else {
    echo 'HTTP request string is working fine </br>';
    echo 'user = ' . $userName . '</br>';
    echo 'password = ' . $password;
    header("HTTP/1.1 200 OK");
    $json = array("status" => TRUE, "msg" => "http get string testcase passed");
    response($json);
}

function response($json) {
    header('Content-type: application/json');
    echo json_encode($json);
}

function parseRequestHeaders() {
    $headers = array();
    foreach ($_SERVER as $key => $value) {
        if (substr($key, 0, 5) <> 'HTTP_') {
            continue;
        }
        $header = str_replace(' ', '-', ucwords(str_replace('_', ' ', strtolower(substr($key, 5)))));
        $headers[$header] = $value;
    }
    return $headers;
}

?>
