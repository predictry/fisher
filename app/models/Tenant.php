<?php

/**
 * Author       : Rifki Yandhi
 * Date Created : Feb 12, 2015 4:33:22 PM
 * File         : Tenant.php
 * Copyright    : rifkiyandhi@gmail.com
 * Function     : 
 */
class Tenant extends NeoEloquent
{

    protected $label    = "Tenant";
    protected $fillable = ["name", "url", "api_key", "api_secret"];

    public function items()
    {
        return $this->hasMany('Item', 'OWNS');
    }

    public function actions()
    {
        return $this->hasMany('Action', 'OWNS');
    }
    
}

/* End of file Tenant.php */