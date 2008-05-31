<?php
  require 'repository_client.php';

  $authToken = Repository_Client::login("mac1", "mac1");
  echo "Authenticated token - $authToken\r\n";

  $item = new Repository_Item();
  $item->title = "nhm tanveer hossain khan";
  $item->addField("about", 'something went wrong');
  $item->addField("object_type", "profile");
  $item->addField("merged_text", "hasan dhaka");
  $item->addField("person_database_id", 232);
  $itemId = Repository_Client::save($authToken, $item);

//  echo Repository_Client::remove($authToken, 1433);

  $result = Repository_Client::find($authToken, "object_type: profile AND person_database_id: 232", array("select" => "person_database_id, about"));
  print_r($result);
  
  /*// save new item
  $prices = array(120000, 22000, 14900, 14000, 10000);
  foreach ($prices as $price) {
    $item = new Repository_Item();
    $item->title = "PHPTEST_ test from php script";
    $item->addField("test1", "Hello curl world");
    $item->addField("price", $price);
    $item->indexRepository = "aawaj";
    Repository_Client::save($authToken, $item);
  }*/

  // find item and sort by price
 /* $items = Repository_Client::find($authToken, "price:[0 TO 22000]", array("select" => "item_id, price"));
  foreach ($items as $item) {
    echo($item[2]["price"]);
    echo "\n";
    echo($item[2]["item_id"]);
    echo "\n";
    print_r(Repository_Client::getItem($authToken, $item[0]));
  }*/
?>