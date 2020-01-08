package com.example.walkinclinicapp.ui.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.walkinclinicapp.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import Account.Account;

public class AccountList extends SwipeRecyclerView.Adapter<AccountList.AccountViewHolder>{
    // instance
    private Context context;
    private ArrayList<Account> accounts;

    // view holder class
    public static class AccountViewHolder extends SwipeRecyclerView.ViewHolder {
        private TextView lastName;
        private TextView firstName;
        private TextView type;
        private TextView id;

        private AccountViewHolder(View v) {
            super(v);
            lastName = (TextView) v.findViewById(R.id.textViewLastNamePopup);
            firstName = (TextView) v.findViewById(R.id.textViewFirstNamePopup);
            type = (TextView) v.findViewById(R.id.textViewAccountType);
            id = (TextView) v.findViewById(R.id.textViewIDPopup);
        }

        //
        private void setAccountText(Account account) {
            lastName.setText(account.getLastName());
            firstName.setText(account.getFirstName());
            type.setText(account.getType());
            id.setText("ID: " + account.getId());
        }
    }

    // constructor
    public AccountList(Context context, ArrayList<Account> accounts) {
        this.context = context;
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    // Create new view (invoked by the layout manager)


    @NonNull
    @Override
    public AccountList.AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_account, parent, false);
        AccountViewHolder viewHolder = new AccountViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AccountList.AccountViewHolder holder, int position) {
        // get element from dataset at this position
        // replace the contents of the view with that element
        holder.setAccountText(accounts.get(position));
    }

    // Return the size
    @Override
    public int getItemCount() {
        if (accounts != null){
            return accounts.size();
        }
        return 0;
    }
}
