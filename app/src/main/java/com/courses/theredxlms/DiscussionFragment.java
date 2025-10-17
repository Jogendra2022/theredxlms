package com.courses.theredxlms;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.courses.theredxlms.databinding.FragmentDiscussionBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DiscussionFragment extends Fragment {

    private FragmentDiscussionBinding binding;
    private FirebaseFirestore firestore;
    private FirestoreRecyclerAdapter<DiscussionPost, DiscussionPostViewHolder> adapter;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscussionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        setupRecyclerView();
        setupSendButton(); // Call the method to activate the send button
    }

    private void setupRecyclerView() {
        // Correct the RecyclerView ID to match your fragment_discussion.xml
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        Query query = firestore.collection("discussion_posts")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<DiscussionPost> options = new FirestoreRecyclerOptions.Builder<DiscussionPost>()
                .setQuery(query, DiscussionPost.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<DiscussionPost, DiscussionPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DiscussionPostViewHolder holder, int position, @NonNull DiscussionPost model) {
                // This is the updated binding logic
                holder.authorTextView.setText(model.getAuthorName());
                holder.postTextView.setText(model.getPostText());

                if (model.getAuthorProfileUrl() != null && !model.getAuthorProfileUrl().isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(model.getAuthorProfileUrl())
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(holder.authorProfileImageView);
                } else {
                    holder.authorProfileImageView.setImageResource(R.drawable.ic_profile);
                }

                if (model.getTimestamp() != null) {
                    CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                            model.getTimestamp().getTime(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS);
                    holder.timestampTextView.setText(relativeTime);
                }
            }

            @NonNull
            @Override
            public DiscussionPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_discussion_post, parent, false);
                return new DiscussionPostViewHolder(view);
            }
        };

        binding.recyclerViewPosts.setAdapter(adapter);
    }

    // ======================= THIS IS THE CHANGED PART =======================
    private void setupSendButton() {
        // Use setEndIconOnClickListener for the button inside the TextInputLayout
        binding.commentTextInputLayout.setEndIconOnClickListener(v -> {
            String commentText = binding.etNewComment.getText().toString().trim();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(getContext(), "You must be logged in to post.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (commentText.isEmpty()) {
                Toast.makeText(getContext(), "Cannot post an empty comment.", Toast.LENGTH_SHORT).show();
                return;
            }

            DiscussionPost newPost = new DiscussionPost();
            newPost.setAuthorName(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous");
            newPost.setPostText(commentText);
            if (currentUser.getPhotoUrl() != null) {
                newPost.setAuthorProfileUrl(currentUser.getPhotoUrl().toString());
            }

            firestore.collection("discussion_posts")
                    .add(newPost)
                    .addOnSuccessListener(documentReference -> {
                        binding.etNewComment.setText(""); // Clear the input box
                        Toast.makeText(getContext(), "Comment posted!", Toast.LENGTH_SHORT).show();
                        binding.recyclerViewPosts.smoothScrollToPosition(0); // Scroll to top
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error posting comment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
    // =========================================================================

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ======================= THIS IS THE CHANGED PART =======================
    // This ViewHolder is updated to match your list_item_discussion_post.xml
    private static class DiscussionPostViewHolder extends RecyclerView.ViewHolder {
        ImageView authorProfileImageView;
        TextView authorTextView;
        TextView timestampTextView;
        TextView postTextView;
        // You can add the like/reply buttons here later

        public DiscussionPostViewHolder(@NonNull View itemView) {
            super(itemView);
            authorProfileImageView = itemView.findViewById(R.id.iv_author_profile);
            authorTextView = itemView.findViewById(R.id.tv_author_name);
            timestampTextView = itemView.findViewById(R.id.tv_post_timestamp);
            postTextView = itemView.findViewById(R.id.tv_post_text);
        }
    }
    // =========================================================================
}
