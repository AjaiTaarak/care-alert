package taarak.com.targettrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



/**
 * Created by Admin on 03-03-2018.
 */

public class Adapter_sellerlist extends BaseAdapter {
    Context context;
    int flags;

    String[] name, id;
    LayoutInflater inflter;

    public Adapter_sellerlist(Context applicationContext, int flags, String[] name, String[] id) {
        this.context = applicationContext;
        this.flags = flags;
        this.name =name;

        this.id =id;
        inflter = (LayoutInflater.from(applicationContext));
    }




    @Override
    public int getCount() {
        return flags;
    }


    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.lv_sellerlist, null);



        TextView names = view.findViewById(R.id.txt_lv_seller_name);

        TextView status=view.findViewById(R.id.txt_lv_seller_status);




        try{

            names.setText("" + name[i]);
            status.setText(""+ id[i]);


        }catch (Exception e) {

        }
        return view;
    }
}