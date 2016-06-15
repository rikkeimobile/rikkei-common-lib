<?php

//request :http://localhost/HTTPStringParamsPost.php

$contentype = $_SERVER["CONTENT_TYPE"];

if (strpos($contentype, 'x-www-form-urlencoded') !== false) {
    $userName = urldecode($_POST['username']);
    $passWord = urldecode($_POST['password']);
    $function = 1;
    checkInput($userName, $passWord);
} else if (strpos($contentype, 'json') !== false) {
    $data = file_get_contents('php://input');
    $data1 = json_decode($data, true);
    checkInput($data1['username'], $data1['password']);
} else if (strpos($contentype, 'multipart') !== false) {
    $userName = $_POST['username'];
    $passWord = $_POST['password'];

    if (is_uploaded_file($_FILES['file']['tmp_name'])) {
        $total = count($_FILES['file']['tmp_name']);
        for ($i = 0; $i < $total; $i++) {
            $uploads_dir = './';
            $tmp_name = $_FILES['file']['tmp_name'];
            $pic_name = $_FILES['file']['name'];
            move_uploaded_file($tmp_name, $uploads_dir . $pic_name);
        }
        $json = array("status" => TRUE, "msg" => "upload success", "username" => $userName, "password" => $passWord);
        response($json);
    } else {
        header('HTTP/1.1 400 Bad Request', true, 400);
    }
} else {
    $userName = urldecode($_POST['username']);
    $passWord = urldecode($_POST['password']);
    $function = 1;
    checkInput($userName, $passWord);
}

function checkInput($username, $password) {
    if ($username == '' || $password == '') {
        header('HTTP/1.1 400 Bad Request', true, 400);
        header('Content-type: text/plain; charset = UTF8');
        echo'invalid request , username or pass word must not null';
    } else {
        $json = array("status" => TRUE, "msg" => "connected correct at all");
        header('HTTP/1.1 200 OK', true, 200);
        response($json);
    }
}

function response($json) {
    header('Content-type: application/json');
    echo json_encode($json);
}

?>
