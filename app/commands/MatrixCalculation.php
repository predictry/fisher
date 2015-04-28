<?php

use App\Fisher\Helper,
    App\Models\Action2,
    App\Models\LogMigration,
    App\Models\Tenant2,
    Carbon\Carbon,
    Illuminate\Support\Facades\Cache,
    Illuminate\Support\Facades\DB,
    Illuminate\Support\Facades\File,
    Symfony\Component\Console\Input\InputArgument,
    Symfony\Component\Console\Input\InputOption;

class MatrixCalculation extends LogsBaseCommand
{

    protected $batch                           = 0;
    protected $counter_total_actions_each_file = 0;
    protected $processed_logs                  = [];
    protected $processing_logs                 = [];
    protected $processing_detail_logs          = [];
    protected $tenants                         = [];
    protected $actions_stats_header            = [
        "id", "action_id", "tenant_id", "regular_stats", "recommendation_stats", "created"
    ];
    protected $sales_stats_header              = [
        "id", "tenant_id", "action_id", "user_id", "session_id", "item_id", "group_id",
        "qty", "sub_total", "is_reco", "created"
    ];

    /**
     * The console command name.
     *
     * @var string
     */
    protected $name = 'logs:matrix-calculation';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Calculate matrix.';

    /**
     * Create a new command instance.
     *
     * @return void
     */
    public function __construct()
    {
        ini_set('memory_limit', '512M');
        parent::__construct();
        $last_batch  = LogMigration::max("batch");
        $this->batch = $last_batch+=1;

        $processed_logs = LogMigration::all();
        if ($processed_logs) {
            $this->processed_logs = $processed_logs->lists("log_name", "id");
            Cache::add('processed_logs', $this->processed_logs, 1440);
        }

        $tenants       = Tenant2::all();
        $this->tenants = $tenants->lists("tenant", "id");
    }

    /**
     * Execute the console command.
     *
     * @return mixed
     */
    public function fire()
    {
        try
        {
            $this->bucket = $this->argument('bucket');

            $this->log_prefix = $this->option('prefix');
            $this->log_prefix = isset($this->log_prefix) ? $this->log_prefix : false;

            $this->log_name_option = $this->option("log_name");

            $this->limit = $this->option('limit');
            $this->limit = (isset($this->limit)) ? $this->limit : 1000;

            $start_date = $this->option('start_date');
            $end_date   = $this->option('end_date');

            $this->printTitle();
            $bucket_objects = $this->getBucketObjects($this->bucket, $this->log_prefix);

            $dates = Helper::getListDateRange($start_date, $end_date);

            foreach ($bucket_objects as $obj) {
                $start_time         = new Carbon();
                $str_date           = $str_date_formatted = null;
                $file_name          = $this->getFilename($obj, $this->log_prefix);
                if ($file_name !== "") {
                    $file_name_without_ext = str_replace('.json', '', $file_name);

                    $arr_filename = explode('.', $file_name);
                    if (is_array($arr_filename) && !empty($arr_filename[0])) {
                        $str_date = $arr_filename[1];
                    }
                    else
                        continue;

                    $date                    = date_create_from_format('Y-m-d-H', $str_date);
                    $full_str_date_formatted = $date->format('Y-m-d H:i:s');
                    $str_date_formatted      = $date->format('Y-m-d');

                    if (!in_array($str_date_formatted, $dates)) { //current file is not within date range//then continue
                        $this->error("{$file_name} is not within date range");
                        continue;
                    }

                    $this->counter_total_actions_each_file = 0;

                    $this->downloadObject($this->bucket, $obj['Key'], storage_path("logs/json_tmp/{$file_name}"));

                    $tenant_actions_stats = $this->assignTenantActionIndexes($full_str_date_formatted);
                    $tenant_sales_stats   = $this->assignTenantSaleIndexes();

                    $arr_csv_formatted = $this->parseJsonToArrayCSVFormatted(storage_path("logs/json_tmp/{$file_name}"), $tenant_actions_stats, $tenant_sales_stats);
                    $this->saveArrCSVFormattedIntoFile($arr_csv_formatted, $file_name_without_ext);
                    $this->importCSV($file_name_without_ext);

                    \File::delete(storage_path("logs/json_tmp/{$file_name}")); //delete zip log

                    array_push($this->processing_logs, $file_name);
                    array_push($this->processing_detail_logs, [
                        'log_name'       => $file_name,
                        'log_created_at' => $full_str_date_formatted,
                        'created_at'     => $start_time,
                        'updated_at'     => new Carbon(),
                        'total_actions'  => $this->counter_total_actions_each_file,
                        'batch'          => $this->batch
                    ]);
                }
                else
                    continue;

                $this->info($file_name);

                if (count($this->processing_detail_logs) > 50) { //save after 50
                    //this should be an event
                    LogMigration::insert($this->processing_detail_logs);
                    $this->processing_detail_logs = [];
                }
            }

            if (count($this->processing_detail_logs) > 0) {
                //this should be an event
                LogMigration::insert($this->processing_detail_logs);
                $this->processing_detail_logs = [];
            }
        }
        catch (Exception $ex)
        {
            \Log::error($ex->getMessage());
            if (count($this->processing_detail_logs) > 0) {
                //this should be an event
                LogMigration::insert($this->processing_detail_logs);
                $this->processing_detail_logs = [];
            }
        }
    }

