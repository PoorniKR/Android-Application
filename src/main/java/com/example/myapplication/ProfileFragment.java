package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    private Button logoutButton;
    private FirebaseAuth mAuth;
    private TextView usernameTextView;
    private RecyclerView recyclerViewPosts;
    private PostsAdapter postsAdapter;
    private List<Post> postsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        recyclerViewPosts = view.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        postsList = new ArrayList<>();
        postsAdapter = new PostsAdapter(getActivity(), postsList);
        recyclerViewPosts.setAdapter(postsAdapter);

        usernameTextView = view.findViewById(R.id.usernameTextView);

        fetchPosts();
        fetchUsername();

        mAuth = FirebaseAuth.getInstance();

        logoutButton = view.findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void fetchPosts() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Firestore", "Fetching posts for user: " + userId);
        db.collection("posts")
                .whereEqualTo("uid", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        Log.d("Firestore", "Fetched posts: " + querySnapshot.size());
                        if (querySnapshot != null) {
                            postsList.clear();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Post post = document.toObject(Post.class);
                                postsList.add(post);
                                Log.d("Firestore", "Fetched post: " + post.getTitle());
                            }
                            postsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                }).addOnFailureListener(e -> {Log.e("Firestore", "Error fetching posts", e);});
    }

    private void fetchUsername() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not logged in
            return;
        }

        String userId = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String username = document.getString("username");
                            if (username != null) {
                                usernameTextView.setText(username);
                            } else {
                                Log.d("Firestore", "Username not found");
                            }
                        }
                    } else {
                        Log.w("Firestore", "Error getting user document.", task.getException());
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching username", e);
                });
    }

}