package com.dignityhealth.myhome.features.enrollment.sq;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dignityhealth.myhome.R;
import com.dignityhealth.myhome.databinding.AdapterSqListBinding;
import com.dignityhealth.myhome.databinding.FragmentSecqListBinding;
import com.dignityhealth.myhome.utils.Constants;

/*
 * Fragment dialog to display the security questions.
 *
 * Created by cmajji on 4/26/17.
 */
public class SQListDialog extends DialogFragment {

    private FragmentSecqListBinding binding;
    private String[] questionIds;
    private String[] questions;

    public SQListDialog() {
        // Required empty public constructor
    }

    public static SQListDialog newInstance() {
        SQListDialog fragment = new SQListDialog();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_secq_list,
                container, false);
        getResourceQuestions();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.sqList.setLayoutManager(layoutManager);
        binding.sqList.setAdapter(new QuestionListAdapter());

        binding.setHandlers(new ListCloseEvent());
        return binding.getRoot();
    }

    public class ListCloseEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.question_view:
                    int tag = (int) view.getTag();

                    if (tag >= 0 && tag <= questions.length)
                        setResult(questionIds[tag], questions[tag]);

                    break;
                case R.id.sq_list_close:
                    dismiss();
                    break;
            }
        }
    }

    private void setResult(String questionId, String questionText) {
        Intent intent = new Intent();

        intent.putExtra(Constants.ENROLLMENT_QUESTION_ID, questionId);
        intent.putExtra(Constants.ENROLLMENT_QUESTION, questionText);

        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }

    private class QuestionListAdapter extends
            RecyclerView.Adapter<QuestionListAdapter.ListViewHolder> {

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater =
                    LayoutInflater.from(parent.getContext());

            AdapterSqListBinding itemBinding =
                    AdapterSqListBinding.inflate(layoutInflater, parent, false);

            itemBinding.setHandlers(new ListCloseEvent());
            return new ListViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            holder.bind(questions[position]);
        }

        @Override
        public int getItemCount() {
            return questions.length;
        }

        public class ListViewHolder extends RecyclerView.ViewHolder {

            private final AdapterSqListBinding binding;

            ListViewHolder(AdapterSqListBinding itemView) {
                super(itemView.getRoot());

                binding = itemView;
            }

            public void bind(String item) {
                binding.questionView.setText(item);
                binding.questionView.setTag(getAdapterPosition());
                binding.executePendingBindings();
            }
        }
    }

    private void getResourceQuestions() {
        questionIds = getResources().getStringArray(R.array.security_question_ids);
        questions = getResources().getStringArray(R.array.security_question_text);
    }
}
