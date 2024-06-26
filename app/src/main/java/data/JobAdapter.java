package data;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.careerup.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.JobLS;

// класические адаптеры, которые необходимы для взамодействия с recyclerview
public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobsViewHolder> {
    private Context context;
    private ArrayList<JobLS> jobs;
    private JobAdapter.OnJobClickListener mListener;

    public JobAdapter(Context context, ArrayList<JobLS> jobs, JobAdapter.OnJobClickListener listener) {
        this.context = context;
        this.jobs = jobs;
        this.mListener = listener;
    }


    public interface OnJobClickListener {
        void onJobClick(JobLS job);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<JobLS> getJobs() {
        return jobs;
    }

    public void setJobs(ArrayList<JobLS> jobs) {
        this.jobs = jobs;
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_item,parent,false);

        return new JobsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        JobLS currentJob = jobs.get(position);
        final JobLS job = jobs.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("JobAdapter", "Item clicked at position: " + position);
                if (mListener != null) {
                    mListener.onJobClick(job);
                } else {
                    Log.d("JobAdapter", "mListener is null");
                }
            }
        });

        String title=currentJob.getJob_title();
        String company = currentJob.getEmployer_name();
        String pictureUrl= currentJob.getEmployer_logo();
        String country = currentJob.getJob_country();
        String city = currentJob.getJob_city();
        String job_employment_type = currentJob.getJob_employment_type();
        boolean isRemote = currentJob.isJob_is_remote();

        holder.job_title.setText(title);
        holder.employer_name.setText(company);
        holder.job_city.setText(city);
        holder.job_country.setText(country);
        holder.job_employment_type.setText(job_employment_type);
        if (holder.employer_logo != null) {
            Picasso.get()
                    .load(pictureUrl)
                    .placeholder(R.drawable.placeholder_job)
                    .fit()
                    .centerInside()
                    .into(holder.employer_logo);
        }
        if(isRemote)
            holder.job_is_remote.setText("Remote");
        else
            holder.job_is_remote.setText("On-site");
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public class JobsViewHolder extends RecyclerView.ViewHolder{
        ImageView employer_logo;
        TextView job_title, employer_name, job_city, job_country, job_is_remote, job_employment_type;

        public JobsViewHolder(@NonNull View itemView) {
            super(itemView);
            employer_logo = itemView.findViewById(R.id.pictureImageView);
            job_title = itemView.findViewById(R.id.title);
            employer_name=itemView.findViewById(R.id.companyTextView);
            job_country = itemView.findViewById(R.id.country);
            job_city=itemView.findViewById(R.id.city);
            job_is_remote=itemView.findViewById(R.id.remote);
            job_employment_type = itemView.findViewById(R.id.jobEmploymentType);
        }
    }
}
