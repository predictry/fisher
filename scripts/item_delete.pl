#! /usr/bin/perl
use strict;
use warnings;
use Net::STOMP::Client;
use File::Find::Rule;
use JSON;

my $queue_host = "127.0.0.1";
my $queue_port = 61613;
my $queue_user = "admin";
my $queue_password = "admin";
my $target_directory = "/volumes/neo/items";

sub read_file {
    my ($filename) = @_;
 
    open my $in, '<:encoding(UTF-8)', $filename or die "Could not open '$filename' for reading $!";
    local $/ = undef;
    my $all = <$in>;
    close $in;
 
    return $all;
}
 
sub write_file {
    my ($filename, $content) = @_;
 
    open my $out, '>:encoding(UTF-8)', $filename or die "Could not open '$filename' for writing $!";;
    print $out $content;
    close $out;
 
    return;
}

print "Connecting to host [$queue_host] port [$queue_port] as user [$queue_user]\n";
my $stomp = Net::STOMP::Client->new(host => $queue_host, port => $queue_port);
$stomp->connect(login => $queue_user, passcode => $queue_password);

print "Subscribing to topic /topic/FISHER.DELETE_ITEM\n";
$stomp->subscribe(
  destination => "/topic/FISHER.DELETE_ITEM",
  id => "perl_item_delete"
);

$stomp->wait_for_frames(callback => sub {
  my($self, $frame) = @_;
  my $json_message = decode_json($frame->body());
  my $item_id = $json_message->{id};
  my $tenant_id = $json_message->{tenantId};
  my $destination_dir = "$target_directory/predictry/data/tenants/$tenant_id/recommendations/similiar";

  unlink "$destination_dir/$item_id.json";

  for my $file (File::Find::Rule->file()->in($destination_dir)) {
    my $data = read_file($file);
    $data =~ s/"$item_id"/"/g;
    $data =~ s/,\]/\]/g;
    $data =~ s/\[,/\[/g;
    $data =~ s/,,/,/g;
    write_file($file, $data);
  }

  system("aws s3 sync s3://predictry/data/tenants/$tenant_id/recommendations/similiar $destination_dir");

  return(0);
});

print "Disconnect from the queue\n";
$stomp->unsubscribe(id => "perl_item_delete");
$stomp->disconnect();
