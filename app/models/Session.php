<?php

/**
 * Author       : Rifki Yandhi
 * Date Created : Feb 12, 2015 4:31:16 PM
 * File         : Session.php
 * Copyright    : rifkiyandhi@gmail.com
 * Function     : 
 */

namespace App\Models;

class Session extends \Eloquent
{

    protected $table      = "sessions";
    protected $connection = 'mysql';
    public $timestamps    = false;

}

/* End of file Session.php */