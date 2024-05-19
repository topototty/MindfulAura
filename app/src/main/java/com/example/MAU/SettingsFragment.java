package com.example.MAU;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.Toast;

import com.example.MAU.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private static final String SWITCH_STATE_KEY = "switch_state";
    private static final String TAG = "PushService";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private LinearLayout deleteAccountLayout, resetLoginLayout;
    private View deleteAccountSeparator, resetLoginSeparator;
    private ImageButton logoutButton, deleteAccountButton, resetPasswordButton, resetLoginButton;
    private SharedPreferences sharedPreferences;
    private Switch noticeSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        logoutButton = view.findViewById(R.id.logoutButton);
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getContext(), LoginManager.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
        deleteAccountLayout = view.findViewById(R.id.deleteAccountLayout);
        deleteAccountSeparator = view.findViewById(R.id.deleteAccountSeparator);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        resetLoginButton = view.findViewById(R.id.resetLoginButton);

        resetLoginLayout = view.findViewById(R.id.resetLoginLayout);
        resetLoginSeparator = view.findViewById(R.id.resetLoginSeparator);

        noticeSwitch = view.findViewById(R.id.noticeSwitch);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            TextView userEmail = view.findViewById(R.id.userEmail);
            TextView userLogin = view.findViewById(R.id.userLogin);

            userEmail.setText(user.getEmail());
            userLogin.setText(user.getDisplayName());
        }

        if (Objects.equals(user.getDisplayName(), "Administrator")) {
            deleteAccountLayout.setVisibility(View.GONE);
            resetLoginLayout.setVisibility(View.GONE);
            deleteAccountSeparator.setVisibility(View.GONE);
            resetLoginSeparator.setVisibility(View.GONE);
        }

        // Выход из системы
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getContext(), LoginManager.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // Удаление аккаунта (кнопка скрыта у администратора)
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Модальное окно удаления аккаунта и всех данных связанных с ним
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Подтвердите удаление аккаунта");
                builder.setMessage("Вы уверены, что хотите удалить свой аккаунт? Это действие нельзя отменить.");
                builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            deleteUserAndData(user);

                        }
                    }
                });
                builder.setNegativeButton("Отмена", null);
                builder.show();
            }
        });

        // Изменение имени пользователя
        resetLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeUsernameDialog();
            }
        });

        // Изменение пароля пользователя
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });


        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        noticeSwitch.setChecked(sharedPreferences.getBoolean(SWITCH_STATE_KEY, false));

        noticeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean notificationsEnabled = areNotificationsEnabled(getContext());
                if (notificationsEnabled) {
                    if (isChecked) {
                        NotificationSubscriptionManager.subscribeToNotifications();
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        String token = task.getResult();
                                        String msg = getString(R.string.msg_token_fmt, token);
                                        Log.d(TAG, msg);
                                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        NotificationSubscriptionManager.unsubscribeFromNotifications();
                    }
                } else {
                    noticeSwitch.setChecked(false);
                    Toast.makeText(getContext(), "Включите уведомления в настройках вашего устройства", Toast.LENGTH_SHORT).show();
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SWITCH_STATE_KEY, isChecked);
                editor.apply();
            }
        });

        return view;
    }

    private boolean areNotificationsEnabled(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        return notificationManager.areNotificationsEnabled();
    }
    private void deleteUserAndData(FirebaseUser user) {
        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                firebaseAuth.signOut();
                deleteUserDataFromFirestore(user.getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("err", e.getMessage());
            }
        });

    }

    private void deleteUserDataFromFirestore(String userId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("uid", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            deleteUserFb(documentSnapshot);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void deleteUserFb(DocumentSnapshot doc) {
        User delUser = doc.toObject(User.class);
        db.collection("users")
                .document(doc.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        deleteNotesByUserId(delUser.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SettingsFragment", "Ошибка удаления документа пользователя: " + e.getMessage());
                    }
                });
    }

    private void deleteNotesByUserId(String uid) {
        db.collection("notes").whereEqualTo("user_id", uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            delAllNotes(doc);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        Intent intent = new Intent(getContext(), LoginManager.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void delAllNotes(DocumentSnapshot doc) {
        db.collection("notes")
                .document(doc.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showChangeUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);

        final EditText input = new EditText(getContext());
        input.setHint("Введите новое имя пользователя");
        builder.setView(input);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = input.getText().toString().trim();
                if (!newUsername.isEmpty()) {
                    updateUsername(newUsername);
                }
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void updateUsername(final String newUsername) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newUsername)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("SettingsFragment", "User profile updated.");
                                // Обновление Firestore
                                updateUsernameInFirestore(user.getUid(), newUsername);
                            } else {
                                Log.e("SettingsFragment", "Error updating user profile: " + task.getException());
                                Toast.makeText(getContext(), "Ошибка обновления имени пользователя", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updateUsernameInFirestore(String userId, final String newUsername) {
        db.collection("users").whereEqualTo("uid", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection("users").document(document.getId())
                                        .update("displayName", newUsername)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("SettingsFragment", "Username updated in Firestore.");
                                                Toast.makeText(getContext(), "Имя пользователя обновлено", Toast.LENGTH_SHORT).show();
                                                // Обновление отображения
                                                TextView userLogin = getView().findViewById(R.id.userLogin);
                                                userLogin.setText(newUsername);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("SettingsFragment", "Error updating username in Firestore: " + e.getMessage());
                                                Toast.makeText(getContext(), "Ошибка обновления имени пользователя в базе данных", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Log.e("SettingsFragment", "Error getting user documents: " + task.getException());
                        }
                    }
                });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);

        final EditText input = new EditText(getContext());
        input.setHint("Введите новый пароль");
        builder.setView(input);

        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = input.getText().toString().trim();
                if (!newPassword.isEmpty()) {
                    updatePassword(newPassword);
                }
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Пароль обновлен", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Ошибка обновления пароля", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}