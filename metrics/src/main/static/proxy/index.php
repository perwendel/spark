<?php
/**
 * Date: 06/06/2012
 * Time: 13:27
 * proxy for getting json data via CURL
 */

header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-type: application/json');

$url = $_REQUEST['url'];

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_HEADER, 0);

$response = curl_exec($ch);
curl_close($ch);

// echo $response;
?>