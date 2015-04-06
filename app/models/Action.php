<?php

/**
 * Author       : Rifki Yandhi
 * Date Created : Feb 12, 2015 4:28:21 PM
 * File         : Action.php
 * Copyright    : rifkiyandhi@gmail.com
 * Function     : Neo4j Action
 */
class Action extends NeoEloquent
{

    protected $label    = "Action";
    protected $fillable = ['name'];

    public function actionInstances()
    {
        return $this->hasMany('ActionInstance', 'TRACKS');
    }

}

/* End of file Action.php */