package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public MainActivity mActivity;
    public ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = MainActivity.this;
        setContentView(R.layout.activity_main);
        RecyclerView rv_photo_book = findViewById(R.id.rv_photo_book);
        EditText et_name = findViewById(R.id.et_name);
        Button btn_insert = findViewById(R.id.btn_insert);
        Button btn_delete = findViewById(R.id.btn_delete);
        Button btn_update = findViewById(R.id.btn_update);
        Button btn_insert_update = findViewById(R.id.btn_insert_update);
        Button btn_insert_100 = findViewById(R.id.btn_insert_100);

        ArrayList<ItemDto> itemLists = SQLiteDB.getInstance(MainActivity.this).selectDB();
        mAdapter = new ListAdapter(itemLists);
        rv_photo_book.setAdapter(mAdapter);
        rv_photo_book.setLayoutManager(new LinearLayoutManager(mActivity));

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = SQLiteDB.getInstance(mActivity).deleteDB();
                if (num < 0) {
                    Toast.makeText(mActivity, "제거할 아이템이 없습니다.", Toast.LENGTH_SHORT).show();
                }
                mAdapter.itemLists = SQLiteDB.getInstance(mActivity).selectDB();
                mAdapter.notifyDataSetChanged();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_insert_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int INSERT_SIZE = 20;
                String[] nameArr = new String[INSERT_SIZE];
                for (int i = 0; i < nameArr.length; i++) {
                    nameArr[i] = i + "";
                }
                new InsertAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, nameArr);
            }
        });

        btn_insert_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int INSERT_SIZE = 20;
                String[] nameArr = new String[INSERT_SIZE];
                for (int i = 0; i < nameArr.length; i++) {
                    nameArr[i] = i + "";
                }
                new InsertAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, nameArr);

                ArrayList<ItemDto> items = mAdapter.itemLists;
                new UpdateAsyncTask(items).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString();
                long num = SQLiteDB.getInstance(mActivity).insertData(name);
                if (num < 0) {
                    Toast.makeText(mActivity, "데이터 추가 실패 ", Toast.LENGTH_SHORT).show();
                } else {
                    mAdapter.itemLists = SQLiteDB.getInstance(mActivity).selectDB();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public class InsertAsyncTask extends AsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... strings) {
            long result = SQLiteDB.getInstance(mActivity).insertData(strings);
            return result;
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            if (result == -1) {
                Toast.makeText(mActivity, "데이터 추가 실패 ", Toast.LENGTH_SHORT).show();
            } else {
                mAdapter.itemLists = SQLiteDB.getInstance(mActivity).selectDB();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public class UpdateAsyncTask extends AsyncTask<Void, Void, Integer> {

        private final ArrayList<ItemDto> items;

        public UpdateAsyncTask(ArrayList<ItemDto> items) {
            this.items = items;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int[] idArr = new int[items.size()];
            String[] nameArr = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                idArr[i] = items.get(i).getId();
                nameArr[i] = items.get(i).getName();
            }
            int result = SQLiteDB.getInstance(mActivity).updateDB(idArr, nameArr);
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == -1) {
                Toast.makeText(mActivity, "데이터 추가 실패 ", Toast.LENGTH_SHORT).show();
            } else {
                mAdapter.itemLists = SQLiteDB.getInstance(mActivity).selectDB();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemHolder> {
        public ArrayList<ItemDto> itemLists;

        public ListAdapter(ArrayList<ItemDto> itemLists) {
            this.itemLists = itemLists;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.db_item, null);
            return new ItemHolder(holder);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            ItemDto item = itemLists.get(position);
            holder.tvItem.setText(item.getId() + ": " + item.getName());
        }


        @Override
        public int getItemCount() {
            return itemLists.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            private final TextView tvItem;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);
                tvItem = itemView.findViewById(R.id.tv_item);
            }
        }
    }
}