<?php

namespace App\Controllers\Api;

use ApiBaseController,
    App\Models\Item,
    Carbon\Carbon,
    Everyman\Neo4j\Cypher\Query,
    Illuminate\Support\Facades\Cache,
    Illuminate\Support\Facades\DB,
    Illuminate\Support\Facades\Response;

class TenantsItemsController extends ApiBaseController
{

    private $item_model = null;

    public function __construct()
    {
        parent::__construct();
        $this->item_model = new Item();
    }

    public function getPaginatedItems($tenant, $skip = false, $limit = false)
    {
        $client = DB::connection('neo4j')->getClient();
        $items  = [];

        if (!Cache::has("{$tenant}_total_items")) {
            $strQuery = 'MATCH (n:`Item`:`' . $tenant . '`) return count(n)';
            $query    = new Query($client, $strQuery);
            $results  = $query->getResultSet();
            $total    = $results->current()['t'];

            //CACHE the $total
            $expiresAt = Carbon::now()->addMinutes(1440); //1 day cache
            Cache::put("{$tenant}_total_items", $total, $expiresAt);
        }
        else
            $total = Cache::get("{$tenant}_total_items");

        $strQuery = 'MATCH (n:`Item`:`' . $tenant . '`) return n SKIP ' . $skip . ' LIMIT ' . $limit;
        $query2   = new Query($client, $strQuery);
        $results2 = $query2->getResultSet();

        foreach ($results2 as $row) {
            $attributes = $row['t']->getProperties();
            $item       = $this->item_model->newFromBuilder($attributes);
            array_push($items, $item);
        }

        $this->base_response['data'] = [
            'items' => $items,
            'total' => $total
        ];

        return Response::json($this->base_response);
    }

    public function getDetail($tenant, $item_id)
    {
        $item     = false;
        $client   = \DB::connection('neo4j')->getClient();
        $strQuery = 'MATCH (n:`Item`:`' . $tenant . '` {id:"' . $item_id . '"}) return n';
        $query    = new Query($client, $strQuery);
        $result   = $query->getResultSet();

        foreach ($result as $row) {
            $attributes = $row['t']->getProperties();
            $item       = $this->item_model->newFromBuilder($attributes);
        }

        $this->base_response['data'] = [
            'item_id' => $item_id,
            'item'    => $item
        ];

        return Response::json($this->base_response);
    }

}
