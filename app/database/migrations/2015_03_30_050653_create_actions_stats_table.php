<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;

class CreateActionsStatsTable extends Migration
{

    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('actions_stats', function(Blueprint $table) {
            $table->increments('id');
            $table->integer('action_id')->unsigned();
            $table->integer('tenant_id')->unsigned();
            $table->integer('regular_stats');
            $table->integer('recommendation_stats');
            $table->datetime('created');
            $table->boolean('is_reco')->default(false);

            $table->foreign('action_id')->references('id')->on('actions')->onUpdate('cascade');
            $table->foreign('tenant_id')->references('id')->on('tenants')->onUpdate('cascade');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::drop('actions_stats');
    }

}
