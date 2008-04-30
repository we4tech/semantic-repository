<?php
  require 'repository_client.php';

  $authToken = Repository_Client::login("mac1", "mac1");
  echo "Authenticated token - $authToken\r\n";
  
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
  $items = Repository_Client::find($authToken, "price:[0 TO 22000]", array("select" => "item_id, price"));
  foreach ($items as $item) {
    echo($item[2]["price"]);
    echo "\n";
    echo($item[2]["item_id"]);
    echo "\n";
    print_r(Repository_Client::getItem($authToken, $item[0]));
  }
?>