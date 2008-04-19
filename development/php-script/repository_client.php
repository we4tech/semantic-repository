<?php
/**
 * $Id$
 * *****************************************************************************
 *    Copyright (C) 2005 - 2007 somewhere in .Net ltd.
 *    All Rights Reserved.  No use, copying or distribution of this
 *    work may be made except in accordance with a valid license
 *    agreement from somewhere in .Net LTD.  This notice must be included on
 *    all copies, modifications and derivatives of this work.
 * *****************************************************************************
 * $LastChangedBy$
 * $LastChangedDate$
 * $LastChangedRevision$
 * *****************************************************************************
 */
 require("eac_curl_stream/eac_curl.class.php");
 define('BASE_REPOSITORY_URL', "http://localhost:1990/rest");
 define('PARAM_AUTH_TOKEN', "authToken");

 /**
 * Repositroy item object
 */
 class Repository_Item {
   var $id;
   var $title;
   var $createdOn;
   var $lastUpdatedOn;
   var $indexRepository;
   var $mFields = array();

   function Repository_Item() {
     $this->createdOn = time();
     $this->lastUpdatedOn = time();
     $indexRepository = "default";
   }

   function addField($pKey, $pValue) {
     $this->mFields[$pKey] = $pValue;
   }

   function removeField($pKey) {
     $this->mFields[$pKey] = null;
   }

   function getField($pKey) {
     return $this->mFields[$pKey];
   }

   function getFields() {
     return $this->mFields;
   }

   function toRequest() {
     $request = "<request><item>";
     if ($this->id != null) {
       $request .= "<id>" . $this->id . "</id>";
     }
     $request .= "<title><![CDATA[" . $this->title . "]]></title>";
     $request .= "<fields>";
     foreach ($this->mFields as $field => $value) {
       $request .= "<field name='$field'><![CDATA[$value]]></field>";       
     }
     $request .= "</fields>";
     $request .= "</item></request>";
     return $request;
   }
 }

 /**
 * Http client library is used to request repository server to store, delete
 * or perform some search operation.
 */
 class Repository_Http_Client {
   function post($pUri, $pParams = null, $pHeaders = null) {
     $ch = curl_init();
     $url = BASE_REPOSITORY_URL . $pUri;
     curl_setopt($ch, CURLOPT_URL, $url);
     curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
     curl_setopt($ch, CURLOPT_TIMEOUT, 4);
     if ($pHeaders != null) {
       curl_setopt($ch, CURLOPT_HTTPHEADER, $pHeaders);
     }
     curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($pParams));

     $data = curl_exec($ch);
     curl_close($ch);

     return $data;
   }

   function deleteRequest($pUri, $pParams = null, $pHeaders = null) {
     $ch = curl_init();
     $url = BASE_REPOSITORY_URL . $pUri;
     if ($pParams != null && !empty($pParams)) {
       $url .= "?" . http_build_query($pParams);
     }
     curl_setopt($ch, CURLOPT_URL, $url);
     curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
     curl_setopt($ch, CURLOPT_TIMEOUT, 4);
     curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "DELETE");

     if ($pHeaders != null) {
       curl_setopt($ch, CURLOPT_HTTPHEADER, $pHeaders);
     }

     $data = curl_exec($ch);
     curl_close($ch);

     return $data;
   }

   function get($pUri, $pParams = null) {
     $url = BASE_REPOSITORY_URL . $pUri;
     if ($pParams != null) {
       $url .= "?" . http_build_query($pParams);
     }
     $options = array();
     $options['CURLOPT_AUTOREFERER'] = 1;
     $options['CURLOPT_CRLF'] 		= 1;
     $options['CURLOPT_NOPROGRESS'] 	= 1;
     $http = new cURL($options);
     $http->get($url);
     $response = $http->getLastResult();
     return $response;
   }
 }

 /**
 * Repository client implemenation. this class is used to encapsulate all
 * underlaying implementation related commplexity.
 */
 class Repository_Client {

   function login($pUser, $pPassword) {
     $phpResponse = Repository_Http_Client::post("/service/login/u&p.php", array("u" => $pUser, "p" => $pPassword));
     $evaledObject = eval($phpResponse);
     if (true == $evaledObject["state"]) {
       return $evaledObject["authToken"];
     } else {
      return false;
     }
   }

   function getItem($pAuthToken, $pItemId) {
     $response = Repository_Http_Client::get($pItemId . ".php", array(PARAM_AUTH_TOKEN => $pAuthToken));
     $object = eval($response);
     if ($object["state"]) {
       $itemObject = $object[0];
       $item = new Repository_Item();
       $item->id = $itemObject["id"];
       $item->title = $itemObject["title"];
       $item->createdOn = $itemObject["createdOn"];
       $item->lastUpdatedOn = $itemObject["lastUpdatedOn"];
       $item->indexRepository = $itemObject["indexRepository"];
       if ($item->indexRepository == 'null') {
         $item->indexRepository = "default";
       }
       foreach ($itemObject["fields"] as $key => $value) {
         $item->addField($key, $value);
       }
       return $item;
     }
     return false;
   }

   function formatItemId($pItemUri) {
     return preg_replace('/[a-zZ-a\/\.]+/', '', $pItemUri);
   }

   function find($pAuthToken, $pQuery, $pOffset = 0, $pMax = 10, $pSortBy = null, $pOrder = null) {
     $params = array(PARAM_AUTH_TOKEN => $pAuthToken, "q" => $pQuery, "max" => $pMax, "offset" => $pOffset);
     if (!empty($pSortBy)) {
      $params["sortby"] = $pSortBy;
     }
     if (!empty($pOrder)) {
      $params["order"] = $pOrder;    
     }
     $response = Repository_Http_Client::get("/service/find/q.php",$params);
     $object = eval($response);
     $items = array();
     if ($object["state"]) {
       $index = 0;
       foreach ($object["items"] as $item) {
         $items[$index] = $item;
         $index++;
       }
     }
     return $items;
   }

   function save($pAuthToken, &$pItem) {
     $response = Repository_Http_Client::post("/service/save/i.xml", array(PARAM_AUTH_TOKEN => $pAuthToken, "i" => $pItem->toRequest()));
     if (preg_match("/state=\"true\"/", $response)) {
       $xml = simplexml_load_string($response, 'SimpleXMLElement', LIBXML_NOCDATA);
       $itemId = Repository_Client::formatItemId($xml->item);
       $pItem->id = $itemId;
       return $itemId;
     }
     return false;
   }

   function remove($pAuthToken, $pItemId) {
     $params = array(PARAM_AUTH_TOKEN => $pAuthToken);
     $response = Repository_Http_Client::deleteRequest("/service/delete/$pItemId.php", $params);
     $object = eval($response);
     if ($object["state"]) {
       return true;
     }
     return false;
   }

 }
?>