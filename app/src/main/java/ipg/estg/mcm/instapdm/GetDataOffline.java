package ipg.estg.mcm.instapdm;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetDataOffline extends AsyncTask<Void, Void, Void> {

    private DataReadRequest readRequest;
    private String TAG = "GetDataOffline";
    private GoogleApiClient mClient = null;
    private DataReadResult dataReadResult;
    private int total;
    private float expendedCalories;


    GetDataOffline(DataReadRequest dataReadRequest_, GoogleApiClient googleApiClient) {
        this.readRequest = dataReadRequest_;
        this.mClient = googleApiClient;
    }

    @Override
    protected Void doInBackground(Void... params) {

        dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);

        for (Bucket bucket : dataReadResult.getBuckets()) {

            List<DataSet> dataSetx =  bucket.getDataSets();


            for (DataSet dataSet : dataSetx) {

                if(dataSet.getDataType().getName().equals("com.google.step_count.delta"))
                {
                    if(dataSet.getDataPoints().size() > 0)
                    {
                        // total steps
                        total = total +  dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    }
                }
            }
            String bucketActivity = bucket.getActivity();
            if (bucketActivity.contains(FitnessActivities.WALKING) || bucketActivity.contains(FitnessActivities.RUNNING)) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {

                    if(dataSet.getDataType().getName().equals("com.google.calories.expended")) {

                        for (DataPoint dp : dataSet.getDataPoints()) {

                            if (dp.getEndTime(TimeUnit.MILLISECONDS) > dp.getStartTime(TimeUnit.MILLISECONDS)) {
                                for (Field field : dp.getDataType().getFields()) {
                                    // total calories burned
                                    expendedCalories = expendedCalories + dp.getValue(field).asFloat();
                                }

                            }

                        }

                    }

                }
            }


        }
        return null;

    }

    @Override
    protected void onPostExecute(Void dataReadResult) {
        super.onPostExecute(dataReadResult);

        Log.e("GoogleFit", "Steps total is " + total);

        Log.e("GoogleFit", "Total cal is " + expendedCalories);

    }
}
