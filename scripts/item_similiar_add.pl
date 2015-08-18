#! /usr/bin/perl
use strict;
use warnings;
use Net::STOMP::Client;
use File::Path qw/make_path/;
use JSON;

my $queue_host = "127.0.0.1";
my $queue_port = 61613;
my $queue_user = "admin";
my $queue_password = "admin";
my $target_directory = "/volumes/neo/items";

print "Connecting to host [$queue_host] port [$queue_port] as user [$queue_user]\n";
my $stomp = Net::STOMP::Client->new(host => $queue_host, port => $queue_port);
$stomp->connect(login => $queue_user, passcode => $queue_password);

print "Subscribing to topic /topic/FISHER.ADD_SIMILIAR_ITEM\n";
$stomp->subscribe(
  destination => "/topic/FISHER.ADD_SIMILIAR_ITEM",
  id => "perl_item_add"
);

$stomp->wait_for_frames(callback => sub {
  my($self, $frame) = @_;
  my $json_message = decode_json($frame->body());
  my $id = $json_message->{id};
  my $tenantId = $json_message->{tenantId};
  my $recommendation = to_json($json_message->{recommendation});
  my $destination_dir = "$target_directory/predictry/data/tenants/$tenantId/recommendations/similiar";

  make_path($destination_dir);
  open(my $fh, '>', "$destination_dir/$id.json");
  print $fh $recommendation . "\n";
  close $fh;

  return(0);
});

print "Disconnect from the queue\n";
$stomp->unsubscribe(id => "perl_item_add");
$stomp->disconnect();