    /**
     * Get the console command arguments.
     *
     * @return array
     */
    protected function getArguments()
    {
        return array(
            array('bucket', InputArgument::REQUIRED, 'The bucket name'),
        );
    }

    /**
     * Get the console command options.
     *
     * @return array
     */
    protected function getOptions()
    {
        return array(
            array('prefix', null, InputOption::VALUE_OPTIONAL, 'The prefix name', null),
            array('log_name', null, InputOption::VALUE_OPTIONAL, 'The specific log name gz', null),
            array('limit', null, InputOption::VALUE_OPTIONAL, 'Limit number of logs fetch', null),
            array('start_date', null, InputOption::VALUE_REQUIRED, 'The start date of the log', null),
            array('end_date', null, InputOption::VALUE_REQUIRED, 'The end date of the log', null)
        );
    }

    private function assignTenantActionIndexes($str_date_formated)
    {
        $tenant_actions_stats = [];
        $cache_actions        = null;
        $cache_tenants        = null;

        if (Cache::has('actions')) {
            $cache_actions = Cache::get('actions');
        }

        if (Cache::has('tenants')) {
            $cache_tenants = Cache::get('tenants');
        }

        if (is_null($cache_tenants) || count($cache_tenants) <= 0) {
            $tenants       = Tenant2::all();
            $cache_tenants = $tenants->toArray();
            Cache::add('tenants', $cache_tenants, 1440);
        }

        if (is_null($cache_actions) || count($cache_actions) <= 0) {
            $actions       = Action2::all();
            $cache_actions = $actions->toArray();
            Cache::add('actions', $cache_actions, 1440);
        }

        //assign tenants
        foreach ($cache_tenants as $tenant) {
            $tenant_actions_stats[$tenant['tenant']] = [];

            //assign actions
            foreach ($cache_actions as $action) {
                $tenant_actions_stats[$tenant['tenant']][$action['name']] = [
                    'action_id'            => $action['id'],
                    'tenant_id'            => $tenant['id'],
                    'regular_stats'        => 0, //regular counter
                    'recommendation_stats' => 0, //recommendation counter
                    'created'              => $str_date_formated
                ];
            }
        }


        return $tenant_actions_stats;
    }

    private function assignTenantSaleIndexes()
    {
        $tenant_sales_stats = [];

        $cache_actions = null;
        $cache_tenants = null;

        if (Cache::has('actions')) {
            $cache_actions = Cache::get('actions');
        }

        if (is_null($cache_tenants)) {
            $tenants       = Tenant2::all();
            $cache_tenants = $tenants->toArray();
            Cache::add('tenants', $cache_tenants, 1440);
        }

        //assign tenants
        foreach ($cache_tenants as $tenant) {
            $tenant_sales_stats[$tenant['tenant']] = [];
        }

        return $tenant_sales_stats;
    }

    private function getBuyRows($tenant, $items, $user_id, $session_id, $str_date_formated, $unique_order_id)
    {
        $rows = [];
        try
        {
            foreach ($items as $item) {

                $tenant_id = array_search($tenant, $this->tenants);
                if (!$tenant_id)
                    continue;

                if (isset($item['item_id'])) {

                    $is_rec   = isset($item['rec']) ? $item['rec'] : false;
                    $buy_stat = [
                        'tenant_id'  => $tenant_id,
                        'action_id'  => 3, //buy
                        'user_id'    => $user_id,
                        'session_id' => $session_id,
                        'item_id'    => $item['item_id'],
                        'group_id'   => $unique_order_id,
                        'qty'        => isset($item['qty']) ? $item['qty'] : 1,
                        'sub_total'  => isset($item['sub_total']) ? $item['sub_total'] : 0,
                        'is_reco'    => ($is_rec) ? true : false,
                        'created'    => $str_date_formated
                    ];
                    array_push($rows, $buy_stat);
                }
            }
            return $rows;
        }
        catch (Exception $ex)
        {
            \Log::info($ex->getMessage());
        }

        return $rows;
    }

