<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;

class CreateSalesStats extends Migration
{

    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('sales_stats', function(Blueprint $table) {
            $table->increments('id');
            $table->integer('tenant_id')->unsigned();
            $table->integer('action_id')->unsigned();
            $table->string('user_id', 60);
            $table->string('session_id', 60);
            $table->string('item_id', 60);
            $table->string('group_id', 10);
            $table->integer('qty');
            $table->double('sub_total');
            $table->boolean('is_reco')->default(false);
            $table->datetime('created');

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
        Schema::drop('sales_stats');
    }

}
