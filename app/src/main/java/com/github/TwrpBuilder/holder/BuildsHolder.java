package com.github.TwrpBuilder.holder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.TwrpBuilder.R;
import com.github.TwrpBuilder.app.FlasherActivity;
import com.github.TwrpBuilder.model.Message;
import com.github.TwrpBuilder.util.GMail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottyab.aescrypt.AESCrypt;
import com.stericson.RootTools.RootTools;


import static com.github.TwrpBuilder.app.InitActivity.isSupport;
import static com.github.TwrpBuilder.util.Config.getBuildModel;

/**
 * Created by androidlover5842 on 12.3.2018.
 */

public class BuildsHolder extends RecyclerView.ViewHolder {
    private TextView tvEmail,tvDevice,tvBoard,tvDate,tvBrand,tvDeveloper,tvNote;
    private Button btDownload,btFlash,btFeedBack;
    private Context context;
    private boolean filterQuery;
    private String reference;
    private String colon=" : ";
    private String email;
    private String developer;
    private String brand;
    private String device;
    private String board;
    private String date;

    public BuildsHolder(View v, String reference, boolean filterQuery, final Context context) {
        super(v);
        this.context=context;
        this.filterQuery=filterQuery;
        this.reference=reference;
        tvEmail = v.findViewById(R.id.list_user_email);
        tvDevice = v.findViewById(R.id.list_user_device);
        tvBoard = v.findViewById(R.id.list_user_board);
        tvDate= v.findViewById(R.id.list_user_date);
        tvBrand = v.findViewById(R.id.list_user_brand);
        tvDeveloper=v.findViewById(R.id.list_developer_email);
        tvNote=v.findViewById(R.id.list_reject_note);
        btDownload=v.findViewById(R.id.bt_download_recovery);
        btFlash=v.findViewById(R.id.bt_flash);
        btFeedBack=v.findViewById(R.id.bt_feedback);

    }

    public void bind(String email,String device,String board,String date,String brand,String developer,String reject,String note,String url){
        this.email=email;
        this.developer=developer;
        this.brand=brand;
        this.device=device;
        this.board=board;
        this.date=date;
        setTvBoard(board);
        setTvEmail(email);
        setTvBrand(brand);
        setTvDate(date);
        setTvDevice(device);
        if (reference.equals("Builds"))
        {
            tvDeveloper.setVisibility(View.VISIBLE);
            btDownload.setVisibility(View.VISIBLE);
            setBtDownload(url);
            setTvDeveloper(developer);
            setTvNote(note);
            if (filterQuery)
            {
                if (RootTools.isAccessGiven() && isSupport)
                {
                    btFlash.setVisibility(View.VISIBLE);
                }
                btFeedBack.setVisibility(View.VISIBLE);
              setBtFlash();
              setBtFeedBack();

            }
        }else if (reference.equals("Rejected"))
        {
            tvDeveloper.setVisibility(View.VISIBLE);
            tvNote.setVisibility(View.VISIBLE);
            setReject(reject);
            setTvNote(note);
        }
    }

    public void setTvBoard(String board) {
        tvBoard.setText(context.getString(R.string.board)+colon +board);
    }

    public void setBtDownload(final String url) {
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });

    }

    public void setBtFlash() {
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FlasherActivity.class));
            }
        });
    }

    private String content;
    private void setBtFeedBack(){
        btFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content="Your build made for \nbrand :- "
                        +brand
                        +"\nboard :- "
                        +board
                        +"\ndevice :- "
                        +device
                        +"\non date:-"
                        +date;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_feedback, null);
                alertDialogBuilder.setView(promptsView);
                final CheckBox CBworks=promptsView.findViewById(R.id.checkbox_works);
                final CheckBox CBNotWorks=promptsView.findViewById(R.id.checkbox_not_works);
                final EditText editTextFeedBack=promptsView.findViewById(R.id.editTextDialogUserInput);
                CBworks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        CBNotWorks.setChecked(false);
                    }
                });

                CBNotWorks.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        CBworks.setChecked(false);
                    }
                });

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.send),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                        DatabaseReference feedBack=firebaseDatabase.getReference("FeedBack");
                                        if (CBworks.isChecked())
                                        {
                                            Message message=new Message(getBuildModel(),editTextFeedBack.getText().toString(),email,true);
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
                                            content+="\nhas successfully booted! . please push your source on twrpbuilder common github.";
                                        }else if (CBNotWorks.isChecked())
                                        {
                                            Message message=new Message(getBuildModel(),editTextFeedBack.getText().toString(),email,false);
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
                                            content+="\nhas failed to boot :(.";
                                        }else
                                        {
                                                Toast.makeText(context, context.getString(R.string.works_or_not), Toast.LENGTH_SHORT).show();
                                        }
                                        try {
                                            content+="\nrequester email :- "
                                                    +email;
                                            if (!editTextFeedBack.getText().toString().isEmpty()) {
                                                content += "\nNotes by user :- " + editTextFeedBack.getText();
                                            }
                                            GMail sender = new GMail(AESCrypt.decrypt("R.menu.settings","oeI35mT0MnrmYd8J42FNEAIdF098+WdHpAld0e1SIIY="), AESCrypt.decrypt("R.menu.activity_option","5sQmagUbQrp1UjDpZiAIhFQBjPOzqw8pKsqVbd/PSoY="));
                                            sender.sendMail("TwrpBuilder "+device, content,
                                                    "twrpbuilder21@gmail.com",
                                                    developer);
                                        } catch (Exception e) {
                                            System.out.println(e.getMessage());
                                        }
                                    }
                                })
                        .setNegativeButton(context.getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    public void setTvBrand(String brand) {
        tvBrand.setText(context.getString(R.string.brand)+colon +brand);
    }

    public void setTvDate(String date) {
        tvDate.setText(context.getString(R.string.date)+colon +date);
    }

    public void setTvDeveloper(String developer) {
        tvDeveloper.setText(context.getString(R.string.developer)+colon +developer);
    }

    public void setTvDevice(String device) {
        tvDevice.setText(context.getString(R.string.model)+colon +device);
    }

    public void setTvEmail(String email) {
        tvEmail.setText(context.getString(R.string.email)+colon +email);
    }

    public void setTvNote(String note) {
        tvNote.setText(context.getString(R.string.note)+colon +note);
    }
    private void setReject(String reject){
        tvDeveloper.setText(context.getString(R.string.rejected_by)+colon+reject);
    }
}