    private function parseJsonToArrayCSVFormatted($log_json_path, $tenant_actions_stats, $tenant_sales_stats)
    {
        $file                            = \File::get($log_json_path);
        $arr_json                        = json_decode($file, true);
        $counter                         = $counter_total_actions_each_file = 0;

        if (!is_array($arr_json) || count($arr_json) <= 0)
            return false;

        foreach ($arr_json as $row) {
            $tenant = trim(array_get($row, 'deserialized_query.tenant_id', ''));

            if ($tenant !== "") {

                //access_log
                $date = array_get($row, 'access_log.date', '');
                $time = array_get($row, 'access_log.time', '');

                //deserialized_query
                $action          = trim(array_get($row, 'deserialized_query.action.name', ''));
                $user            = array_get($row, "deserialized_query.user");
                $items           = array_get($row, "deserialized_query.items", []);
                $session_id      = array_get($row, 'deserialized_query.session_id', '');
                $session_user_id = array_get($row, 'deserialized_query.user_id', '');
                $user_id         = (isset($user) && count($user) > 0 && isset($user['id'])) ? $user['id'] : $session_user_id;

                if ($action !== "") {

                    if (isset($tenant_actions_stats[$tenant]) && isset($tenant_actions_stats[$tenant][$action])) {
                        $is_rec = array_get($row, 'deserialized_query.action.rec', false);

                        if ($is_rec)
                            $tenant_actions_stats[$tenant][$action]['recommendation_stats'] +=1;
                        else
                            $tenant_actions_stats[$tenant][$action]['regular_stats'] +=1;

                        //Sales
                        if ($action === "buy") {
                            $unique_id = \Str::random(10);
                            $buy_rows  = $this->getBuyRows($tenant, $items, $user_id, $session_id, $date . ' ' . $time, $unique_id);
                            if (count($buy_rows) > 0)
                                foreach ($buy_rows as $buy_row) {
                                    array_push($tenant_sales_stats[$tenant], $buy_row);
                                }
                        }

                        $this->counter_total_actions_each_file+=1;
                    }
                }
            }

            $counter+=1;
        }

        $csv_actions_stats = [];
        foreach ($tenant_actions_stats as $tenant => $action_stat) {
            foreach ($action_stat as $action_name => $data) {
                array_push($csv_actions_stats, array_values($data));
            }
        }

        $csv_sales_stats = [];
        foreach ($tenant_sales_stats as $tenant => $tenant_sale_stat) {
            foreach ($tenant_sale_stat as $action_name => $data) {
                array_push($csv_sales_stats, array_values($data));
            }
        }

        return [
            'csv_actions_stats' => $csv_actions_stats,
            'csv_sales_stats'   => $csv_sales_stats
        ];
    }

    private function saveArrCSVFormattedIntoFile($arr_csv_formatted, $file_name_without_ext)
    {
        if ($arr_csv_formatted && is_array($arr_csv_formatted)) {
            $arr_csv_actions_stats = $arr_csv_formatted['csv_actions_stats'];
            $arr_csv_sales_stats   = $arr_csv_formatted['csv_sales_stats'];

            if (count($arr_csv_actions_stats) > 0 && !File::exists(storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv"))) {
                $str_header    = implode(",", $this->actions_stats_header) . "\n";
                $bytes_written = File::put(storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv"), $str_header);

                foreach ($arr_csv_actions_stats as $action_stat) {
                    $row = "NULL,";
                    $row .= implode(",", $action_stat) . "\n";
                    File::append(storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv"), $row);
                }
            }

            if (count($arr_csv_sales_stats) > 0 && !File::exists(storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv"))) {
                $str_header    = implode(",", $this->sales_stats_header) . "\n";
                $bytes_written = File::put(storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv"), $str_header);

                foreach ($arr_csv_sales_stats as $sale_stat) {
                    $row = "NULL,";
                    $row .= implode(",", $sale_stat) . "\n";
                    File::append(storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv"), $row);
                }
            }
        }
    }

    private function importCSV($file_name_without_ext)
    {
        $pdo = DB::connection()->getPdo();
        DB::statement('SET FOREIGN_KEY_CHECKS=0;');

        if (File::exists(storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv"))) {
            $this->info("LOAD DATA LOCAL INFILE '" . storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv") . "' INTO TABLE actions_stats FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES");
            $pdo->exec("LOAD DATA LOCAL INFILE '" . storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv") . "' INTO TABLE actions_stats FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES");
        }

        if (File::exists(storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv"))) {
            $this->info("LOAD DATA LOCAL INFILE '" . storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv") . "' INTO TABLE sales_stats FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES");
            $pdo->exec("LOAD DATA LOCAL INFILE '" . storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv") . "' INTO TABLE sales_stats FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES");
        }

        DB::statement('SET FOREIGN_KEY_CHECKS=1;');
        \File::delete(storage_path("logs/csv_tmp/actions-stats-{$file_name_without_ext}.csv")); //delete zip log
        \File::delete(storage_path("logs/csv_tmp/sales-stats-{$file_name_without_ext}.csv")); //delete zip log
    }

    protected function printTitle()
    {
        $str = "Bucket name: {$this->bucket}; Prefix name = " . (isset($this->log_prefix) && ($this->log_prefix) ? $this->log_prefix : '-') . "; ";
        $str .= "Log name: " . (isset($this->log_name_option) && ($this->log_name_option) ? $this->log_name_option : '-') . "; ";
        $str .= "Start date: " . $this->option('start_date') . "; ";
        $str .= "End date: " . $this->option('end_date');
        $this->comment($str);
    }

}
