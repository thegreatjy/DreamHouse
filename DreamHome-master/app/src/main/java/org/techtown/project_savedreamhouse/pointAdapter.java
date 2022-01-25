package org.techtown.project_savedreamhouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.naver.maps.map.overlay.InfoWindow;

public class pointAdapter extends InfoWindow.DefaultViewAdapter
{
    private final Context mContext;
    private final ViewGroup mParent;

    private final String House;
    private final String Money;
    private final String Addr;

    public pointAdapter(@NonNull Context context, ViewGroup parent, String house,String money,String addr)
    {
        super(context);
        mContext = context;
        mParent = parent;
        House=house;
        Money=money;
        Addr=addr;
    }

    @NonNull
    @Override
    protected View getContentView(@NonNull InfoWindow infoWindow)
    {

        View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_point, mParent, false);

        TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);
        ImageView imagePoint = (ImageView) view.findViewById(R.id.imagepoint);
        TextView txtAddr = (TextView) view.findViewById(R.id.txtaddr);
        TextView txtTel = (TextView) view.findViewById(R.id.txttel);

        txtTitle.setText(House);
        imagePoint.setImageResource(R.drawable.home);
        txtAddr.setText(Money);
        txtTel.setText(Addr);

        return view;
    }
}