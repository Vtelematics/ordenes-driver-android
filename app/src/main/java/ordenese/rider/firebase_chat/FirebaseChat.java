package ordenese.rider.firebase_chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ordenese.rider.Common.AppLanguageSupport;
import ordenese.rider.Common.Constant;
import ordenese.rider.R;
import ordenese.rider.Service.FcmNotificationsSender;

public class FirebaseChat extends Fragment {

    RecyclerView chat_list;
    ChatsAdapter adapter;
    ArrayList<Chat> chats;

    //    FirebaseUser fuser_mine;
//    FirebaseAuth authLogin;
    DatabaseReference referenceUser;
    DatabaseReference referenceChat;

    ImageView send, select_image;
    EditText msg_ed;
    String time = "", user_id = "", rider_uid = "", token_user = "";
    Activity activity;
    ProgressBar progress_bar;

    private static int PICK_IMAGE_CAMERA = 0, PICK_IMAGE_GALLERY = 0;
    private final static int IMAGE = 1;
    private String PROFILE_IMAGE;
    public static String PROFILE_PATH;

    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static ActivityResultLauncher<Intent> someActivityResultLauncher;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_id = getArguments().getString("user_id");
            // Log.e("onCreate: ", Order_id + "");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(AppLanguageSupport.onAttach(context));
        this.activity = (Activity) context;
        if (getActivity() != null) {
            getActivity().getWindow().getDecorView().setLayoutDirection(
                    "ae".equals(AppLanguageSupport.getLanguage(getActivity())) ?
                            View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_firebase_chat, container, false);

        rider_uid = Constant.DataGetValue(activity, "rider_uid");

        referenceUser = FirebaseDatabase.getInstance().getReference("users_list").child(user_id).child("token");
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    token_user = snapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send = view.findViewById(R.id.send);
        msg_ed = view.findViewById(R.id.msg);
        select_image = view.findViewById(R.id.select_image);
        progress_bar = view.findViewById(R.id.progress_bar);
        chat_list = view.findViewById(R.id.chat_list);
        chat_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setStackFromEnd(true);

        chat_list.setLayoutManager(linearLayoutManager);
//        authLogin = FirebaseAuth.getInstance();

        referenceUser = FirebaseDatabase.getInstance().getReference("users_list").child(user_id);
//        vendor_uid = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                vendor_uid = FirebaseAuth.getInstance().getCurrentUser();

                if (rider_uid != null) {
                    chats = new ArrayList<>();
                    readMessage(rider_uid, user_id, "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions(activity, PERMISSIONS)) {
                    selectImage();
                } else {
                    requestPermission(IMAGE);
                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!msg_ed.getText().toString().isEmpty()) {
                    time = String.valueOf(Calendar.getInstance().getTime());
                    sendMessage(rider_uid, user_id, msg_ed.getText().toString(), "text");
                }
            }
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (PICK_IMAGE_CAMERA == 1) {

                                progress_bar.setVisibility(View.VISIBLE);

                                PICK_IMAGE_CAMERA = 0;
                                Bundle extras = data.getExtras();
                                Bitmap photo = (Bitmap) extras.get("data");

                                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                                Uri tempUri = getImageUri(activity, photo);
                                // CALL THIS METHOD TO GET THE ACTUAL PATH
                                File finalFile = new File(getRealPathFromURI(tempUri));
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byte_arr = stream.toByteArray();

                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                String path = timestamp.getTime() + "_" + rider_uid + "_" + user_id + "_" + "file.jpg";

                                storageReference = storage.getReference().child("images").child(path);
                                UploadTask uploadTask = storageReference.putBytes(byte_arr);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Constant.loadToastMessage(activity, activity.getResources().getString(R.string.process_failed_please_try_again));
                                        progress_bar.setVisibility(View.GONE);
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                if (uri != null) {

                                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    String val = timestamp.getTime() + "_" + rider_uid + "_" + user_id;
                                                    Chat chat = new Chat(rider_uid, user_id, uri.toString(), "image");
                                                    hashMap.put(val, chat);
                                                    reference.child("messages").child(rider_uid).child(user_id).updateChildren(hashMap);

                                                    HashMap<String, Object> hashMap1 = new HashMap<>();
                                                    String val1 = timestamp.getTime() + "_" + rider_uid + "_" + user_id;
                                                    Chat chat1 = new Chat(rider_uid, user_id, uri.toString(), "image");
                                                    hashMap1.put(val1, chat1);
                                                    reference.child("messages").child(user_id).child(rider_uid).updateChildren(hashMap1);
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Constant.loadToastMessage(activity, activity.getResources().getString(R.string.process_failed_please_try_again));
                                                progress_bar.setVisibility(View.GONE);
                                            }
                                        });

                                    }
                                });
                            } else if (PICK_IMAGE_GALLERY == 1) {

                                PICK_IMAGE_GALLERY = 0;
                                Uri filePath = data.getData();
                                progress_bar.setVisibility(View.VISIBLE);
                                try {
                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    String path = timestamp.getTime() + "_" + rider_uid + "_" + user_id + "_" + "file.jpg";

                                    storageReference = storage.getReference().child("images").child(path);
                                    UploadTask uploadTask = storageReference.putFile(filePath);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Constant.loadToastMessage(activity, activity.getResources().getString(R.string.process_failed_please_try_again));
                                            progress_bar.setVisibility(View.GONE);
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (uri != null) {
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        String val = timestamp.getTime() + "_" + rider_uid + "_" + user_id;
                                                        Chat chat = new Chat(rider_uid, user_id, uri.toString(), "image");
                                                        hashMap.put(val, chat);
                                                        reference.child("messages").child(rider_uid).child(user_id).updateChildren(hashMap);

                                                        HashMap<String, Object> hashMap1 = new HashMap<>();
                                                        String val1 = timestamp.getTime() + "_" + rider_uid + "_" + user_id;
                                                        Chat chat1 = new Chat(rider_uid, user_id, uri.toString(), "image");
                                                        hashMap1.put(val1, chat1);
                                                        reference.child("messages").child(user_id).child(rider_uid).updateChildren(hashMap1);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Constant.loadToastMessage(activity, activity.getResources().getString(R.string.process_failed_please_try_again));
                                                    progress_bar.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });


        return view;
    }

    private void seenMessages() {
        DatabaseReference referenceChat_ = FirebaseDatabase.getInstance().getReference("messages").child(rider_uid).child(user_id);
        referenceChat_.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue() != null) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            if (chat.getSeen() != null && chat.getSeen().equals("false") && chat.getSender().equals(user_id)) {
                                DatabaseReference referenceChat__;
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("seen", "true");
                                referenceChat__ = FirebaseDatabase.getInstance().getReference("messages").child(rider_uid).child(user_id).child(snapshot.getKey());
                                referenceChat__.updateChildren(hashMap);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void sendMessage(String sender, String receiver, String content, String type) {
        msg_ed.setText("");
        msg_ed.clearFocus();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String token = "";
        if (!Constant.DataGetValue(activity, "token_notify").equals("empty") && !Constant.DataGetValue(activity, "token_notify").equals("")) {
            token = Constant.DataGetValue(activity, "token_notify");
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        String val = timestamp.getTime() + "_" + sender + "_" + receiver;
        Chat chat = new Chat(sender, receiver, content, type, "");
        hashMap.put(val, chat);
//        hashMap.put(val, message);
//        hashMap.put("sender", sender);
//        hashMap.put("receiver", receiver);
//        hashMap.put("message", message);
//        hashMap.put("time", time);
        reference.child("messages").child(sender).child(receiver).updateChildren(hashMap);

        HashMap<String, Object> hashMap1 = new HashMap<>();
        String val1 = timestamp.getTime() + "_" + sender + "_" + receiver;
        Chat chat1 = new Chat(sender, receiver, content, type, "");
        hashMap1.put(val1, chat1);
        reference.child("messages").child(receiver).child(sender).updateChildren(hashMap1);

        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("token", token);
        referenceUser = FirebaseDatabase.getInstance().getReference("users_list").child(sender);
        referenceUser.updateChildren(hashMap2);

        if (token_user != null && !token_user.isEmpty()) {
            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(token_user, "", content, activity, activity);
            fcmNotificationsSender.SendNotifications();
        }
    }

    private void readMessage(String my_id, String user_id, String image_url) {

        referenceChat = FirebaseDatabase.getInstance().getReference("messages").child(my_id).child(user_id);
        referenceChat.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue() != null) {
                        Chat chat = snapshot.getValue(Chat.class);
                        chats.add(chat);
                        progress_bar.setVisibility(View.GONE);
//                        for (Map.Entry<String, Object> m : hashMap.entrySet()) {
//                        }

//                        String[] val = snapshot.getKey().split("_");
//                        String time = val[0];
//                        String sender = val[1];
//                        String receiver = val[2];
                    }
                }
                adapter = new ChatsAdapter(activity, chats);
                chat_list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progress_bar.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        seenMessages();
        progress_bar.setVisibility(View.GONE);
    }

    public class ChatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final ArrayList<Chat> mChat;
        private final Context mContext;

        public static final int MSG_TYPE_LEFT = 0;
        public static final int MSG_TYPE_RIGHT = 1;

        public ChatsAdapter(Context context, ArrayList<Chat> mChat) {
            this.mContext = context;
            this.mChat = mChat;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // View view = LayoutInflater.from(mContext).inflate(R.layout.reviews_row, parent, false);
            //  return new ViewHolder(view);
            if (viewType == MSG_TYPE_RIGHT) {
                return new ChatViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false));
            } else {
                return new ChatViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false));
            }

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            if (mChat.get(position).getType().equals("text")) {
                chatViewHolder.mCustomerName.setVisibility(View.VISIBLE);
                chatViewHolder.image_chat.setVisibility(View.GONE);
                chatViewHolder.mCustomerName.setText(mChat.get(position).getContent());
            } else {
                chatViewHolder.mCustomerName.setVisibility(View.GONE);
                chatViewHolder.image_chat.setVisibility(View.VISIBLE);
                Constant.glide_image_loader(mChat.get(position).getContent(), chatViewHolder.image_chat);
            }
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return mChat.size();
        }

        @Override
        public int getItemViewType(int position) {

            if (mChat.get(position).getSender().equals(rider_uid)) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }

        public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final TextView mCustomerName;
            ImageView image_chat;

            public ChatViewHolder(View itemView) {
                super(itemView);
                mCustomerName = itemView.findViewById(R.id.msg_tv);
                image_chat = itemView.findViewById(R.id.image_chat);
                image_chat.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.image_chat) {
                    if (!mChat.get(getBindingAdapterPosition()).getType().equals("text")) {
                        DialogImageView imageView = new DialogImageView();
                        Bundle bundle = new Bundle();
                        bundle.putString("image", chats.get(getBindingAdapterPosition()).getContent());
                        imageView.setArguments(bundle);
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.delivery_loader, imageView, "imageView")
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .addToBackStack("imageView")
                                .commitAllowingStateLoss();
                    }
                }
            }
        }
    }

    private void selectImage() {
        try {
            PackageManager pm = activity.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, activity.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                ImagePicker addPhotoBottomDialogFragment =
                        new ImagePicker(activity);
                addPhotoBottomDialogFragment.show(getChildFragmentManager(),
                        addPhotoBottomDialogFragment.getTag());
            } else
                showAlert();
        } catch (Exception e) {
//        Toast.makeText(activity, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(getResources().getString(R.string.app_needs_to_access_the_Camera));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.deny),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.allow),
                (dialog, which) -> {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            PICK_IMAGE_CAMERA);


                });
        alertDialog.show();
    }

    private void requestPermission(int Type) {

        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, Type);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static class ImagePicker extends BottomSheetDialogFragment {

        LinearLayout take_camera, take_gallery;
        Activity activity;

        public ImagePicker(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {

            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.image_upload, container, false);

            take_camera = view.findViewById(R.id.take_camera);
            take_gallery = view.findViewById(R.id.take_gallery);

            take_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    someActivityResultLauncher.launch(intent);
                    PICK_IMAGE_CAMERA = 1;
                    PICK_IMAGE_GALLERY = 0;
                }
            });
            take_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    someActivityResultLauncher.launch(pickPhoto);
                    PICK_IMAGE_CAMERA = 0;
                    PICK_IMAGE_GALLERY = 1;
                }
            });

            return view;
        }
    }
}