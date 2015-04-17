<?php

/*
  |--------------------------------------------------------------------------
  | Application Routes
  |--------------------------------------------------------------------------
  |
  | Here is where you can register all of the routes for an application.
  | It's a breeze. Simply tell Laravel the URIs it should respond to
  | and give it the Closure to execute when that URI is requested.
  |
 */

Route::pattern('tenants', '[A-Za-z0-9]+');
Route::pattern('start_date', '^([0-9]{4})-([0-9]{2})-([0-9]{2})$');
Route::pattern('end_date', '^([0-9]{4})-([0-9]{2})-([0-9]{2})$');

Route::get('/', function() {
    return View::make('hello');
});

Route::group(['prefix' => 'api/v1', 'namespace' => 'App\Controllers\Api'], function() {

    Route::resource('tenants', 'TenantsController', ['only' => ['store', 'index']]);
    Route::resource('items', 'ItemsController', ['only' => ['store']]);
    Route::resource('actions', 'ActionsController', ['only' => []]);
    Route::resource('actions_instances', 'ActionInstancesController', ['only' => []]);
    Route::resource('sessions', 'SessionsController', ['only' => []]);
    Route::resource('users', 'UsersController', ['only' => []]);

    Route::resource('tenants.actions', 'TenantsActionsController', ['only' => []]);

    ## Reference: http://docs.predictryanalytics.apiary.io/#reference/items
    Route::controller('tenants/{tenants}/items', 'TenantsItemsController');

    ## Reference: http://docs.predictryanalytics.apiary.io/#reference/sessions
    Route::resource('tenants.sessions', 'TenantsSessionsController', ['only' => ['index', 'show']]);
    Route::get('tenants/{tenants}/sessions/range/{$start_date}/{$end_date}', ['as' => 'tenants.sessions.range', 'uses' => 'TenantsSessionsController@getByRange']);
    ## Reference: http://docs.predictryanalytics.apiary.io/#reference/action-instances
    Route::resource('tenants.action-instances', 'TenantsActionInstancesController', ['only' => ['index', 'show']]);
    Route::get('tenants/{tenants}/action-instances/range/{$start_date}/{$end_date}', ['as' => 'tenants.action-instances.range', 'uses' => 'TenantsActionInstancesController@getByRange']);
    ## Reference: http://docs.predictryanalytics.apiary.io/#reference/actions
    Route::resource('tenants.actions.action-instances', 'TenantsActionsActionInstancesController', ['only' => ['index', 'show', 'update']]);
    Route::get('tenants/{tenants}/actions/{actions}/action-instances/range/{$start_date}/{$end_date}', ['as' => 'tenants.actions.action-instances.range', 'uses' => 'TenantsActionsActionInstancesController@getByRange']);
    ## Reference: http://docs.predictryanalytics.apiary.io/#reference/users
    Route::resource('tenants.users', 'TenantsUsersController', ['only' => ['index', 'show']]);

//    Route::get('tenants/{tenants}/stats-summary/pageviews/{start_date?}/{end_date?}', ['as' => 'tenants.stats-summary.pageviews', 'uses' => 'TenantsStatsSummaryController@getPageviews']);
    Route::controller('tenants/{tenants}/stats-summary', 'TenantsStatsSummaryController');
});

