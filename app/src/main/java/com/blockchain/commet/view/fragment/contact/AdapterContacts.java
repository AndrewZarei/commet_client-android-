package com.blockchain.commet.view.fragment.contact;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.blockchain.commet.R;
import com.blockchain.commet.data.sharepref.SharedPrefsHelper;
import com.blockchain.commet.databinding.RowContactBinding;
import com.example.mysolana.contact.SignatureForAddressComponent;
import com.solana.models.buffer.ContactModel;
import com.solana.models.buffer.GetSignaturesForAddressModel;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.MyViewHolder> {
    public List<ContactModel> list;
    public OnReply onReply;
    public OnClick onClick;
    public boolean selectable;
    public boolean isAdd;
    public ClickAddContact clickAddContact;

    public AdapterContacts(OnReply onReply, OnClick onClick, boolean isAdd, ClickAddContact clickAddContact) {
        list = new ArrayList<>();
        this.onReply = onReply;
        this.onClick = onClick;
        this.isAdd = isAdd;
        this.clickAddContact = clickAddContact;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        notifyDataSetChanged();
    }

    public void add(ContactModel model) {
        list.add(model);
        notifyDataSetChanged();
    }

    public interface OnReply {
        void onClick(ContactModel model, boolean check);
    }

    public interface OnClick {
        void onClick(ContactModel model);
    }

    public void addAll(List<ContactModel> models) {
        list.clear();
        for (int i = 0; i < models.size(); i++) {
            if (models.get(i).getPublic_key().equals(SharedPrefsHelper.getSharedPrefsHelper().get("id"))) {
                if (models.size() == 1) {
                    list.add(models.get(i));
                }
            } else {
                list.add(models.get(i));
            }
        }
        notifyDataSetChanged();

    }

    public void clear() {
        list.clear();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final RowContactBinding binding;

        private MyViewHolder(RowContactBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void Bind(ContactModel model) {
            binding.text.setText(model.getUser_name());
            if (selectable) {
                binding.checkBox.setVisibility(View.VISIBLE);
                binding.checkBox.setOnClickListener(view -> {
                    RadioButton v = (RadioButton) view;
                    binding.checkBox.setChecked(v.isChecked());
                });
                binding.card.setOnClickListener(view -> {
                });
            } else {
                binding.card.setOnClickListener(view -> onClick.onClick(model));
                binding.checkBox.setVisibility(View.GONE);
            }
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RowContactBinding itemBinding = RowContactBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(true);


        SignatureForAddressComponent signatureForAddressComponent = new SignatureForAddressComponent((hasError, data) -> {

            if (data == null ) {
                return;
            }
            GetSignaturesForAddressModel data1 = data;
            Log.e("AddressComponent", data1.getResult().get(0).getBlockTime().toString());

            double timestampInSeconds = data1.getResult().get(0).getBlockTime();

            // Using Date constructor
            Date date1 = new Date((long) (timestampInSeconds * 1000));
            System.out.println("Date using Date constructor: " + date1);

            // Using Instant class (Java 8 and later)
            Instant instant = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                instant = Instant.ofEpochSecond((long) timestampInSeconds);

                Date date2 = Date.from(instant);
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                String formattedDate = sdf.format(date2);
                holder.binding.lastActivity.setText(formattedDate);
            }
        });
        if (!list.get(position).isFilled) {
            list.get(position).isFilled = true;
            signatureForAddressComponent.getSignaturesForAddress(list.get(position).getPublic_key());
        }


        holder.binding.checkBox.setChecked(list.get(position).isIs_select());
        holder.binding.text.setText(list.get(position).getUser_name());
        switch (list.get(position).getAvatar()) {
            case "1":
                holder.binding.image.setImageResource(R.drawable.user_1);
                break;
            case "2":
                holder.binding.image.setImageResource(R.drawable.user_2);
                break;
            case "3":
                holder.binding.image.setImageResource(R.drawable.user_3);
                break;
            case "4":
                holder.binding.image.setImageResource(R.drawable.user_4);
                break;
            case "5":
                holder.binding.image.setImageResource(R.drawable.user_5);
                break;
            case "6":
                holder.binding.image.setImageResource(R.drawable.user_6);
                break;
        }

        if (isAdd) {
            holder.binding.addContact.setVisibility(View.VISIBLE);
        } else {
            holder.binding.addContact.setVisibility(View.GONE);

        }

        holder.binding.addContact.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "User add to list contact", Toast.LENGTH_SHORT).show();
            holder.binding.addContact.setVisibility(View.INVISIBLE);
            clickAddContact.add(list.get(position));
        });

        if (selectable) {
            holder.binding.checkBox.setVisibility(View.VISIBLE);
            holder.binding.card.setOnClickListener(view -> {
                if (selectable) {
                    ConstraintLayout s = (ConstraintLayout) view;
                    CheckBox v = (CheckBox) s.getChildAt(4);

                    boolean ccc = !v.isChecked();
                    onReply.onClick(list.get(position), ccc);
                    holder.binding.checkBox.setChecked(ccc);
                    list.get(position).setIs_select(ccc);
                    v.setChecked(ccc);
                }
            });
            holder.binding.checkBox.setOnClickListener(view -> {
                CheckBox v = (CheckBox) view;
                onReply.onClick(list.get(position), v.isChecked());
                holder.binding.checkBox.setChecked(v.isChecked());
                list.get(position).setIs_select(v.isChecked());
            });
        } else {
            holder.binding.card.setOnClickListener(view -> onClick.onClick(list.get(position)));
            holder.binding.checkBox.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}

