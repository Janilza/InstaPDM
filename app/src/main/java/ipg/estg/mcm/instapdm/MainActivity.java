package ipg.estg.mcm.instapdm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private FirebaseAuth mAuth;
    private Toolbar mainToolbar;
    private GoogleApiClient mClient;
    private Context context = MainActivity.this;

    //Google APIClient
    private boolean authInProgress = false;
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final int REQUEST_OAUTH = 1;
    public static final String TAG ="My steps";


    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main);
        createFitnessClient();
    }

    public void onStart() {
        super.onStart();
        this.mAuth = FirebaseAuth.getInstance();
        this.mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(this.mainToolbar);
        ((ActionBar) Objects.requireNonNull(getSupportActionBar())).setTitle((CharSequence) "My Steps");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        mClient.connect();
    }

    private void createFitnessClient() {
        // Create the Google API Client
        Log.i(TAG, "Create Fitness client start");
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .useDefaultAccount().build();
        Log.i(TAG, "Create Fitness client end");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.actionLogout) {
            return false;
        }
        //new VerifyDataTask().execute();

        logOut();

        return true;
    }

    private void logOut() {
        this.mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();

        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));


        /*final DataSource ds = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();*/


        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi.readData(mClient, readRequest);
        pendingResult.setResultCallback(
                new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(@NonNull DataReadResult dataReadResult) {
                        if(dataReadResult.getBuckets().size() > 0){
                            for(Bucket bucket : dataReadResult.getBuckets()){
                                List<DataSet> dataSets = bucket.getDataSets();
                                for (DataSet dataSet : dataSets){
                                    processDataSet(dataSet);
                                }
                            }
                        }
                    }
                }
        );

        //new VerifyDataTask();
    }
    public void processDataSet(DataSet dataSet){
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getDefault());
        for(DataPoint dp : dataSet.getDataPoints()){

            //long dpStart = dp.getStartTime();
            //long dpEnd = dp.getEndTime();
            Log.i(TAG, "Data point");
            Log.i(TAG, "\tType: "+ dp.getDataType().getName());
            Log.i(TAG, "\tStart: "+ dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: "+ dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

            for(Field field: dp.getDataType().getFields()){
                String fieldName = field.getName();
                Log.i(TAG, "\tField: " + fieldName + "Value: "+ dp.getValue(field));

                Toast.makeText(context,"\tField"+ fieldName + "Value: "+ dp.getValue(field), Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        //
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );


            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e( "GoogleFit", "authInProgress" );
            Toast.makeText(context,"A conexão falhou!!!", Toast.LENGTH_LONG).show();
        }
    }

    private class VerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            long total = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            Log.i(TAG, "Total steps: " + total);

            return null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                if( !mClient.isConnecting() && !mClient.isConnected() ) {
                    mClient.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
                Log.i( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
            Log.i("GoogleFit", "requestCode NOT request_oauth");
        }
    }

}
