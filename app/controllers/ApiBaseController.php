<?php

use Illuminate\Support\Facades\Response;

class ApiBaseController extends Controller
{

    public $base_response = [];
    public $tenant        = null;

    public function __construct()
    {
        $this->base_response = [
            'error'          => false,
            'data'           => [],
            'status'         => 200,
            'message'        => "",
            "client_message" => ""
        ];

        $this->tenant = \Input::get("tenant");
    }

    public function missingMethod($parameters = array())
    {
        return Response::json(['message' => 'URI not found'], 404);
    }

}
