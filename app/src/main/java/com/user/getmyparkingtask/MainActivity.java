package com.user.getmyparkingtask;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.user.getmyparkingtask.room.ImageDatabse;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, IPickResult {

    public static final String TAG = "TEST";
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private LinearLayout linear_layout;
    private List<ImagePojo> imagePojoList = new ArrayList<>();
    private int recycerViewWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView2);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        Spinner spinner = findViewById(R.id.spinner);
        linear_layout = findViewById(R.id.linear_layout);

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.image_sizes_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup()).show(MainActivity.this);
            }
        });


        final ViewTreeObserver observer= recyclerView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.d("Log", "width: " + recyclerView.getWidth());
                        recycerViewWidth = recyclerView.getWidth();
                    }
                });

        new GetAllImages().execute();

    }

    void initRecyclerView(int width, int height,int coloumnCount){
        recyclerView.setLayoutManager(new GridLayoutManager(this, coloumnCount));
        imageAdapter  = new ImageAdapter(this,imagePojoList,width,height);
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int spanCount = 1;
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        String[] dimensions = selectedItem.split("\\*");
        int width = Integer.parseInt(dimensions[0]);
        int coloumnCount =  (int)Math.floor(recycerViewWidth/width);
        Log.i(TAG, "Coloumn count: " + coloumnCount);
        if (coloumnCount==0)
            coloumnCount=1;
        /*if (coloumnCount>15)
            spanCount = 3;
        else if (coloumnCount>1)
            spanCount = 2;
        else
            spanCount = 1;
*/
        initRecyclerView(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]),coloumnCount);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            uploadImage(r.getUri());
        } else {
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void uploadImage(Uri imageUri) {
        String requestId = MediaManager.get().upload(imageUri).unsigned("ml_default").option("connect_timeout", 10000)
                .option("read_timeout", 10000).callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // your code here
                        Log.i(TAG, "onStart: " + requestId);
                        floatingActionButton.setVisibility(View.GONE);
                        linear_layout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // example code starts here
                        Double progress = (double) bytes / totalBytes;
                        Log.i(TAG, "onProgress: image uploaded" + progress);
                        // post progress to app UI (e.g. progress bar, notification)
                        // example code ends here
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // your code here
                        Log.i(TAG, "onSuccess: image uploaded: " + requestId);
                        Log.i(TAG, "onSuccess: URL " +resultData.get("url"));
                        Log.i(TAG, "onSuccess: Secure URL " +resultData.get("secure_url"));
                        Log.i(TAG, "onSuccess: Map Data: " + resultData.entrySet());
                        floatingActionButton.setVisibility(View.VISIBLE);
                        linear_layout.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Image upload Successful", Toast.LENGTH_SHORT).show();
                        String url = resultData.get("url").toString();
                        new AddImageLocally(new ImagePojo(url)).execute();

                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.i(TAG, "onError: " + error.getDescription());
                        // your code here
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // your code here
                    }
                })
                .dispatch();
    }

    class AddImageLocally extends AsyncTask<Void, Void, Void> {
        ImagePojo imagePojo;

        public AddImageLocally(ImagePojo searchItem) {
            this.imagePojo = searchItem;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ImageDatabse.getInstance(MainActivity.this).roomDao().insert(imagePojo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            imagePojoList.add(imagePojo);
            imageAdapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }


    class GetAllImages extends AsyncTask<Void, Void, List<ImagePojo>> {

        @Override
        protected List<ImagePojo> doInBackground(Void... voids) {
            return ImageDatabse.getInstance(MainActivity.this).roomDao()
                    .getAllNotes();
        }

        @Override
        protected void onPostExecute(List<ImagePojo> searchItems) {
            super.onPostExecute(searchItems);
            Log.i("TEST", "-> " + searchItems.get(0).getUrl());
            imagePojoList = searchItems;
        }
    }
}
