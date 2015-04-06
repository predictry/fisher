<?php

namespace App\Controllers\Api;

use ApiBaseController,
    Illuminate\Support\Facades\Response;

class TenantsStatsSummaryController extends ApiBaseController
{

    /**
     * 
     * 
     * @param string $tenant
     * @param string $start_date
     * @param string $end_date
     * 
     * return json
     */
    public function getPageviews($tenant, $start_date = null, $end_date = null)
    {
        $result      = null;
        $view_action = \App\Models\Action2::where('name', 'view')->first();
        $obj_tenant  = \App\Models\Tenant2::where('tenant', $tenant)->first();

        if ($obj_tenant && $view_action) {

            $query = "SELECT SUM(regular_stats) as total FROM " .
                    "(SELECT * FROM actions_stats WHERE action_id = {$view_action->id} AND tenant_id = {$obj_tenant->id} AND " .
                    "(created BETWEEN '{$start_date}' AND '{$end_date}') AND is_reco = false ORDER BY created DESC" .
                    ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }
            $this->base_response['data'] = [
                'tenant'     => $tenant,
                'pageviews'  => !is_null($result) ? $result->total : 0,
                'start_date' => $start_date,
                'end_date'   => $end_date
            ];
        }
        else {
            $this->base_response['error']   = true;
            $this->base_response['message'] = "Tenant or action not found";
        }

        return Response::json($this->base_response);
    }

    /**
     * 
     * @param string $tenant_id
     * @param string $start_date
     * @param string $end_date
     * @param string $filter_key
     */
    public function getUniqueVisitors($tenant_id, $start_date = null, $end_date = null, $filter_key = 'session')
    {
        
    }

    /**
     * 
     * @param string $tenant
     * @param string $start_date
     * @param string $end_date
     */
    public function getSalesAmount($tenant, $start_date = null, $end_date = null)
    {
        $result     = null;
        $buy_action = \App\Models\Action2::where('name', 'buy')->first();
        $obj_tenant = \App\Models\Tenant2::where('tenant', $tenant)->first();

        if ($buy_action && $obj_tenant) {

            $query = "SELECT SUM(sub_total) as grand_total FROM ";
            $query .= "(";
            $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} ";

            if (!is_null($start_date) && !is_null($end_date))
                $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

            $query .= "ORDER BY created DESC";
            $query .= ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }

            if (!is_null($result)) {
                $this->base_response['data'] = [
                    'tenant'     => $tenant,
                    'sum'        => (isset($result->grand_total)) ? $result->grand_total : 0,
                    'start_date' => $start_date,
                    'end_date'   => $end_date
                ];
            }
        }
        else {
            $this->base_response['error']   = true;
            $this->base_response['message'] = "Tenant or action not found";
        }

        return Response::json($this->base_response);
    }

    /**
     * 
     * @param string $tenant
     * @param string $start_date
     * @param string $end_date
     */
    public function getItemPurchased($tenant, $start_date = null, $end_date = null)
    {
        $result     = null;
        $buy_action = \App\Models\Action2::where('name', 'buy')->first();
        $obj_tenant = \App\Models\Tenant2::where('tenant', $tenant)->first();

        if ($buy_action && $obj_tenant) {
            $query = "SELECT SUM(qty) as total_qty FROM ";
            $query .= "(";
            $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} ";

            if (!is_null($start_date) && !is_null($end_date))
                $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

            $query .= "ORDER BY created DESC";
            $query .= ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }

            if (!is_null($result)) {
                $this->base_response['data'] = [
                    'tenant'     => $tenant,
                    'sum'        => (isset($result->total_qty)) ? $result->total_qty : 0,
                    'start_date' => $start_date,
                    'end_date'   => $end_date
                ];
            }
        }
        else {
            $this->base_response['error']   = true;
            $this->base_response['message'] = "Tenant or action not found";
        }

        return Response::json($this->base_response);
    }

    /**
     * 
     * @param integer $tenant_id
     * @param string $start_date
     * @param string $end_date
     */
    public function getAverageSalesAmount($tenant_id, $start_date = null, $end_date = null)
    {
        
    }

    /**
     * 
     * @param integer $tenant_id
     * @param string $start_date
     * @param string $end_date
     */
    public function getAverageItemsPurchased($tenant_id, $start_date = null, $end_date = null)
    {
        
    }

    /**
     * 
     * @param integer $tenant
     * @param string $start_date
     * @param string $end_date
     */
    public function getOrders($tenant, $start_date = null, $end_date = null)
    {
        $result     = null;
        $buy_action = \App\Models\Action2::where('name', 'buy')->first();
        $obj_tenant = \App\Models\Tenant2::where('tenant', $tenant)->first();

        if ($buy_action && $obj_tenant) {
            $query = "SELECT COUNT(*) as count FROM ";
            $query .= "(";
            $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} ";

            if (!is_null($start_date) && !is_null($end_date))
                $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

            $query .= "GROUP BY group_id ";
            $query .= "ORDER BY created DESC";
            $query .= ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }

            if (!is_null($result)) {
                $this->base_response['data'] = [
                    'tenant'     => $tenant,
                    'count'      => (isset($result->count)) ? $result->count : 0,
                    'start_date' => $start_date,
                    'end_date'   => $end_date
                ];
            }
        }
        else {
            $this->base_response['error']   = true;
            $this->base_response['message'] = "Tenant or action not found";
        }

        return Response::json($this->base_response);
    }

}
