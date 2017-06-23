package com.prokarma.myhome.features.enrollment.sq;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prokarma.myhome.R;
import com.prokarma.myhome.databinding.ActivitySecqListBinding;
import com.prokarma.myhome.databinding.AdapterSqListBinding;
import com.prokarma.myhome.features.enrollment.EnrollmentRequest;
import com.prokarma.myhome.utils.Constants;

/*
 * Fragment dialog to display the security questions.
 *
 * Created by cmajji on 4/26/17.
 */
public class SQListActivity extends AppCompatActivity {

    private EnrollmentRequest enrollmentRequest;
    private ActivitySecqListBinding binding;
    private String[] questionIds;
    private String[] questions;

    /*
 * Get an intent for SQActivity activity.
 */
    public static Intent getSQListActivityIntent(Context context) {

        return new Intent(context, SQListActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_secq_list);
        getResourceQuestions();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.sqList.setLayoutManager(layoutManager);
        binding.sqList.setAdapter(new QuestionListAdapter());

        enrollmentRequest = (EnrollmentRequest) getIntent()
                .getParcelableExtra(Constants.ENROLLMENT_REQUEST);

        Toolbar appToolbar = (Toolbar) findViewById(R.id.toolbarWhite);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650, getTheme()));
        } else {
            //noinspection deprecation
            appToolbar.setTitleTextColor(getResources().getColor(R.color.md_blue_grey_650));
        }
        setSupportActionBar(appToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.setHandlers(new ListCloseEvent());
    }

    public class ListCloseEvent {

        public void onClickEvent(View view) {
            switch (view.getId()) {
                case R.id.question_view:
                    int tag = (int) view.getTag();

                    if (tag >= 0 && tag <= questions.length)
                        setResult(questionIds[tag], questions[tag]);

                    break;
            }
        }
    }

    private void setResult(String questionId, String questionText) {
        Intent intent = SQActivity.getSQActivityIntent(this);

        intent.putExtra(Constants.ENROLLMENT_QUESTION_ID, questionId);
        intent.putExtra(Constants.ENROLLMENT_QUESTION, questionText);
        intent.putExtra(Constants.ENROLLMENT_REQUEST, enrollmentRequest);
        startActivity(intent);
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
