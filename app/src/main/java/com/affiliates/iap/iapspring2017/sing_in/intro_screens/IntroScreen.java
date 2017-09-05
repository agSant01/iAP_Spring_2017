package com.affiliates.iap.iapspring2017.sing_in.intro_screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.affiliates.iap.iapspring2017.R;

public class IntroScreen extends Fragment {
    private int position;
    public static IntroScreen newInstance(int p) {
        Bundle args = new Bundle();
        args.putInt("page", p);
        IntroScreen fragment = new IntroScreen();
        fragment.setArguments(args);
        return fragment;
    }

    int[] ids = {R.drawable.posterlist_view,
                    R.drawable.poster_details,
                    R.drawable.general_vote_cut,
                    R.drawable.company_eval_cul,
                    R.drawable.profile_edit_cut
    };

    String[] mTitle = {"List of Projects",
                        "Project Details",
                        "Voting",
                        "Project Evaluation",
                        "User Profile Edit"};
    String[] mDesc = {"The main page of the app displays a list of all projects participating in IAP",
                        "You can view the project, abstract, team members, and advisors by tapping any project on the project list.",
                        "If you are an IAP student team member of a submitted project, an advisor to submitted projects or an event guest you will be able to vote for your favorite poster and oral presentation.",
                        "If you are a company representative, instead of voting for your favorite you will be able to evaluate each project based on a specified evaluation criteria for posters and oral presentation.",
                        "You can change some of your user profile information in the edit button on the More tab."

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.position = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.intro_adaptable_view, container, false);

        TextView title = (TextView) view.findViewById(R.id.textView24);
        TextView desc = (TextView) view.findViewById(R.id.textView28);
        ImageView pic = (ImageView) view.findViewById(R.id.image_);

        title.setText(mTitle[position]);
        desc.setText(mDesc[position]);
        pic.setImageResource(ids[position]);

        return view;
    }
}
