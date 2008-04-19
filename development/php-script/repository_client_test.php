<?php
  require 'repository_client.php';

  $authToken = Repository_Client::login("we4tech", "hasankhan");
  echo "Authenticated token - $authToken\r\n";
  
  $items = Repository_Client::find($authToken, "content:(shilpakala academy)", 2, 2, "id", "true");
  foreach ($items as $itemRef) {
    print_r($itemRef);
    print_r(Repository_Client::getItem($authToken, $itemRef[0]));
  }

  // save new item
  $item = new Repository_Item();
  $item->title = "test from php script";
  $item->addField("test1", "Hello curl world");
  Repository_Client::save($authToken, $item);

  echo "\r\nnewly created Item id - " . $item->id . "\r\n";
  echo "removed ? - " . (Repository_Client::remove($authToken, $item->id) ? "yes" : "no") . "\r\n";
?>