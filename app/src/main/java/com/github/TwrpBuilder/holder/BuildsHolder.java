package com.github.TwrpBuilder.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.FlasherActivity;
import com.github.TwrpBuilder.model.Message;
import com.github.TwrpBuilder.util.GMail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;
import com.stericson.RootTools.RootTools;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.github.TwrpBuilder.app.InitActivity.isSupport;
import static com.github.TwrpBuilder.util.Config.getBuildModel;

/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class BuildsHolder extends RecyclerView.ViewHolder {
    private TextView tvDevice, tvBoard, tvDate, tvBrand, tvDeveloper, tvNote;
    private Button btDownload, btFlash, btFeedBack;
    private Context context;
    private boolean filterQuery;
    private String reference;
    private String colon = " : ";
    private String email;
    private String developer;
    private String brand;
    private String device;
    private String board;
    private String date;

    public BuildsHolder(View v, String reference, boolean filterQuery, final Context context) {
        super(v);
        this.context = context;
        this.filterQuery = filterQuery;
        this.reference = reference;
        tvDevice = v.findViewById(R.id.list_user_device);
        tvBoard = v.findViewById(R.id.list_user_board);
        tvDate = v.findViewById(R.id.list_user_date);
        tvBrand = v.findViewById(R.id.list_user_brand);
        tvDeveloper = v.findViewById(R.id.list_developer_email);
        tvNote = v.findViewById(R.id.list_reject_note);
        btDownload = v.findViewById(R.id.bt_download_recovery);
        btFlash = v.findViewById(R.id.bt_flash);
        btFeedBack = v.findViewById(R.id.bt_feedback);

    }

    public void bind(String email, String device, String board, String date, String brand, String developer, String reject, String note, String url) {
        this.email = email;
        this.developer = developer;
        this.brand = brand;
        this.device = device;
        this.board = board;
        this.date = date;
        setTvBoard(board);
        setTvBrand(brand);
        setTvDate(date);
        setTvDevice(device);
        if (reference.equals("Builds")) {
            tvDeveloper.setVisibility(View.VISIBLE);
            btDownload.setVisibility(View.VISIBLE);
            setBtDownload(url);
            setTvDeveloper(developer);
            setTvNote(note);
            if (filterQuery) {
                if (RootTools.isAccessGiven() && isSupport) {
                    btFlash.setVisibility(View.VISIBLE);
                }
                btFeedBack.setVisibility(View.VISIBLE);
                setBtFlash();
                setBtFeedBack();

            }
        } else if (reference.equals("Rejected")) {
            tvDeveloper.setVisibility(View.VISIBLE);
            tvNote.setVisibility(View.VISIBLE);
            setReject(reject);
            setTvNote(note);
        }
    }

    private void setTvBoard(String board) {
        tvBoard.setText(context.getString(R.string.board) + colon + board);
    }

    private void setBtDownload(final String url) {
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(context);
                View downloadDialog = li.inflate(R.layout.dialog_download, null);
                BottomSheetDialog dialog = new BottomSheetDialog(context);
                dialog.setContentView(downloadDialog);
                TextView model = downloadDialog.findViewById(R.id.tv_model);
                TextView tvboard = downloadDialog.findViewById(R.id.tv_board);
                TextView tvbrand = downloadDialog.findViewById(R.id.tv_brand);
                final CardView cardView = downloadDialog.findViewById(R.id.cv_developerProfile);
                final TextView tvGitId = downloadDialog.findViewById(R.id.tv_his_gitId);
                final TextView tvXdaUrl = downloadDialog.findViewById(R.id.tv_his_xda);
                final TextView tvDescription = downloadDialog.findViewById(R.id.tv_his_description);
                final TextView tvName = downloadDialog.findViewById(R.id.tv_his_name);
                final TextView textViewEmail = downloadDialog.findViewById(R.id.tv_his_email);
                final TextView tvDonation = downloadDialog.findViewById(R.id.tv_his_donation);
                final CircleImageView imageViewProfile = downloadDialog.findViewById(R.id.img_his_profile);
                final Button btDownload = downloadDialog.findViewById(R.id.download);
                tvbrand.setText(context.getString(R.string.brand) + colon + device);
                tvboard.setText(context.getString(R.string.board) + colon + board);
                model.setText(context.getString(R.string.model) + colon + brand);

                Query query = FirebaseDatabase.getInstance().getReference().child("Developers").orderByChild("email").equalTo(developer);
                query.keepSynced(true);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                cardView.setVisibility(View.VISIBLE);
                                tvGitId.setText(d.child("gitId").getValue().toString());
                                tvName.setText(d.child("name").getValue().toString());
                                textViewEmail.setText(d.child("email").getValue().toString());
                                tvXdaUrl.setText(d.child("xdaUrl").getValue().toString());
                                if (d.child("description").getValue() != null){
                                    tvDescription.setText(d.child("description").getValue().toString());
                                    tvDescription.setVisibility(View.VISIBLE);
                                }
                                if (d.child("donationUrl").getValue() != null) {
                                    tvDonation.setText(d.child("donationUrl").getValue().toString());
                                    tvDonation.setVisibility(View.VISIBLE);
                                }
                                Glide.with(context.getApplicationContext())
                                        .load(d.child("photoUrl").getValue())
                                        .apply(new RequestOptions().centerCrop())
                                        .into(imageViewProfile);
                                btDownload.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                        context.startActivity(browserIntent);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.show();

            }
        });

    }

    private void setBtFlash() {
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FlasherActivity.class));
            }
        });
    }

    private String content;

    private void setBtFeedBack() {
        btFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = "Your build made for \nbrand :- "
                        + brand
                        + "\nboard :- "
                        + board
                        + "\ndevice :- "
                        + device
                        + "\non date:-"
                        + date;

                final BottomSheetDialog alertDialogBuilder = new BottomSheetDialog(context);
                LayoutInflater li = LayoutInflater.from(context);
                @SuppressLint("InflateParams") View promptsView = li.inflate(R.layout.dialog_feedback, null);
                alertDialogBuilder.setContentView(promptsView);
                final CheckBox CBWorks = promptsView.findViewById(R.id.checkbox_works);
                final CheckBox CBNotWorks = promptsView.findViewById(R.id.checkbox_not_works);
                final EditText editTextFeedBack = promptsView.findViewById(R.id.editTextDialogUserInput);
                final Button btSend = promptsView.findViewById(R.id.send_feedback);
                CBWorks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        CBNotWorks.setChecked(false);
                    }
                });

                CBNotWorks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        CBWorks.setChecked(false);
                    }
                });
                btSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference feedBack = firebaseDatabase.getReference("FeedBack");
                        if (CBWorks.isChecked()) {
                            Message message = new Message(getBuildModel(), editTextFeedBack.getText().toString(), email, true);
                            Toast.makeText(context, context.getString(R.string.sending), Toast.LENGTH_SHORT).show();
                            feedBack.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, context.getString(R.string.Feedback_sent), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, context.getString(R.string.feedback_failed), Toast.LENGTH_SHORT).show();

                                }
                            });
                            content += "\nhas successfully booted! . please push your source on twrpbuilder common github.";
                        } else if (CBNotWorks.isChecked()) {
                            Message message = new Message(getBuildModel(), editTextFeedBack.getText().toString(), email, false);
                            feedBack.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    alertDialogBuilder.dismiss();
                                    Toast.makeText(context, context.getString(R.string.Feedback_sent), Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, context.getString(R.string.feedback_failed), Toast.LENGTH_SHORT).show();

                                }
                            });
                            content += "\nhas failed to boot :(.";
                        } else {
                            Toast.makeText(context, context.getString(R.string.works_or_not), Toast.LENGTH_SHORT).show();
                        }
                        try {
                            content += "\nrequester email :- "
                                    + email;
                            if (!editTextFeedBack.getText().toString().isEmpty()) {
                                content += "\nNotes by user :- " + editTextFeedBack.getText();
                            }
                            GMail sender = new GMail(AESCrypt.decrypt("R.menu.settings", "oeI35mT0MnrmYd8J42FNEAIdF098+WdHpAld0e1SIIY="), AESCrypt.decrypt("R.menu.activity_option", "5sQmagUbQrp1UjDpZiAIhFQBjPOzqw8pKsqVbd/PSoY="));
                            sender.sendMail("TwrpBuilder " + device, content,
                                    "twrpbuilder21@gmail.com",
                                    developer);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                });

                alertDialogBuilder.show();
            }
        });
    }

    private void setTvBrand(String brand) {
        tvBrand.setText(context.getString(R.string.brand) + colon + brand);
    }

    private void setTvDate(String date) {
        tvDate.setText(context.getString(R.string.date) + colon + date);
    }

    private void setTvDeveloper(String developer) {
        tvDeveloper.setText(context.getString(R.string.developer) + colon + developer);
    }

    private void setTvDevice(String device) {
        tvDevice.setText(context.getString(R.string.model) + colon + device);
    }

    private void setTvNote(String note) {
        tvNote.setText(context.getString(R.string.note) + colon + note);
    }

    private void setReject(String reject) {
        tvDeveloper.setText(context.getString(R.string.rejected_by) + colon + reject);
    }
}
