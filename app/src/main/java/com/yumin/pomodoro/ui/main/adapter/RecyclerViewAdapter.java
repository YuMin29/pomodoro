//package com.yumin.pomodoro.ui.main.adapter;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.DiffUtil;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.yumin.pomodoro.R;
//import com.yumin.pomodoro.data.model.AdjustMissionItem;
//import com.yumin.pomodoro.data.model.Mission;
//import com.yumin.pomodoro.databinding.MissionItemViewBinding;
//import com.yumin.pomodoro.ui.main.viewmodel.AddMissionViewModel;
//import com.yumin.pomodoro.utils.LogUtil;
//
//public class RecyclerViewAdapter extends RecyclerViewBaseAdapter<AdjustMissionItem, MissionItemViewBinding>{
//    private static final String TAG = "[RecyclerViewAdapter]";
//    AddMissionViewModel addMissionViewModel;
//
//    public RecyclerViewAdapter(Context context, AddMissionViewModel addMissionViewModel) {
//        super(new DiffUtil.ItemCallback<AdjustMissionItem>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull AdjustMissionItem oldItem, @NonNull AdjustMissionItem newItem) {
//                return oldItem.equals(newItem);
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull AdjustMissionItem oldItem, @NonNull AdjustMissionItem newItem) {
//                return oldItem.getAdjustItem().equals(newItem.getAdjustItem());
//            }
//        },context);
//
//        this.addMissionViewModel = addMissionViewModel;
//
//        setOnItemClickListener(new RecyclerViewBaseAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(Object binding, Object item, int position) {
//                LogUtil.logD(TAG,"[onItemClick] position = "+position);
//                AdjustMissionItem adjustMissionItem = (AdjustMissionItem)item;
//                AdjustMissionItem.AdjustItem adjustItem = adjustMissionItem.getAdjustItem();
//                if (isAdjustNumber(adjustItem)) {
//                    // new a dialog to set count here
//                    final String[] editNum = {""};
//                    LayoutInflater layoutInflater = LayoutInflater.from(context);
//                    View view1 = layoutInflater.inflate(R.layout.dialog_count,null);
//                    final EditText editText = view1.findViewById(R.id.editText);
//                    editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
//                    editText.setText(adjustMissionItem.getContent());
//                    editText.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
//                            editNum[0] = s.toString();
//                        }
//                    });
//                    AlertDialog alertDialog = new AlertDialog.Builder(context)
//                            .setTitle("設置"+ adjustMissionItem.getDesc())
//                            .setView(view1)
//                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    adjustMissionItem.setContent(editNum[0]);
//                                    notifyDataSetChanged();
//                                }
//                            })
//                            .setNegativeButton("cancel", null).show();
//                    alertDialog.show();
//                } else if (isAdjustBoolean(adjustItem)) {
//                    if (adjustMissionItem.getShowIcon()) {
//                        boolean isEnabled = adjustMissionItem.getEnabledIcon();
//                        adjustMissionItem.setEnabledIcon(isEnabled ? false : true);
//                        notifyDataSetChanged();
//                    }
//                } else if (isAdjustOption(adjustItem)) {
//                    // get define options to adjust
//                } else if (isAdjustDay(adjustItem)) {
//                    // show a calendar to adjust day
//                }
//            }
//        });
//    }
//
//    private boolean isAdjustNumber(AdjustMissionItem.AdjustItem adjustItem){
//        return adjustItem == AdjustMissionItem.AdjustItem.TIME || adjustItem == AdjustMissionItem.AdjustItem.LONG_BREAK ||
//                adjustItem == AdjustMissionItem.AdjustItem.SHORT_BREAK || adjustItem == AdjustMissionItem.AdjustItem.GOAL ||
//                adjustItem == AdjustMissionItem.AdjustItem.REPEAT;
//    }
//
//    private boolean isAdjustBoolean(AdjustMissionItem.AdjustItem adjustItem){
//        return adjustItem == AdjustMissionItem.AdjustItem.NOTIFICATION || adjustItem == AdjustMissionItem.AdjustItem.SCREEN_ON ||
//                adjustItem == AdjustMissionItem.AdjustItem.VIBRATE || adjustItem == AdjustMissionItem.AdjustItem.SOUND;
//    }
//
//    private boolean isAdjustOption(AdjustMissionItem.AdjustItem adjustItem){
//        return adjustItem == AdjustMissionItem.AdjustItem.COLOR || adjustItem == AdjustMissionItem.AdjustItem.VOLUME;
//    }
//
//    private boolean isAdjustDay(AdjustMissionItem.AdjustItem adjustItem){
//        return adjustItem == AdjustMissionItem.AdjustItem.OPERATE_DAY;
//    }
//
//    @Override
//    protected int getLayoutResId(int viewType) {
//        return R.layout.mission_item_view;
//    }
//
//    @Override
//    protected void onBindItem(MissionItemViewBinding binding, AdjustMissionItem item, RecyclerView.ViewHolder holder) {
//        binding.setItem(item);
//        binding.addNum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtil.logD(TAG,"[onAddClick] position = "+holder.getAdapterPosition());
//                switch (item.getAdjustItem()) {
//                    case TIME:
//                    case SHORT_BREAK:
//                    case LONG_BREAK:
//                    case GOAL:
//                    case REPEAT:
//                        int content = Integer.valueOf(item.getContent());
//                        LogUtil.logD(TAG,"[onBindItem] "+item.getAdjustItem().name()+" ,content = "+content);
//                        content++;
//                        item.setContent(String.valueOf(content));
//                        notifyDataSetChanged();
//                        break;
//                }
//            }
//        });
//
//        binding.minusNum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtil.logD(TAG,"[onMinusClick] position = "+holder.getAdapterPosition());
//                switch (item.getAdjustItem()) {
//                    case TIME:
//                    case SHORT_BREAK:
//                    case LONG_BREAK:
//                    case GOAL:
//                    case REPEAT:
//                        int content = Integer.valueOf(item.getContent());
//                        LogUtil.logD(TAG,"[onBindItem] "+item.getAdjustItem().name()+" ,content = "+content);
//                        if (content > 0 )content--;
//                        item.setContent(String.valueOf(content));
//                        notifyDataSetChanged();
//                        break;
//                }
//            }
//        });
//    }
//}
