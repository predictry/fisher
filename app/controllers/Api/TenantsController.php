<?php

namespace App\Controllers\Api;

use ApiBaseController,
    Illuminate\Support\Facades\Input;

class TenantsController extends ApiBaseController
{

    public function index()
    {
        return "TenantsController:index";
    }

    public function store()
    {
        $inputs = Input::only(['name', 'api_key', 'api_secret', 'url']);

        $new_tenant = \Tenant::create($inputs);

        return \Illuminate\Support\Facades\Response::json(['tenant' => $new_tenant]);
    }

}
