<?php

namespace App\Controllers\Api;

use ApiBaseController,
    Illuminate\Support\Facades\Input,
    Illuminate\Support\Facades\Response,
    Item,
    Tenant;

class TenantsItemsController extends ApiBaseController
{

    public function __construct()
    {
        parent::__construct();
    }

    /**
     * Display a listing of the resource.
     *
     * @return Response
     */
    public function index($tenant)
    {
        $this->tenant = $tenant;
        return "{$this->tenant} api.v1.tenants.items.index";
    }

    /**
     * Display the specified resource.
     *
     * @param  string $tenant
     * @param  int $item_id
     * @return Response
     */
    public function show($tenant, $item_id)
    {
        $this->tenant = $tenant;
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  int  $item_id
     * @return Response
     */
    public function update($item_id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $item_id
     * @return Response
     */
    public function destroy($item_id)
    {
        //
    }

    public function store($tenant_id)
    {
        $inputs = Input::all();
        $item   = new Item($inputs);
        $tenant = Tenant::find($tenant_id);

        if (isset($tenant->id))
            $relation = $tenant->items()->save($item);

        return Response::json(['relation' => $relation]);
    }

}
