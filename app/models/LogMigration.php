<?php

namespace App\Models;

class LogMigration extends \Eloquent
{

    protected $table      = "logs_migration";
    protected $connection = 'mysql';
    public $timestamps    = false;

}
