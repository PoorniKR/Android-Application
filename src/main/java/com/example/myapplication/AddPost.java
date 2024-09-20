package com.example.myapplication;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.location.Address;
import android.location.Geocoder;
import java.util.Locale;
import java.util.List;

public class AddPost extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int PERMISSION_REQUEST_CODE = 102;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 123;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 124;

    private EditText editTextTitle;
    private EditText editTextContent;
    private Button buttonSelectImage;
    private Button buttonCaptureImage;
    private Button buttonGetLocation;
    private ImageView imageViewSelected;
    private Button buttonPublish;

    private Uri selectedImageUri;
    private Bitmap capturedImageBitmap;
    private FusedLocationProviderClient fusedLocationClient;

    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        FirebaseApp.initializeApp(this);
        if (FirebaseAuth.getInstance() != null) {
            Log.d("FirebaseInit", "Firebase initialized successfully");
        } else {
            Log.d("FirebaseInit", "Failed to initialize Firebase");
        }

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonCaptureImage = findViewById(R.id.buttonCaptureImage);
        buttonGetLocation = findViewById(R.id.buttonGetLocation);
        imageViewSelected = findViewById(R.id.imageViewSelected);
        buttonPublish = findViewById(R.id.buttonPublish);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    openGallery();
                } else {
                    requestPermission();
                }
            }
        });

        buttonCaptureImage.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_CODE_CAPTURE_IMAGE);
        });

        buttonGetLocation.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                fetchLocation();
            } else {
                requestLocationPermission();
            }
        });

        buttonPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishPost();
            }
        });
    }

    private boolean checkLocationPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_LOCATION_PERMISSION:
                    fetchLocation();
                    break;

                case PERMISSION_REQUEST_CODE:
                    openGallery();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_CODE_LOCATION_PERMISSION:
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                    break;

                case PERMISSION_REQUEST_CODE:
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void fetchLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                lastLocation = task.getResult();
                                double latitude = lastLocation.getLatitude();
                                double longitude = lastLocation.getLongitude();
                                getAddressFromLocation(latitude, longitude);
                                //Toast.makeText(AddPost.this, "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();
                            } else {
                                Log.w("Location", "getLastLocation:exception", task.getException());
                                Toast.makeText(AddPost.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e("Location", "Security exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();
                String fullAddress = addressText + ", " + city + ", " + state + ", " + country;

                Toast.makeText(AddPost.this, "Location: " + fullAddress, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(AddPost.this, "Unable to find location name", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(AddPost.this, "Geocoder service not available", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Log.d("ImageSelection", "Image URI: " + selectedImageUri.toString());
                imageViewSelected.setVisibility(View.VISIBLE);
                imageViewSelected.setImageURI(selectedImageUri);
            } else {
                Log.e("ImageSelection", "Selected image URI is null");
            }
        }

        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK && data != null) {
            capturedImageBitmap = (Bitmap) data.getExtras().get("data");
            imageViewSelected.setVisibility(View.VISIBLE);
            imageViewSelected.setImageBitmap(capturedImageBitmap);
        }
    }

    private void publishPost() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(AddPost.this, "Title and content cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            uploadImageAndPost(title, content, selectedImageUri);
        } else if (capturedImageBitmap != null) {
            uploadBitmapAndPost(title, content, capturedImageBitmap);
        } else {
            postToFirestore(title, content, null);
        }
    }

    private void uploadImageAndPost(String title, String content, Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        imagesRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imagesRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            Log.d("Firebase", "Image uploaded successfully. URL: " + uri.toString());
                            postToFirestore(title, content, imageUrl);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Failed to get image URL!", e);
                            Toast.makeText(AddPost.this, "Failed to get image URL!", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed!", e);
                    Toast.makeText(AddPost.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadBitmapAndPost(String title, String content, Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imagesRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> imagesRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            Log.d("Firebase", "Image uploaded successfully. URL: " + uri.toString());
                            postToFirestore(title, content, imageUrl);
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Failed to get image URL!", e);
                            Toast.makeText(AddPost.this, "Failed to get image URL!", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed!", e);
                    Toast.makeText(AddPost.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void postToFirestore(String title, String content, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (imageUrl != null) {
            post.put("imageUrl", imageUrl);
        }
        if (lastLocation != null) {
            post.put("latitude", lastLocation.getLatitude());
            post.put("longitude", lastLocation.getLongitude());
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String addressText = address.getAddressLine(0);
                    String city = address.getLocality();
                    String state = address.getAdminArea();
                    String country = address.getCountryName();
                    post.put("address", addressText);
                    post.put("city", city);
                    post.put("state", state);
                    post.put("country", country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                   // Toast.makeText(AddPost.this, "Post published!", Toast.LENGTH_SHORT).show();
                    createNotificationChannel();
                    addNotification();
                    editTextTitle.setText("");
                    editTextContent.setText("");
                    imageViewSelected.setVisibility(View.GONE);
                    selectedImageUri = null;
                    capturedImageBitmap = null;
                    Intent intent = new Intent(AddPost.this, ProfileFragment.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    Toast.makeText(AddPost.this, "Failed to publish post!", Toast.LENGTH_SHORT).show();
                });
    }

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "default_channel")
                        .setSmallIcon(R.drawable.baseline_notifications_24)
                        .setContentTitle("Funstagram")
                        .setContentText("Post published successfully")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(getApplicationContext(), NotificationFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Default Channel";
            String description = "Channel for general notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
