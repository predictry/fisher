<?php

namespace App\Controllers\Api;

use ApiBaseController,
    App\Models\Action2,
    App\Models\Session,
    App\Models\Tenant2,
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
    public function getPageviews($tenant, $start_date = null, $end_date = null, $is_reco = false)
    {
        $result                = null;
        $view_action           = Action2::where('name', 'view')->first();
        $obj_tenant            = Tenant2::where('tenant', $tenant)->first();
        $regular_pageviews     = $recommended_pageviews = $overall_pageviews     = 0;

        if ($obj_tenant && $view_action) {

            $query = "SELECT SUM(regular_stats) as total FROM " .
                    "(SELECT * FROM actions_stats WHERE action_id = {$view_action->id} AND tenant_id = {$obj_tenant->id} AND " .
                    "(created BETWEEN '{$start_date}' AND '{$end_date}') AND is_reco = false ORDER BY created DESC" .
                    ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }

            $regular_pageviews = !is_null($result) ? $result->total : 0;

            if ($is_reco) {
                $query = "SELECT SUM(regular_stats) as total FROM " .
                        "(SELECT * FROM actions_stats WHERE action_id = {$view_action->id} AND tenant_id = {$obj_tenant->id} AND " .
                        "(created BETWEEN '{$start_date}' AND '{$end_date}') AND is_reco = true ORDER BY created DESC" .
                        ") as table1 LIMIT 1";

                $results = \DB::select(\DB::raw($query));
                if ($results && is_array($results)) {
                    $result = current($results);
                }

                $recommended_pageviews = !is_null($result) ? $result->total : 0;
            }

            $this->base_response['data'] = [
                'tenant'     => $tenant,
                'pageviews'  => [
                    'overall'     => $regular_pageviews + $recommended_pageviews,
                    'regular'     => $regular_pageviews,
                    'recommended' => $recommended_pageviews
                ],
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
     * @param string $tenant
     * @param string $start_date
     * @param string $end_date
     * @param string $filter_key
     */
    public function getUniqueVisitors($tenant, $start_date = null, $end_date = null, $filter_key = 'session')
    {
        $obj_tenant    = Tenant2::where('tenant', $tenant)->first();
        $count_session = Session::select(\DB::raw('COUNT(DISTINCT session) as count'))->where('tenant_id', $obj_tenant->id)
                ->whereBetween('log_created', array($start_date, $end_date))
                ->first();

        $this->base_response['data'] = [
            'tenant'     => $tenant,
            'count'      => $count_session->count,
            'start_date' => $start_date,
            'end_date'   => $end_date
        ];

        return Response::json($this->base_response);
    }

    /**
     * 
     * @param string $tenant
     * @param string $start_date
     * @param string $end_date
     */
    public function getSalesAmount($tenant, $start_date = null, $end_date = null, $is_reco = false)
    {
        $result                   = null;
        $buy_action               = Action2::where('name', 'buy')->first();
        $obj_tenant               = Tenant2::where('tenant', $tenant)->first();
        $regular_sum_of_sales     = $recommended_sum_of_sales = 0;
        if ($buy_action && $obj_tenant) {

            $query = "SELECT SUM(sub_total) as grand_total FROM ";
            $query .= "(";
            $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} AND is_reco = false ";

            if (!is_null($start_date) && !is_null($end_date))
                $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

            $query .= "ORDER BY created DESC";
            $query .= ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }

            $regular_sum_of_sales = (isset($result->grand_total)) ? $result->grand_total : 0;

            if ($is_reco) {
                $query = "SELECT SUM(sub_total) as grand_total FROM ";
                $query .= "(";
                $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} AND is_reco = true ";

                if (!is_null($start_date) && !is_null($end_date))
                    $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

                $query .= "ORDER BY created DESC";
                $query .= ") as table1 LIMIT 1";

                $results = \DB::select(\DB::raw($query));
                if ($results && is_array($results)) {
                    $result = current($results);
                }

                $recommended_sum_of_sales = (isset($result->grand_total)) ? $result->grand_total : 0;
            }

            $sums = [
                'overall'     => $regular_sum_of_sales + $recommended_sum_of_sales,
                'regular'     => $regular_sum_of_sales,
                'recommended' => $recommended_sum_of_sales
            ];

            if (!is_null($result)) {
                $this->base_response['data'] = [
                    'tenant'     => $tenant,
                    'sum'        => $sums,
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
    public function getItemPurchased($tenant, $start_date = null, $end_date = null, $is_reco = false)
    {
        $result                             = null;
        $buy_action                         = Action2::where('name', 'buy')->first();
        $obj_tenant                         = Tenant2::where('tenant', $tenant)->first();
        $regular_sum_of_items_purchased     = $recommended_sum_of_items_purchased = 0;
        if ($buy_action && $obj_tenant) {
            $query = "SELECT SUM(qty) as total_qty FROM ";
            $query .= "(";
            $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} AND is_reco = false ";

            if (!is_null($start_date) && !is_null($end_date))
                $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

            $query .= "ORDER BY created DESC";
            $query .= ") as table1 LIMIT 1";

            $results = \DB::select(\DB::raw($query));
            if ($results && is_array($results)) {
                $result = current($results);
            }

            $regular_sum_of_items_purchased = (isset($result->total_qty)) ? $result->total_qty : 0;

            if ($is_reco) {
                $query = "SELECT SUM(qty) as total_qty FROM ";
                $query .= "(";
                $query .= "SELECT * FROM sales_stats WHERE action_id = {$buy_action->id} AND tenant_id = {$obj_tenant->id} AND is_reco = true ";

                if (!is_null($start_date) && !is_null($end_date))
                    $query .= "AND (created BETWEEN '{$start_date}' AND '{$end_date}') ";

                $query .= "ORDER BY created DESC";
                $query .= ") as table1 LIMIT 1";

                $results = \DB::select(\DB::raw($query));
                if ($results && is_array($results)) {
                    $result = current($results);
                }

                $recommended_sum_of_items_purchased = (isset($result->total_qty)) ? $result->total_qty : 0;
            }

            $sums = [
                'overall'     => $regular_sum_of_items_purchased + $recommended_sum_of_items_purchased,
                'regular'     => $regular_sum_of_items_purchased,
                'recommended' => $recommended_sum_of_items_purchased
            ];

            if (!is_null($result)) {
                $this->base_response['data'] = [
                    'tenant'     => $tenant,
                    'sum'        => $sums,
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
        $buy_action = Action2::where('name', 'buy')->first();
        $obj_tenant = Tenant2::where('tenant', $tenant)->first();

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

    /**
     * GET Total of SKU's
     * 
     * @param string $tenant
     * @param string $start_date
     * @param string $end_date
     */
    public function getTotalSKUs($tenant)
    {
//        $items      = [];
//        $item       = false;
//        $item_model = new Item();
//        $client     = \DB::connection('neo4j')->getClient();
//        $strQuery   = 'MATCH (n:`Item`:`' . $tenant . '` {type:"' . 'catalog' . '"}) return n';
//        $query      = new Query($client, $strQuery);
//        $result     = $query->getResultSet();
    }

    /**
     * GET Top 10 Bought Items
     * 
     * @param string $tenant
     * @param int $limit
     * @param string $start_date
     * @param string $end_date
     */
    public function getTopBoughtItems($tenant, $limit, $start_date = null, $end_date = null)
    {

        $result     = null;
        $obj_tenant = Tenant2::where('tenant', $tenant)->first();

        $query = "SELECT item_id, COUNT(item_id) AS occurences, max(id) FROM sales_stats ";
        $query .= "WHERE tenant_id = {$obj_tenant->id} ";
        $query .= "GROUP BY item_id ";
        $query .= "HAVING (COUNT(item_id) > 1) ";
        $query .= "ORDER BY occurences DESC ";
        $query .= "LIMIT {$limit}";

        $results = \DB::select(\DB::raw($query));
        if ($results && is_array($results)) {
            $result = current($results);
        }
        
        echo '<pre>';
        print_r($result);
        echo "<br/>----<br/>";
        echo '</pre>';
        die;



        $top_10_bought_items = "";
    }

    //GET Top 10 Viewed Items (Check with Gui)
}
