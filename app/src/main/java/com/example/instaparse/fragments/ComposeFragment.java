package com.example.instaparse.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instaparse.R;
import com.example.instaparse.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public static final String TAG = "ComposeFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private EditText etDescription;
    private Button btnCaptureImage;
    private ImageView ivPostImage;
    private Button btnSubmit;
    private File photoFile;
    private String photoFileName = "photo.jpg";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // The onCreate method is called when the Fragment should create its View object hierarchy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        //        etDescription = findViewById(R.id.etDescription);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivPostImage = view.findViewById(R.id.ivPostImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser);
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
    }


    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post p : posts) {
                    Log.i(TAG, "Post: " + p.getDescription() + ", username: " + p.getUser().getUsername());
                }
            }
        });
    }

    private void savePost(String description, ParseUser user) {
        Post p = new Post();
        p.setDescription(description);
        p.setUser(user);
        p.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving post!", e);
                }
                Log.i(TAG, "Post was saved successfully!");
                Toast.makeText(getContext(), "Posted!", Toast.LENGTH_SHORT).show();
                etDescription.setText(""); // reset text
            }
        });

    }

    public File getPhotoFileUri(String photoFileName) {
        File mediaStorageDirectory = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directtory if it does not exist
        if (!mediaStorageDirectory.exists() && !mediaStorageDirectory.mkdirs()) {
            Log.d(TAG, "failed to create media directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDirectory.getPath() + File.separator + photoFileName);
    }

    private void launchCamera() {
        //Create intent to take a picutre and reutnr control to the calling application
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access 
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object in a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.exmample.instaparse", photoFile);
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // Note: If you call starActivityResult() using an intent that no app can handle, the application will crash
        // So long as the result is not null, it's safe to use the intent
        if (i.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take the photo
            startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo saved locally on the phone
                Bitmap imageTaken = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // resize bitmap
                // load the image captured into a preview
                ivPostImage.setImageBitmap(imageTaken);
            } else { // fail case
                Toast.makeText(getContext(), "Failed to capture image!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}