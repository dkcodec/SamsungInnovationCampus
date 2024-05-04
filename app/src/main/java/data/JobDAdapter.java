//package data;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
//import com.example.careerup.R;
//
//import java.util.ArrayList;
//import model.JobLS;
//
//public class JobDAdapter extends RecyclerView.Adapter<JobDAdapter.ViewHolder> {
//    private Context mContext;
//    private ArrayList<JobLS> jobs;
//    private OnJobClickListener mListener;
//
//    public JobDAdapter(Context mContext, ArrayList<JobLS> jobs, OnJobClickListener listener) {
//        this.mContext = mContext;
//        this.jobs = jobs;
//        this.mListener = listener;
//    }
//
//    public interface OnJobClickListener {
//        void onJobClick(JobLS job);
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
//        final JobLS job = jobs.get(position);
//
//        holder.title.setText(job.getJob_title());
//        holder.company.setText(job.getEmployer_name());
//        holder.country.setText(job.getJob_country());
//        holder.city.setText(job.getJob_city());
//        holder.type.setText(job.getJob_employment_type());
//
//        Glide.with(mContext).load(job.getEmployer_logo()).into(holder.picture);
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onJobClick(job);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return jobs.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView picture;
//        TextView title, company, country, city, type;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            picture = itemView.findViewById(R.id.pictureImageView);
//            title = itemView.findViewById(R.id.title);
//            company = itemView.findViewById(R.id.companyTextView);
//            country = itemView.findViewById(R.id.country);
//            city = itemView.findViewById(R.id.city);
//            type = itemView.findViewById(R.id.jobEmploymentType);
//        }
//    }
//}
//
