package welcome;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.watotoappgmail.watotoapp2.R;

import java.util.ArrayList;

/**
 * Created by victor on 1/13/2018.
 */

public class ChildAgeAdapter extends ArrayAdapter {

    private ArrayList<String> ages;
    private Context context;

    public ChildAgeAdapter(@NonNull Context context,ArrayList<String> ages) {
        super(context, R.layout.tab_reg_child_age);
        this.ages = ages;
        this.context = context;
    }

    @Override
    public View getView(int pos, View View, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tab_reg_child_age, viewGroup, false);
        TextView age = (TextView) view.findViewById(R.id.reg_child_age_id);
        age.setText(ages.get(pos));
        return view;
    }
}
