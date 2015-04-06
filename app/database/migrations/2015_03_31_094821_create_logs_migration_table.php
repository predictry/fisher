<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;

class CreateLogsMigrationTable extends Migration
{

    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('logs_migration', function(Blueprint $table) {
            $table->increments('id');
            $table->string('log_name', 100);
            $table->integer('batch');
            $table->integer('total_actions');
            $table->string('status', 20);
            $table->datetime('log_created_at');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::drop('logs_migration');
    }

}
