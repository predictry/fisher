<?php

/**
 * Author       : Rifki Yandhi
 * Date Created : Feb 12, 2015 3:35:23 PM
 * File         : Item.php
 * Copyright    : rifkiyandhi@gmail.com
 * Function     : 
 */
class Item extends NeoEloquent
{

    protected $label    = 'Item';
    protected $fillable = ['identifier', 'name'];

}

/* End of file Item.php */