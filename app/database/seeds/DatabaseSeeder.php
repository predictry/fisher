<?php

use App\Models\Action2,
    App\Models\Tenant2,
    Illuminate\Database\Seeder,
    Illuminate\Support\Facades\DB;

class DatabaseSeeder extends Seeder
{

    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        Eloquent::unguard();

        $this->call('ActionSeeder');
        $this->call('TenantSeeder');
    }

}

class ActionSeeder extends Seeder
{

    public function run()
    {
        DB::connection('mysql')->table('actions')->delete();
        $actions_name = ['view', 'add_to_cart', 'buy', 'started_checkout', 'started_payment', 'check_delete_item', 'delete_item', 'custom'];

        foreach ($actions_name as $action_name) {
            Action2::create(['name' => $action_name]);
        }
    }

}

class TenantSeeder extends Seeder
{

    public function run()
    {
        DB::connection('mysql')->table('tenants')->delete();
        $tenants = ['FAMILYNARA2014', 'bukalapak', 'grouponid', 'pricearea2', 'SUPERBUYMY', 'THECRESCENTMY', 'SOUQMY'];

        foreach ($tenants as $tenant) {
            Tenant2::create(['tenant' => $tenant, 'api_key' => \Str::random(32)]);
        }
    }

}
