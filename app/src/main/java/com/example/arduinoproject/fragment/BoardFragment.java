package com.example.arduinoproject.fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arduinoproject.MainActivity;
import com.example.arduinoproject.R;
import com.example.arduinoproject.model.ReBoardModel;
import com.example.arduinoproject.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment implements MainActivity.OnBackPressedListener{
    private String room;
    private String uid;
    private UserModel userModel;

    private TextView roomview;
    private TextView nickview;

    public BoardFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        uid = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = snapshot.getValue(UserModel.class);

                room = userModel.getRoomnm();
                Log.d("room num : ", room);

                roomview.setText(room+" 호");
                nickview.setText(userModel.getUsernm()+" 님");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        roomview=(TextView)view.findViewById(R.id.item_comment_room);
        nickview=(TextView)view.findViewById(R.id.item_comment_nickname);

        // recyclerview 설정
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_reboard);
        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ReBoardModel> boards = new ArrayList<>();

        public  RecyclerViewAdapter(){
            FirebaseDatabase.getInstance().getReference().child("post").orderByChild("room").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boards.clear();

                    for(DataSnapshot item:snapshot.getChildren()){
                        Log.d("snapshot : ", item.getValue().toString());
                        if(item.getValue(ReBoardModel.class).room.equals(room)){
                            boards.add(item.getValue(ReBoardModel.class));
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reboard, parent, false);

            return new BoardFragment.RecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Bundle bundle = new Bundle();

            String state0 = "물품 수령대기중 입니다";
            String state1 = "물품을 수령하였습니다";
            String state2 = "시스템 에러";
            BoardFragment.RecyclerViewAdapter.CustomViewHolder customViewHolder = (BoardFragment.RecyclerViewAdapter.CustomViewHolder)holder;
            customViewHolder.cabinet.setText(boards.get(position).cabinet + " 번 보관함");
            if(boards.get(position).state.equals("0")){customViewHolder.state.setText(state0);}
            else if(boards.get(position).state.equals("1")){customViewHolder.state.setText(state1);}
            else customViewHolder.state.setText(state2);
            customViewHolder.time.setText(boards.get(position).time);

        }

        @Override
        public int getItemCount() { return boards.size(); }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView cabinet;
            public TextView state;
            public TextView time;
            public CustomViewHolder(View view) {
                super(view);

                cabinet=(TextView)view.findViewById(R.id.reboardItem_textView_cabinet);
                state=(TextView)view.findViewById(R.id.reboardItem_textView_state);
                time=(TextView)view.findViewById(R.id.reboardItem_textView_time);
            }
        }
    }
    @Override
    public void onBack() {
        Log.e("Other", "onBack()");
        // 리스너를 설정하기 위해 Activity 를 받아옵니다.
        MainActivity activity = (MainActivity)getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnBackPressedListener(null);
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Other", "onAttach()");
        ((MainActivity)context).setOnBackPressedListener(this);
    }
}
