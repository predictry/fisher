<?php

namespace App\Fisher\Repositories;

use App\Models\ActionStat,
    App\Models\SaleStat,
    App\Models\Session,
    App\Models\Tenant2,
    Illuminate\Support\Facades\Cache;

/**
 * Author       : Rifki Yandhi
 * Date Created : Mar 31, 2015 10:34:21 AM
 * File         : AnalyticsTenantRepository.php
 * Copyright    : rifkiyandhi@gmail.com
 * Function     : 
 */
class TenantRepository
{

    public static function getCacheTenantIDs()
    {
        $tenants = [];
        if (Cache::has('tenants')) {
            $tenants = Cache::get('tenants');
            return $tenants;
        }

        $tenants = Tenant2::all();
        if ($tenants)
            $tenants = $tenants->toArray();

        return $tenants;
    }

    public static function saveActionsStatsCollection($tenant_stats)
    {
        foreach ($tenant_stats as $tenant_name => $actions_stat) {
            $data = [];
            foreach ($actions_stat as $action_name => $action_stat) {
                array_push($data, $action_stat);
            }

            if (count($data) > 0) {
                ActionStat::insert($data);
            }
        }
    }

    public static function saveSalesStatsCollection($tenant_stats)
    {
        foreach ($tenant_stats as $tenant_name => $sales_stat) {
            $data = [];

            foreach ($sales_stat as $sale_stat) {
                array_push($data, $sale_stat);
            }

            if (count($data) > 0) {
                SaleStat::insert($data);
            }
        }
    }

    public static function saveSessionCollection($tenant_stats)
    {
        foreach ($tenant_stats as $tenant_name => $sales_stat) {
            $data = [];

            foreach ($sales_stat as $sale_stat) {
                array_push($data, $sale_stat);

                if (count($data) > 1000) {
                    Session::insert($data);
                    $data = [];
                }
            }
        }
    }

}

/* End of file AnalyticsTenantRepository.php */