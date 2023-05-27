package com.example.womensafetyapp.View;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.womensafetyapp.Model.Contacts;
import com.example.womensafetyapp.R;
import com.example.womensafetyapp.ViewModel.ContactViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class Contact_RecyclerViewAdapter extends RecyclerView.Adapter<Contact_RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<Contacts> contacts;
    ContactViewModel viewModel;

    public Contact_RecyclerViewAdapter(Context context,List<Contacts> contacts,ContactViewModel viewModel){
        this.context=context;
        this.contacts=contacts;
        this.viewModel=viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).
                inflate(R.layout.contacts_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Contacts contact=contacts.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getPhone_no());
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Remove Contact")
                        .setMessage("Are you sure want to remove this contact?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // delete the specified contact from the database
                                viewModel.delete_contact(contact);
                                // remove the item from the list
                                contacts.remove(contact);
                                // notify the listview that dataset has been changed
                                notifyDataSetChanged();
                                Toast.makeText(context, "Contact removed!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return false;
            }
        });
    }



    @Override
    public int getItemCount() {
        return contacts.size();
    }
    public void refresh(List<Contacts> list) {
        contacts.clear();
        contacts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView number;
        private LinearLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.contact_name);
            number=itemView.findViewById(R.id.contact_number);
            layout=itemView.findViewById(R.id.contact_layout);
        }
    }
}
