<?php

//request :http://localhost/HTTPParamPUT.php

$contentype = $_SERVER["CONTENT_TYPE"];

if (strpos($contentype, 'x-www-form-urlencoded') !== false) {
    parse_str(file_get_contents('php://input'), $put_vars);
    $userName = $put_vars['username'];
    $passWord = $put_vars['password'];
    checkInput($userName, $passWord);
} else if (strpos($contentype, 'json') !== false) {
    $data = file_get_contents('php://input');
    $data1 = json_decode($data, true);
    checkInput($data1['username'], $data1['password']);
} else if (strpos($contentype, 'multipart') !== false) {
    $files = array();
    $data = array();
    // Fetch content and determine boundary
    $rawData = file_get_contents('php://input');
    $boundary = substr($rawData, 0, strpos($rawData, "\r\n"));
    // Fetch and process each part
    $parts = array_slice(explode($boundary, $rawData), 1);

    if(count($parts)>0){
        $json = array("status" => TRUE, "message" => $parts);
        header('HTTP/1.1 200 OK', true, 200);
        response($json);
    }else{
         header('HTTP/1.1 400 Bad Request', true, 400);
    }


    foreach ($parts as $part) {
        // If this is the last part, break
        if ($part == "--\r\n") {
            break;
        }
        // Separate content from headers
        $part = ltrim($part, "\r\n");
        list($rawHeaders, $content) = explode("\r\n\r\n", $part, 2);
        $content = substr($content, 0, strlen($content) - 2);

        // Parse the headers list
        $rawHeaders = explode("\r\n", $rawHeaders);
        $headers = array();
        foreach ($rawHeaders as $header) {
            list($name, $value) = explode(':', $header);
            $headers[strtolower($name)] = ltrim($value, ' ');
        }
        // Parse the Content-Disposition to get the field name, etc.
        if (isset($headers['content-disposition'])) {
            $filename = null;
            preg_match(
                    '/^form-data; *name="([^"]+)"(; *filename="([^"]+)")?/', $headers['content-disposition'], $matches
            );

            $fieldName = $matches[1];
            $fileName = (isset($matches[3]) ? $matches[3] : null);
            // If we have a file, save it. Otherwise, save the data.
            if ($fileName !== null) {
                $localFileName = tempnam(sys_get_temp_dir(), 'sfy');
                file_put_contents($localFileName, $content);
                $files[$fieldName] = array(
                    'name' => $fileName,
                    'type' => $headers['content-type'],
                    'tmp_name' => $localFileName,
                    'error' => 0,
                    'size' => filesize($localFileName)
                );

                // save data to file .

            } else {
                //write file ;

            }
        }
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
        $json = array("status" => TRUE, "username" => $username, "password" => $password);
        header('HTTP/1.1 200 OK', true, 200);
        response($json);
    }
}

function response($json) {
    header('Content-type: application/json');
    echo json_encode($json);
}

?>
